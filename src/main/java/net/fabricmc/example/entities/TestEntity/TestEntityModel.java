package net.fabricmc.example.entities.TestEntity;


import java.util.List;
import java.util.Vector;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.register.EntityRegister;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class TestEntityModel extends AnimatedGeoModel<TestEntity> {

    @Override
    public Identifier getModelLocation(TestEntity object) {
        return new Identifier(ExampleMod.MODID, "geo/test.geo.json");
    }

    @Override
    public Identifier getTextureLocation(TestEntity object) {
        return new Identifier(ExampleMod.MODID, "textures/entities/test_entity.png");
    }



    @Override
    public Identifier getAnimationFileLocation(TestEntity animatable) {
        return new Identifier(ExampleMod.MODID, "animations/test.animation.json");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setLivingAnimations(TestEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        IBone head            = getAnimationProcessor().getBone("turret_head"); 
        IBone cannon          = getAnimationProcessor().getBone("turret_head_cannon");
        IBone cannon_nozle    = getAnimationProcessor().getBone("cannon_nozle"); 
        
        
               
        //slimes
        List<Entity> slimes = entity.world.getEntitiesByClass(SlimeEntity.class, EntityRegister.TEST_ENTITY.getDimensions().getBoxAt(entity.getPos()).expand(TestEntity.TURRET_RANGE), null);
        
        //other hostiles
        List<Entity> targets = entity.world.getEntitiesByClass(HostileEntity.class, EntityRegister.TEST_ENTITY.getDimensions().getBoxAt(entity.getPos()).expand(TestEntity.TURRET_RANGE), null);

        targets.addAll(slimes);

        for (Entity e : targets) {
            if(e instanceof ZombieEntity){
                
            } 
        }

        Entity targetEntity = getClosestTarget(targets, entity, head);
        entity.target = (targetEntity != null)? targetEntity.getPos() : null;
        
        if(entity.target != null){
            double distance = entity.target.distanceTo(entity.getPos());
            if(distance <= TestEntity.TURRET_RANGE && targetEntity.isAlive()){
                //System.out.println("TARGET: "+TestEntity.target.getDisplayName().getString()+": "+distance);
                lookat2(targetEntity, head, entity);
                sendBonePacket(head, cannon_nozle , entity, entity.target);
                System.out.println("y rot: "+cannon_nozle.getRotationY() * 57.29f );
            }
            else{
                //System.out.println("no target");
                entity.target = null;
                sendBonePacket(head, cannon_nozle , entity, entity.target);
            }  
        }
        else{
            //System.out.println("no target");
            entity.target = null;
            sendBonePacket(head, cannon_nozle , entity, entity.target);
        }    
    }

    private void lookat2(Entity target, IBone head, TestEntity turret){
        EntityDimensions dimensions = target.getDimensions(target.getPose());
        Vec3d targetPos =  new Vec3d(target.getPos().x, target.getPos().y + dimensions.height/2, target.getPos().z); 
        Vec3d headPos = new Vec3d(turret.getPos().x, turret.getPos().y + head.getPivotY()/16, turret.getPos().z);

        double xdist = targetPos.x - headPos.x;
        double ydist = targetPos.y - headPos.y;
        double zdist = targetPos.z - headPos.z;

    
        double xzdist = Math.sqrt(xdist*xdist + zdist*zdist);
  
        head.setRotationY((float) Math.atan2(xdist, zdist));
        head.setRotationX((float) Math.atan2(ydist, xzdist));
        head.setRotationZ(0);
    }

    private void sendBonePacket(IBone head, IBone nozle, Entity turret, Vec3d target){
        PacketByteBuf data = PacketByteBufs.create();

        //orientation
        data.writeFloat(head.getRotationX());
        data.writeFloat(head.getRotationY());
        data.writeFloat(head.getRotationZ());

        //position
        data.writeDouble(turret.getPos().x);
        data.writeDouble(turret.getPos().y + head.getPivotY()/16);
        data.writeDouble(turret.getPos().z);

        //nozle position
        data.writeFloat(nozle.getPivotX() / 16);
        data.writeFloat(nozle.getPivotY() / 16);
        data.writeFloat(nozle.getPivotZ() / 16);

        //target position (If the passed target vector is null, we write the double as NaN)
        data.writeDouble((target != null) ? target.getX() : Double.NaN);
        data.writeDouble((target != null) ? target.getY() : Double.NaN);
        data.writeDouble((target != null) ? target.getZ() : Double.NaN);

        data.writeString(turret.getUuidAsString());

        ClientPlayNetworking.send(new Identifier(ExampleMod.MODID, "bone_packet"), data);
    }

    //from the list, returns the entity that is closest to the turret, that is not a turret and which is visible to the turret
    private Entity getClosestTarget(List<Entity> targets, Entity entity, IBone head){
        double minDist = Integer.MAX_VALUE;
        Entity closest = null;

        Vec3d headPos = new Vec3d(entity.getPos().x, entity.getPos().y + head.getPivotY()/16, entity.getPos().z);

        for (Entity target : targets) {
            double dist = target.getPos().squaredDistanceTo(entity.getPos());
            if(dist <= minDist && !(target instanceof TestEntity) && target.isAlive() && !linesightIsObstructed(headPos, target.getPos(), entity.world, entity, target)){
                minDist = dist;
                closest = target;
            }
        }

        return closest;
    }

    public Boolean linesightIsObstructed(Vec3d from, Vec3d to, World world, Entity entity, Entity target){
        EntityDimensions dimensions = target.getDimensions(target.getPose());
        to = new Vec3d(to.getX(), to.getY() + dimensions.height/2, to.getZ());
        
        RaycastContext ctx = new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY , entity);
        BlockHitResult result = world.raycast(ctx);
        HitResult.Type type = result.getType();

        if(type == HitResult.Type.BLOCK) return true;
        return false;
    }

       
    private double lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}

}
