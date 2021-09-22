package net.fabricmc.example.entities.ProjectileEntity;

import net.fabricmc.example.ExampleMod;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderer.geo.GeoProjectilesRenderer;


public class ProjectilEntityRenderer extends GeoProjectilesRenderer<ProjectileEntity> {

    private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier(ExampleMod.MODID, "textures/entities/p1.png"));


    public ProjectilEntityRenderer(EntityRenderDispatcher renderManager,AnimatedGeoModel<ProjectileEntity> modelProvider) {
        super(renderManager, modelProvider);
      
    }

    @Override
    public RenderLayer getRenderType(ProjectileEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
            Identifier textureLocation) {
    
        return SKIN;
    }

    @Override
    public void render(GeoModel model, ProjectileEntity animatable, float partialTicks, RenderLayer type,MatrixStack matrixStackIn, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
            int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {     

        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, 50,packedOverlayIn, red, green, blue, alpha);
    }
     
    @Override
    protected int getBlockLight(ProjectileEntity entity, BlockPos pos) {
        return 0;
    }
  
}

