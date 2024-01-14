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
import net.minecraft.item.SwordItem;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.Cancellable;

import java.lang.reflect.AnnotatedType;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(!stack.isEmpty() && OldCombatModClient.oldCombat()) {
            if(leftHanded) {
                ((Cancellable) ci).cancel();
            }
            if(!leftHanded && stack.getItem() instanceof SwordItem && entity.isUsingItem() && entity.getOffHandStack().getUseAction() == UseAction.BLOCK) {
                SwordBlockRender.swordBlockingFirstPerson(matrices);
            }
        }
    }

    @ModifyVariable(method = "updateHeldItems()V", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private float modifyAttackCooldown(float original) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();

        // Überprüfe, ob itemStack eine Instanz von SwordItem ist
        if (itemStack.getItem() instanceof SwordItem) {
            // Setze float f auf 1.0f
            return 1.0f;
        }

        // Andernfalls belasse den ursprünglichen Wert
        return original;
    }
}
