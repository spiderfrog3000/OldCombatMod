package com.spiderfrog.oldcombatmod.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class SwordBlockRender {

    public static void swordBlockingFirstPerson(MatrixStack matrices) {
        matrices.translate(-0.15f, 0.16f, 0.15f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-18.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(82.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(112.0F));
    }

    public static void swordSwingWhileBlocking(LivingEntity player) {
        HitResult blockhit = MinecraftClient.getInstance().crosshairTarget;
        if(blockhit.getType() == HitResult.Type.BLOCK && MinecraftClient.getInstance().options.attackKey.isPressed()) {
            player.swingHand(Hand.MAIN_HAND, false);
        }
    }

    public static void transformThirdPersonSwordBlocking(MatrixStack matrices, LivingEntity entity) {
        matrices.translate(-0.22f, -0.07f, 0.10f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(25));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(30));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(70));
        if(entity.isInSneakingPose()) {
        }

    }
}
