package com.spiderfrog.oldcombatmod.mixin.render;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import com.spiderfrog.oldcombatmod.utils.SwordBlockRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.Cancellable;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    public void renderItemReplacement(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(!stack.isEmpty() && OldCombatModClient.oldCombat()) {
            if(leftHanded && stack != null && stack.getItem() instanceof ShieldItem) {
                ((Cancellable) ci).cancel();
            }
            if(!leftHanded && stack != null && stack.getItem() instanceof SwordItem && OldCombatModClient.isPlayerSwordblocking(entity) && entity.isUsingItem() && entity.getOffHandStack().getUseAction() == UseAction.BLOCK && renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
                SwordBlockRender.swordBlockingFirstPerson(matrices);
            }
            SwordBlockRender.swordSwingWhileBlocking();
        }
    }

    @ModifyVariable(method = "updateHeldItems()V", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private float modifyAttackCooldown(float original) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();

        if (OldCombatModClient.oldCombat() && itemStack.getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return original;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), method = "renderFirstPersonItem")
    private void fixFirstPersonBlocking(MatrixStack instance, float x, float y, float z) {
        if(OldCombatModClient.isPlayerSwordblocking(client.player)) {

        } else {
            instance.translate(x, y, z);
        }
    }
}
