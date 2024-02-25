package com.spiderfrog.oldcombatmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;

public class OldCombatModClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
    }

    public static boolean oldCombat() {
        return true;
    }
    public static boolean oldEatSwing() {
        return true;
    }

    public static boolean isPlayerSwordblocking(LivingEntity player) {
        ItemStack offhandItem = player.getStackInHand(Hand.OFF_HAND);
        ItemStack mainHandItem = player.getStackInHand(Hand.MAIN_HAND);
        if(oldCombat() && offhandItem.getItem() instanceof ShieldItem && mainHandItem.getItem() instanceof SwordItem && player.getItemUseTime() > 0 && player.getOffHandStack().getUseAction() == UseAction.BLOCK) {
            return true;
        }
        return false;
    }
}
