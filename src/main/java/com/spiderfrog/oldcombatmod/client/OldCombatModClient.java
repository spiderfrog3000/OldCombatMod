package com.spiderfrog.oldcombatmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.UseAction;

public class OldCombatModClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
    }

    public static boolean oldCombat() {
        return true;
    }

    public static boolean isPlayerSwordblocking(LivingEntity player) {
        Item offhandItem = player.getOffHandStack().getItem();
        Item mainHandItem = player.getMainHandStack().getItem();
        if(oldCombat() && offhandItem instanceof ShieldItem && player.isUsingItem() && mainHandItem instanceof SwordItem) {
            return true;
        }
        return false;
    }
}
