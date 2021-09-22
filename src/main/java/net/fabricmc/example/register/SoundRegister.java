package net.fabricmc.example.register;


import net.fabricmc.example.ExampleMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundRegister {
    
    //turret fire audio
    public static final Identifier LASER_MONO_EVENT_ID = new Identifier(ExampleMod.MODID, "laser_mono");
    public static SoundEvent LASER_MONO_EVENT = new SoundEvent(LASER_MONO_EVENT_ID);

    public static void init(){
        Registry.register(Registry.SOUND_EVENT, LASER_MONO_EVENT_ID, LASER_MONO_EVENT);
    }
}
