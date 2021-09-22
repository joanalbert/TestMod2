package net.fabricmc.example.entities.TestEntity;



import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;

import java.util.HashMap;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.entities.ProjectileEntity.ProjectileEntity;
import net.fabricmc.example.register.ParticleRegister;
import net.fabricmc.example.register.SoundRegister;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.model.Texture;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;


/*
 * Our TestEntity extends PathAwareEntity, which extends MobEntity, which extends LivingEntity.
 * 
 * LlivingEntity has health and can deal damage
 * MobEntity has movement controls and AI capabilities
 * PathAwareEntity has pathfinding favor and slightly tweaked leash behavior.
*/
public class TestEntity extends PathAwareEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this); // <- this is needed for geckolib
    public static final float TURRET_RANGE = 35f;

    //animation controller
    AnimationController main;
    AnimationController radar;

    //static field in which turretData from all instances of this entity is stored, then recovered
    public static HashMap<String, TurretData> turretInfo = new HashMap<String, TurretData>();

    //turret data of each instance
    Vec3d target;         
    Vec3d headRotations;  
    Vec3d headPosition;   
    Vec3d nozzlePosition; 

    public TestEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }

    //when this turret dies, remove it's entry from turret info
    @Override
    public void onDeath(DamageSource source) {
        turretInfo.remove(this.getUuidAsString());
        super.onDeath(source);
    }

    //so that the entity doesn't get randomly rotated upon egg spawn
    @Override
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        super.refreshPositionAndAngles(x, y, z, 0f, 0f);
    }
          

    //METHODS FOR ANIMATION////////////////////////////
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event){
        if(target == null)event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));  
        else event.getController().setAnimation(new AnimationBuilder().addAnimation("shoot"));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event){
        event.getController().setAnimation(new AnimationBuilder().addAnimation("radar")); 
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        //set turret data for this entity
        this.target         = TurretDataHelper.getTarget(this.getUuidAsString());
        this.headRotations  = TurretDataHelper.getHeadRotation(this.getUuidAsString());
        this.headPosition   = TurretDataHelper.getHeadPosition(this.getUuidAsString());
        this.nozzlePosition = TurretDataHelper.getNozzlePosition(this.getUuidAsString()); 

        if(target != null && !world.isClient && world.getTime() % 4 == 0) shoot();
        super.tick();
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
    @Override
    public void registerControllers(AnimationData data) {
       this.main  = data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));     
       this.radar = data.addAnimationController(new AnimationController<>(this, "radar", 0, this::predicate2));  
    }
    ///////////////////////////////////////////////////

    public static void prepareReceiver(){
		ServerPlayNetworking.registerGlobalReceiver(new Identifier(ExampleMod.MODID, "bone_packet"), (server, player, handler, buf, responseSender)->{
            
            //head rotation
            float x = (float) (buf.readFloat() * 180 / Math.PI);
            float y = (float) (buf.readFloat() * 180 / Math.PI);
            float z = (float) (buf.readFloat() * 180 / Math.PI);
            Vec3d headRotations = new Vec3d(-x,-y,z);

            //pos
            double px = buf.readDouble();
            double py = buf.readDouble();
            double pz = buf.readDouble();
            Vec3d headPosition = new Vec3d(px,py,pz);

            //nozle
            float nx = buf.readFloat();
            float ny = buf.readFloat();
            float nz = buf.readFloat();
            Vec3d nozlePosition = new Vec3d(nx,ny,nz);

            //target
            double tx = buf.readDouble();
            double ty = buf.readDouble();
            double tz = buf.readDouble();
            Vec3d target = new Vec3d(tx, ty, tz);

            if( Double.isNaN(target.x) || Double.isNaN(target.y) || Double.isNaN(target.z) ) target = null;

            String turretID = buf.readString(50);
            TurretData data = new TurretData(headPosition, headRotations, nozlePosition, target);

            turretInfo.put(turretID, data);
		});
	}

    public void shoot(){
        if(headRotations == null || headPosition == null || target == null ) return;
        if(!world.isClient){
            playShootSound();
            shootCustomProjectile();
            //shootFireBall();
        }
    }

    private void playShootSound(){
        world.playSound(null, new BlockPos(this.getPos()), SoundRegister.LASER_MONO_EVENT, SoundCategory.AMBIENT, 1f, 1f);
    } 

    private void shootCustomProjectile(){
               
           
        ProjectileEntity myProjectile = new ProjectileEntity(world, this);
        myProjectile.setPosition( headPosition.x, headPosition.y, headPosition.z);
        myProjectile.setProperties(this, (float)headRotations.x, (float)headRotations.y, 0f, 4.3f, 0f);
        myProjectile.removed = false;

    
        Boolean s = world.spawnEntity(myProjectile);
        if(!s) System.out.println("no shot");
    }

    private void shootFireBall(){
        FireballEntity myProjectile = new FireballEntity(EntityType.FIREBALL, world);
        myProjectile.setPosition(headPosition.getX(), headPosition.getY(), headPosition.getZ());
        myProjectile.setProperties(this, (float)headRotations.x, (float)headRotations.y, 0f, 1.5f, 0f);
        world.spawnEntity(myProjectile);
    }

    private abstract class TurretDataHelper{

        public static Vec3d getTarget(String turretID){
            TurretData data = turretInfo.get(turretID);
            Vec3d target = (data != null)? data.target : null;
            return target;
        }

        public static Vec3d getHeadPosition(String turretID){
            TurretData data = turretInfo.get(turretID);
            Vec3d pos = (data != null)? data.headPosition : null;
            return pos;
        }

        public static Vec3d getHeadRotation(String turretID){
            TurretData data = turretInfo.get(turretID);
            Vec3d rot = (data != null)? data.headRotations : null;
            return rot;
        }

        public static Vec3d getNozzlePosition(String turretID){
            TurretData data = turretInfo.get(turretID);
            Vec3d pos = (data != null)? data.nozzlePosition : null;
            return pos;
        }
    }

    public void packetToClient_Example(){
        PlayerEntity player = this.world.getClosestPlayer(this, TURRET_RANGE); 
        //destination, identifier, payload
        if(player instanceof ServerPlayerEntity) ServerPlayNetworking.send((ServerPlayerEntity)player, new Identifier(ExampleMod.MODID, "test_packet"), PacketByteBufs.empty());  
    }
     
    private Vec3d directionLook(double d, double e){
        double radYaw   = d / 180 * Math.PI;
        double radPitch = e / 180 * Math.PI;

        double x = -Math.sin(radYaw) * Math.cos(radPitch);
        double y = -Math.sin(radPitch);
        double z =  Math.cos(radYaw) * Math.cos(radPitch);
        
        return new Vec3d(x, y, z).normalize();
    }

   
}
