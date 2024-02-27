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
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.Cancellable;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {
    @Shadow
    private MinecraftClient client;
    @Shadow
    private ItemStack offHand;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    public void renderItemReplacement(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(!stack.isEmpty() && OldCombatModClient.oldCombat()) {
            if(leftHanded && stack != null && stack.getItem() instanceof ShieldItem) {
                ((Cancellable) ci).cancel();
            }
            if(!leftHanded && stack != null && stack.getItem() instanceof SwordItem && OldCombatModClient.isPlayerSwordblocking(entity) && entity.isUsingItem() && entity.getOffHandStack().getUseAction() == UseAction.BLOCK && renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
                SwordBlockRender.swordBlockingFirstPerson(matrices);
            }
            SwordBlockRender.swordSwingWhileBlocking(entity);
        }
    }

    @ModifyVariable(method = "updateHeldItems()V", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private float modifyAttackCooldown(float original) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();

        if (itemStack.getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return original;
    }

    /**
     * @author spiderfrog
     * @reason  fix sword position while blockhitting
     */
    @Overwrite
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!player.isUsingSpyglass()) {
            boolean bl = hand == Hand.MAIN_HAND;
            Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
            matrices.push();
            if (item.isEmpty()) {
                if (bl && !player.isInvisible()) {
                    this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
                }
            } else if (item.isOf(Items.FILLED_MAP)) {
                if (bl && this.offHand.isEmpty()) {
                    this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
                } else {
                    this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
                }
            } else {
                boolean bl2;
                float f;
                float g;
                float h;
                float j;
                if (item.isOf(Items.CROSSBOW)) {
                    bl2 = CrossbowItem.isCharged(item);
                    boolean bl3 = arm == Arm.RIGHT;
                    int i = bl3 ? 1 : -1;
                    if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        matrices.translate((float)i * -0.4785682F, -0.094387F, 0.05731531F);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 65.3F));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * -9.785F));
                        f = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
                        g = f / (float)CrossbowItem.getPullTime(item);
                        if (g > 1.0F) {
                            g = 1.0F;
                        }

                        if (g > 0.1F) {
                            h = MathHelper.sin((f - 0.1F) * 1.3F);
                            j = g - 0.1F;
                            float k = h * j;
                            matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
                        }

                        matrices.translate(g * 0.0F, g * 0.0F, g * 0.04F);
                        matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
                        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * 45.0F));
                    } else {
                        f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
                        g = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F);
                        h = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
                        matrices.translate((float)i * f, g, h);
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        this.applySwingOffset(matrices, arm, swingProgress);
                        if (bl2 && swingProgress < 0.001F && bl) {
                            matrices.translate((float)i * -0.641864F, 0.0F, 0.0F);
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 10.0F));
                        }
                    }

                    this.renderItem(player, item, bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
                } else {
                    bl2 = arm == Arm.RIGHT;
                    int l;
                    float m;
                    if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                        l = bl2 ? 1 : -1;
                        switch (item.getUseAction()) {
                            case NONE:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                break;
                            case EAT:
                            case DRINK:
                                this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item);
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                break;
                            case BLOCK:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                break;
                            case BOW:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                matrices.translate((float)l * -0.2785682F, 0.18344387F, 0.15731531F);
                                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3F));
                                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785F));
                                m = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
                                f = m / 20.0F;
                                f = (f * f + f * 2.0F) / 3.0F;
                                if (f > 1.0F) {
                                    f = 1.0F;
                                }

                                if (f > 0.1F) {
                                    g = MathHelper.sin((m - 0.1F) * 1.3F);
                                    h = f - 0.1F;
                                    j = g * h;
                                    matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
                                }

                                matrices.translate(f * 0.0F, f * 0.0F, f * 0.04F);
                                matrices.scale(1.0F, 1.0F, 1.0F + f * 0.2F);
                                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0F));
                                break;
                            case SPEAR:
                                this.applyEquipOffset(matrices, arm, equipProgress);
                                matrices.translate((float)l * -0.5F, 0.7F, 0.1F);
                                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3F));
                                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785F));
                                m = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
                                f = m / 10.0F;
                                if (f > 1.0F) {
                                    f = 1.0F;
                                }

                                if (f > 0.1F) {
                                    g = MathHelper.sin((m - 0.1F) * 1.3F);
                                    h = f - 0.1F;
                                    j = g * h;
                                    matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
                                }

                                matrices.translate(0.0F, 0.0F, f * 0.2F);
                                matrices.scale(1.0F, 1.0F, 1.0F + f * 0.2F);
                                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0F));
                                break;
                            case BRUSH:
                                this.applyBrushTransformation(matrices, tickDelta, arm, item, equipProgress);
                        }
                    } else if (player.isUsingRiptide()) {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        l = bl2 ? 1 : -1;
                        matrices.translate((float)l * -0.4F, 0.8F, 0.3F);
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 65.0F));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -85.0F));
                    } else {
                        float n = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 3.1415927F);
                        m = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.2831855F);
                        f = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
                        int o = bl2 ? 1 : -1;
                        if(OldCombatModClient.isPlayerSwordblocking(player)) {

                        } else {
                            matrices.translate((float)o * n, m, f);
                        }
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        this.applySwingOffset(matrices, arm, swingProgress);
                    }

                    this.renderItem(player, item, bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
                }
            }

            matrices.pop();
        }
    }

    @Shadow
    private void renderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
    }

    @Shadow
    private void renderMapInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack item) {
    }

    @Shadow
    private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack item) {
    }

    @Shadow
    private void applyBrushTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack item, float equipProgress) {
    }

    @Shadow
    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
    }

    @Shadow
    private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm) {
    }

    @Shadow
    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
    }

    @Shadow
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }
}
