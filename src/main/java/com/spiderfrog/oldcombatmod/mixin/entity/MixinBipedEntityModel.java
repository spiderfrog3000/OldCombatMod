package com.spiderfrog.oldcombatmod.mixin.entity;

import com.spiderfrog.oldcombatmod.client.OldCombatModClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class MixinBipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {

    @Shadow
    public ModelPart head;
    @Shadow
    public ModelPart leftArm;
    @Shadow
    public ModelPart rightArm;

    public MixinBipedEntityModel(ModelPart head, ModelPart leftArm, ModelPart rightArm) {
        this.head = head;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }

    @Inject(method = "animateArms", at = @At("HEAD"))
    protected void animateArms(T entity, float animationProgress, CallbackInfo ci) {
        if(OldCombatModClient.isPlayerSwordblocking(entity)) {
            this.positionSwordBlocking(this.rightArm);
        }
    }

    private void positionSwordBlocking(ModelPart arm) {
        arm.pitch = arm.pitch * 0.5F - 0.9424779F;
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
