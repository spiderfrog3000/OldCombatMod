package com.spiderfrog.oldcombatmod.mixin.entity;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    /**
     * @author spiderfrog
     * @reason switching blockposition from off to main hand
     */
    @Overwrite
    private void setModelPose(AbstractClientPlayerEntity player) {
        PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = (PlayerEntityModel)this.getModel();
        if (player.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        } else {
            playerEntityModel.setVisible(true);
            playerEntityModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            playerEntityModel.jacket.visible = player.isPartVisible(PlayerModelPart.JACKET);
            playerEntityModel.leftPants.visible = player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            playerEntityModel.rightPants.visible = player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            playerEntityModel.leftSleeve.visible = player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            playerEntityModel.rightSleeve.visible = player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.sneaking = player.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = getArmPose(player, Hand.OFF_HAND);

            if (armPose.isTwoHanded()) {
                armPose2 = player.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (player.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }

            if(OldCombatModClient.oldCombat() && player.getOffHandStack().getItem() instanceof ShieldItem) {
                playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
            }
            if(OldCombatModClient.isPlayerSwordblocking(player)) {
                playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.BLOCK;
            }
        }

    }

    // Füge diese Methode hinzu, um die nicht implementierten Methoden zu behandeln
    @Shadow
    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        // Hier implementiere die Logik, um die ArmPose für die Hände zu erhalten
        // Du kannst die bestehende Logik aus der Originalmethode übernehmen oder anpassen
        return BipedEntityModel.ArmPose.EMPTY;
    }

    @Shadow
    public Identifier getTexture(AbstractClientPlayerEntity entity) {
        return null;
    }
}
