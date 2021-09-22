package net.fabricmc.example.entities.TestEntity;


import net.fabricmc.example.ExampleMod;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

/*
 * A renderer is used to provide an entity model, shadow size and texture
 */
public class TestEntityRenderer extends GeoEntityRenderer<TestEntity> {

    
    public TestEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TestEntityModel());
        this.shadowRadius = 0f;
    }

    @Override
    public void render(GeoModel model, TestEntity animatable, float partialTicks, RenderLayer type,
                    MatrixStack matrixStackIn, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                    int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

 

        //GeoBone head = model.getBone("turret_head").get();
        //PlayerEntity player = animatable.world.getClosestPlayer(animatable, 100);
        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
    
}
