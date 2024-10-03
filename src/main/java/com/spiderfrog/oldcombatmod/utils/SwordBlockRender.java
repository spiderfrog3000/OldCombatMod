package com.spiderfrog.oldcombatmod.utils;

import com.spiderfrog.oldcombatmod.OldCombatMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;

public class SwordBlockRender {
    public static MinecraftClient minecraft = MinecraftClient.getInstance();

    public static void swordBlockingFirstPerson(MatrixStack matrices) {
        matrices.translate(-0.15f, 0.16f, 0.15f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-18.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(82.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(112.0F));
    }

    public static void swordSwingWhileBlocking() {
        LivingEntity player = minecraft.player;
        HitResult blockhit = minecraft.crosshairTarget;
        if(blockhit != null && blockhit.getType() == HitResult.Type.BLOCK && minecraft.options.attackKey.isPressed() && minecraft.options.useKey.isPressed()) {
            assert player != null;
            if (player.getMainHandStack().getItem() instanceof SwordItem && player.getOffHandStack().getItem() instanceof ShieldItem) {
                player.swingHand(Hand.MAIN_HAND, false);
            }
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

    public static boolean swingWhileEating() {
        LivingEntity player = minecraft.player;
        HitResult blockhit = minecraft.crosshairTarget;
        if(blockhit != null && blockhit.getType() == HitResult.Type.BLOCK && minecraft.options.attackKey.isPressed() && minecraft.options.useKey.isPressed()) {
            assert player != null;
            if ((player.getMainHandStack().getItem() instanceof MilkBucketItem) && player.getOffHandStack().getItem() instanceof ShieldItem) {
                return true;
            }
        }
        return false;
    }
}
