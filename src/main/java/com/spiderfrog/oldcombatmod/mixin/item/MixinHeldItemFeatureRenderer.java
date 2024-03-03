package com.spiderfrog.oldcombatmod.mixin.item;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import com.spiderfrog.oldcombatmod.utils.SwordBlockRender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public class MixinHeldItemFeatureRenderer {

    @Inject(method = "renderItem", at = @At(value = "INVOKE", target = "net/minecraft/client/render/item/HeldItemRenderer.renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void renderItemFix(LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        boolean bool = arm == Arm.LEFT;
        if(!bool && OldCombatModClient.isPlayerSwordblocking(entity)) {
            SwordBlockRender.transformThirdPersonSwordBlocking(matrices, entity);
        }
    }
}
