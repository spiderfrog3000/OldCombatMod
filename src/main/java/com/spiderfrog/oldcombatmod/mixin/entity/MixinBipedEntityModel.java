package com.spiderfrog.oldcombatmod.mixin.entity;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class MixinBipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow
    public final ModelPart head;
    @Shadow
    public final ModelPart leftArm;
    @Shadow
    public final ModelPart rightArm;

    public MixinBipedEntityModel(ModelPart head, ModelPart leftArm, ModelPart rightArm) {
        this.head = head;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    /**
     * @author spiderfrog
     * @reason adding hand blocking position
     */
    @Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
    private void positionRightArmFix(T entity, CallbackInfo ci) {
        if(OldCombatModClient.isPlayerSwordblocking(entity)) {
            this.positionSwordBlocking(this.rightArm, true);
            ci.cancel();
        }
    }

    private void positionSwordBlocking(ModelPart arm, boolean rightArm) {
        arm.pitch = arm.pitch * 0.5F - 0.9424779F + MathHelper.clamp(this.head.pitch, -1.3962634F, 0.43633232F);
        arm.yaw = (rightArm ? -30.0F : 30.0F) * 0.017453292F + MathHelper.clamp(this.head.yaw, -0.5235988F, 0.5235988F);
    }

    @Shadow
    protected Iterable<ModelPart> getHeadParts() {
        return null;
    }

    @Shadow
    protected Iterable<ModelPart> getBodyParts() {
        return null;
    }

    @Shadow
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Shadow
    public void setArmAngle(Arm arm, MatrixStack matrices) {

    }

    @Shadow
    public ModelPart getHead() {
        return null;
    }
}