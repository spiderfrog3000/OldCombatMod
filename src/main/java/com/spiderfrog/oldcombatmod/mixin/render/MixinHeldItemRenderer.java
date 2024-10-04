package com.spiderfrog.oldcombatmod.mixin.render;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import com.spiderfrog.oldcombatmod.utils.SwordBlockRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.Cancellable;

import java.util.Set;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {
    @Shadow
    private MinecraftClient client;

    @Shadow protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

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
        if(!stack.isEmpty()) {
            SwordBlockRender.swingWhileEating();
        }
    }

    @ModifyVariable(method = "updateHeldItems()V", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private float modifyAttackCooldown(float original) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();

        if (OldCombatModClient.oldCombat() && itemStack.getItem() instanceof SwordItem) {
            return 1.0f;
        }
        if(OldCombatModClient.oldEat() && (Set.of(UseAction.EAT, UseAction.DRINK).contains(itemStack.getUseAction()))) {
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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V", ordinal = 3, shift = At.Shift.AFTER), method = "renderFirstPersonItem")
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(OldCombatModClient.oldEat()) {
            this.applySwingOffset(matrices, player.getMainArm(), swingProgress);
        }
    }
}
