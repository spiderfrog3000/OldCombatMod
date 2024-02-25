package com.spiderfrog.oldcombatmod.mixin.item;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Arm;
import net.minecraft.util.UseAction;

@Mixin(HeldItemFeatureRenderer.class)
public class MixinHeldItemFeatureRenderer <T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {

    @Shadow
    private HeldItemRenderer heldItemRenderer;
    public MixinHeldItemFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    /**
     * @author spiderfrog
     * @reason removes the shield in left hand and corrects the sword position while blocking
     */
    @Overwrite
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        if (!stack.isEmpty()) {
            matrices.push();
            ((ModelWithArms)this.getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            boolean bl = arm == Arm.LEFT;
            matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            if(OldCombatModClient.isPlayerSwordblocking(entity)) {
                matrices.translate(-0.20f, -0.13f, -0.15f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(7));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-10));
                if(entity.isInSneakingPose()) {
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(10));
                }
            }
            this.heldItemRenderer.renderItem(entity, stack, transformationMode, bl, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }

    @Shadow
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    }
}
