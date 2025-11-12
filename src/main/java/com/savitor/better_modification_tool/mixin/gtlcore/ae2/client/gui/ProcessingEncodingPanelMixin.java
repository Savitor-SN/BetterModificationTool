package com.savitor.better_modification_tool.mixin.gtlcore.ae2.client.gui;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.EncodingModePanel;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.items.ProcessingEncodingPanel;
import appeng.client.gui.style.Blitter;
import com.savitor.better_modification_tool.BetterModifyToolMod;
import com.savitor.better_modification_tool.client.gui.widget.BetterCustomIconButton;
import com.savitor.better_modification_tool.common.accessor.PatterEncodingTermMenuAccessor;
import com.savitor.better_modification_tool.common.accessor.WidgetContainerAccessor;
import com.savitor.better_modification_tool.common.setting.ModifyPatterParm;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import net.minecraft.network.chat.Component;
import org.gtlcore.gtlcore.client.gui.ModifyIcon;
import org.gtlcore.gtlcore.client.gui.ModifyIconButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProcessingEncodingPanel.class, priority = 2000)
public abstract class ProcessingEncodingPanelMixin extends EncodingModePanel {

    @Unique
    private ModifyIconButton BMT$multipleTow;
    @Unique
    private ModifyIconButton BMT$multipleThree;
    @Unique
    private ModifyIconButton BMT$multipleFive;
    @Unique
    private ModifyIconButton BMT$dividingTow;
    @Unique
    private ModifyIconButton BMT$dividingThree;
    @Unique
    private ModifyIconButton BMT$dividingFive;
    @Unique
    private BetterCustomIconButton BMT$modifyTypeBtn;

    @Unique
    private TargetMode BMT$TargetMode = TargetMode.BOTH;

    public ProcessingEncodingPanelMixin(PatternEncodingTermScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Inject(method = "<init>", at = @At("TAIL"), cancellable = true)
    public void init(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {

        for (var i = 1; i <= 6; i++) {
            if (((WidgetContainerAccessor) widgets).BMT$getWidgets().containsKey("modify" + i))
                ((WidgetContainerAccessor) widgets).BMT$remove("modify" + i);
        }

        BMT$multipleTow = new ModifyIconButton(b -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(2, BMT$TargetMode)), ModifyIcon.MULTIPLY_2,
                Component.translatable("gui.gtlcore.pattern_recipe_multiply_2"),
                Component.translatable("tooltip.gtlcore.pattern_materials_multiply_2"));

        BMT$multipleThree = new ModifyIconButton(b -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(3, BMT$TargetMode)), ModifyIcon.MULTIPLY_3,
                Component.translatable("gui.gtlcore.pattern_recipe_multiply_3"),
                Component.translatable("tooltip.gtlcore.pattern_materials_multiply_3"));

        BMT$multipleFive = new ModifyIconButton(b -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(5, BMT$TargetMode)), ModifyIcon.MULTIPLY_5,
                Component.translatable("gui.gtlcore.pattern_recipe_multiply_5"),
                Component.translatable("tooltip.gtlcore.pattern_materials_multiply_5"));

        BMT$dividingTow = new ModifyIconButton(b -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(-2, BMT$TargetMode)), ModifyIcon.DIVISION_2,
                Component.translatable("gui.gtlcore.pattern_recipe_divide_2"),
                Component.translatable("tooltip.gtlcore.pattern_materials_divide_2"));

        BMT$dividingThree = new ModifyIconButton(b -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(-3, BMT$TargetMode)), ModifyIcon.DIVISION_3,
                Component.translatable("gui.gtlcore.pattern_recipe_divide_3"),
                Component.translatable("tooltip.gtlcore.pattern_materials_divide_3"));

        BMT$dividingFive = new ModifyIconButton(b -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(-5, BMT$TargetMode)), ModifyIcon.DIVISION_5,
                Component.translatable("gui.gtlcore.pattern_recipe_divide_5"),
                Component.translatable("tooltip.gtlcore.pattern_materials_divide_5"));

        var TARGET_TEXTURE = Blitter.texture(BetterModifyToolMod.id("textures/guis/modify_target_2.png"), 48, 16);
        BMT$modifyTypeBtn = BetterCustomIconButton.Builder.builder(__ -> BMT$TargetMode = BMT$TargetMode.next())
                .addState(TARGET_TEXTURE.copy()
                        .src(0, 0, 16, 16), Component.translatable("gui.mae2a.target_material"))
                .addState(TARGET_TEXTURE.copy()
                        .src(16, 0, 16, 16), Component.translatable("gui.mae2a.target_product"))
                .addState(TARGET_TEXTURE.copy()
                        .src(32, 0, 16, 16), Component.translatable("gui.mae2a.target_all"))
                .status(() -> this.BMT$TargetMode.getStatus())
                .size(8, 8)
                .message(Component.translatable("gui.mae2a.target_mode_tip"))
                .build();
        widgets.add("modifyType", BMT$modifyTypeBtn);
        widgets.add("modify1", BMT$multipleTow);
        widgets.add("modify2", BMT$multipleThree);
        widgets.add("modify3", BMT$multipleFive);
        widgets.add("modify4", BMT$dividingTow);
        widgets.add("modify5", BMT$dividingThree);
        widgets.add("modify6", BMT$dividingFive);
        ci.cancel();
    }

    @Inject(method = "setVisible", at = @At("TAIL"), remap = false)
    public void setVisibleHooks(boolean visible, CallbackInfo ci) {
        this.BMT$modifyTypeBtn.setVisibility(visible);
        this.BMT$multipleTow.setVisibility(visible);
        this.BMT$multipleThree.setVisibility(visible);
        this.BMT$multipleFive.setVisibility(visible);
        this.BMT$dividingTow.setVisibility(visible);
        this.BMT$dividingThree.setVisibility(visible);
        this.BMT$dividingFive.setVisibility(visible);
    }

}
