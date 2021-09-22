package net.fabricmc.example.entities.ProjectileEntity;

import net.fabricmc.example.ExampleMod;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ProjectileEntityModel extends AnimatedGeoModel<ProjectileEntity> {

    @Override
    public Identifier getAnimationFileLocation(ProjectileEntity animatable) {
        return new Identifier(ExampleMod.MODID, "animations/projectile.animation.json");
    }

    @Override
    public Identifier getModelLocation(ProjectileEntity object) {
        return new Identifier(ExampleMod.MODID, "geo/projectile.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ProjectileEntity object) {
        return new Identifier(ExampleMod.MODID, "textures/entities/p1.png");
    }
    
}
