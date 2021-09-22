package net.fabricmc.example.register;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegister {
    
    public static final DefaultParticleType TEST_PARTICLE = FabricParticleTypes.simple();

    public static void init(){
            Registry.register(Registry.PARTICLE_TYPE, new Identifier(ExampleMod.MODID, "test_particle"), TEST_PARTICLE);
    }
}
