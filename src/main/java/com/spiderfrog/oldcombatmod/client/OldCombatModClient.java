package com.spiderfrog.oldcombatmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;

import java.util.Set;
import java.util.function.Consumer;

public class OldCombatModClient implements ClientModInitializer {

    private static boolean oldCombat = true;
    private static boolean oldEat = true;
    private static MinecraftClient minecraft = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
    }

    public static boolean oldCombat() {
        return oldCombat;
    }
    public static boolean oldEat() {
        return oldEat;
    }

    public static boolean isPlayerSwordblocking(LivingEntity player) {
        ItemStack offhandItem = player.getStackInHand(Hand.OFF_HAND);
        ItemStack mainHandItem = player.getStackInHand(Hand.MAIN_HAND);
        if(oldCombat() && offhandItem.getItem() instanceof ShieldItem && mainHandItem.getItem() instanceof SwordItem && player.getItemUseTime() > 0 && player.getOffHandStack().getUseAction() == UseAction.BLOCK) {
            return true;
        }
        return false;
    }

    public static boolean isPlayerEatingAndBlocking(LivingEntity player) {
        if(OldCombatModClient.oldEat() && (Set.of(UseAction.EAT, UseAction.DRINK).contains(player.getMainHandStack().getUseAction())) && minecraft.options.attackKey.isPressed() && minecraft.options.useKey.isPressed()) {
            return true;
        }
        return false;
    }

    public static void switchOldCombat() {
        oldCombat = !oldCombat;
    }

    public static void switchOldEat() {
        oldEat = !oldEat;
    }
}
