package net.fabricmc.example.client;

import java.util.UUID;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.entities.ProjectileEntity.EntitySpawnPacket;
import net.fabricmc.example.entities.ProjectileEntity.ProjectilEntityRenderer;
import net.fabricmc.example.entities.ProjectileEntity.ProjectileEntityModel;
import net.fabricmc.example.entities.SkeletonEntity.MySkeletonEntityRenderer;
import net.fabricmc.example.entities.TestEntity.TestEntityRenderer;
import net.fabricmc.example.register.EntityRegister;
import net.fabricmc.example.register.ParticleRegister;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ExampleModClient implements ClientModInitializer{

    
    
    @Override
    public void onInitializeClient() { //erd -> EntityRendererDispatcher

		//register entities
        EntityRendererRegistry.INSTANCE.register(EntityRegister.TEST_ENTITY,   (erd,context)-> new TestEntityRenderer(erd));
        EntityRendererRegistry.INSTANCE.register(EntityRegister.MY_SKELETON,   (erd,context)-> new MySkeletonEntityRenderer(erd));
        EntityRendererRegistry.INSTANCE.register(EntityRegister.MY_PROJECTILE, (erd,context)-> new ProjectilEntityRenderer(erd, new ProjectileEntityModel()));
		
		//register particles
		ParticleFactoryRegistry.getInstance().register(ParticleRegister.TEST_PARTICLE, FlameParticle.Factory::new);

        receiveEntityPacket();
		//receiveShootPacket();
    }

	//this code is only here as an example, it isn't used.
	public void receiveShootPacket(){
		ClientPlayNetworking.registerGlobalReceiver(new Identifier(ExampleMod.MODID, "test_packet"), (client, handler, buf, responseSender)-> {
			System.out.println("hii");
		});
	}
    

	//this is for the spawn egg of the sentry, IGNORE FOR NOW 
    @SuppressWarnings("deprecation")
    public void receiveEntityPacket() {
		ClientSidePacketRegistry.INSTANCE.register(ExampleMod.PacketID, (ctx, byteBuf) -> {
			EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
			UUID uuid = byteBuf.readUuid();
			int entityId = byteBuf.readVarInt();
			Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
			float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			ctx.getTaskQueue().execute(() -> {
				if (MinecraftClient.getInstance().world == null)
					throw new IllegalStateException("Tried to spawn entity in a null world!");
				Entity e = et.create(MinecraftClient.getInstance().world);
				if (e == null)
					throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
				e.updateTrackedPosition(pos);
				e.setPos(pos.x, pos.y, pos.z);
				e.pitch = pitch;
				e.yaw = yaw;
				e.setEntityId(entityId);
				e.setUuid(uuid);
				MinecraftClient.getInstance().world.addEntity(entityId, e);
			});
		});
	}
}
