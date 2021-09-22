package net.fabricmc.example.entities.ProjectileEntity;

import java.util.HashMap;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.client.ExampleModClient;
import net.fabricmc.example.entities.TestEntity.TestEntity;
import net.fabricmc.example.register.EntityRegister;
import net.fabricmc.example.register.ItemRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ProjectileEntity extends ThrownItemEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);


    public ProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
        this.ignoreCameraFrustum = true;
	}
 
	public ProjectileEntity(World world, LivingEntity owner) {
		super(EntityRegister.MY_PROJECTILE, owner, world); // null will be changed later
        this.ignoreCameraFrustum = true;
	}

    public ProjectileEntity(World world, TestEntity owner) {
		super(EntityRegister.MY_PROJECTILE, owner, world); // null will be changed later
        this.ignoreCameraFrustum = true;
	}
 
	public ProjectileEntity(World world, double x, double y, double z) {
		super(EntityRegister.MY_PROJECTILE, x, y, z, world); // null will be changed later
        this.ignoreCameraFrustum = true;
	}

    @Override
    public Packet<?> createSpawnPacket() {
        return EntitySpawnPacket.create(this, ExampleMod.PacketID);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected Item getDefaultItem() {
        return ItemRegister.MY_PROJECTILE;
    }
    
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity entity = entityHitResult.getEntity();
        float damage = 5;

        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), damage);

        if(entity instanceof LivingEntity ){ //check if entity is not a boat or minecart
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.POISON, 20 * 3, 0)));
            //entity.playSound(SoundEvents.AMBIENT_CAVE, 2f, 1f);
            ((LivingEntity) entity).setOnFireFor(2);
            this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, 1);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        
        if(!this.world.isClient){
            this.world.sendEntityStatus(this, (byte)3);
            this.remove();
        }

        this.playSound(SoundEvents.BLOCK_CHAIN_HIT, 1, 1);

        super.onCollision(hitResult);
    }

    @Override
    public void tick() {
        
        super.tick();
    }

    //METHODS FOR ANIMATION////////////////////////////
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event){                       
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate)); 
    }
}
