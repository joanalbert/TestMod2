package net.fabricmc.example;


import net.fabricmc.example.register.BlockRegister;
import net.fabricmc.example.register.EntityRegister;
import net.fabricmc.example.register.ItemRegister;
import net.fabricmc.example.register.ParticleRegister;
import net.fabricmc.example.register.SoundRegister;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.entities.TestEntity.TestEntity;
import software.bernie.geckolib3.GeckoLib;


public class ExampleMod implements ModInitializer {

	public static final String MODID = "modid";
	
	public static final Identifier PacketID = new Identifier(ExampleMod.MODID, "spawn_packet");
	
	@Override
	public void onInitialize() {

		GeckoLib.initialize();

		//registration
		ParticleRegister.init();
		SoundRegister.init();
		EntityRegister.init();
		BlockRegister.init();
		ItemRegister.init();	
		
		TestEntity.prepareReceiver();
	}

	
}
