package com.spiderfrog.oldcombatmod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("OldCombatMod"));
        this.parent = parent;
    }


    public ButtonWidget sword;
    public ButtonWidget eatDrink;

    public ButtonWidget back;

    @Override
    protected void init() {
        sword = ButtonWidget.builder(Text.literal("1.7 Sword Blockhit: " + (OldCombatModClient.oldCombat() ? "Enabled" : "Disabled")), button -> {OldCombatModClient.switchOldCombat();})
                .dimensions(width / 2 - 100, height / 2 - 25, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Enable or disable the 1.7 Sword blocking")))
                .build();
        eatDrink = ButtonWidget.builder(Text.literal("1.7 Eat & Drink: " + (OldCombatModClient.oldEat() ? "Enabled" : "Disabled")), button -> {OldCombatModClient.switchOldEat();})
                .dimensions(width / 2 - 100, height / 2, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Enable or disable the 1.7 Eating and drinking animation")))
                .build();
        back = ButtonWidget.builder(Text.literal("Back"), button -> {
                    this.client.setScreen(this.parent);
                })
                .dimensions(width / 2 - 100, height / 2 + 25, 200, 20)
                .build();

        addDrawableChild(sword);
        addDrawableChild(eatDrink);
        addDrawableChild(back);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackgroundTexture(context);
        String headline = "OldCombatMod Settings";
        context.drawCenteredTextWithShadow(this.textRenderer, headline, width / 2, 20, -1);

        this.sword.render(context, mouseX, mouseY, delta);
        this.sword.setMessage(Text.literal("1.7 Sword Blockhit: " + (OldCombatModClient.oldCombat() ? "Enabled" : "Disabled")));
        this.eatDrink.render(context, mouseX, mouseY, delta);
        this.eatDrink.setMessage(Text.literal("1.7 Eat & Drink: " + (OldCombatModClient.oldEat() ? "Enabled" : "Disabled")));
        this.back.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
