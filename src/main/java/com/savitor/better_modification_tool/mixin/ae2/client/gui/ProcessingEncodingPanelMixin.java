package com.savitor.better_modification_tool.mixin.ae2.client.gui;

import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.EncodingModePanel;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.items.ProcessingEncodingPanel;
import appeng.client.gui.style.Blitter;
import com.savitor.better_modification_tool.BetterModifyToolMod;
import com.savitor.better_modification_tool.client.gui.widget.BetterCustomIconButton;
import com.savitor.better_modification_tool.common.accessor.PatterEncodingTermMenuAccessor;
import com.savitor.better_modification_tool.common.setting.ModifyPatterParm;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Savitor
 * 代码参考于gtlcore
 */
@Mixin(value = ProcessingEncodingPanel.class, priority = 2000)
public abstract class ProcessingEncodingPanelMixin extends EncodingModePanel {

    @Unique
    private int[] BMT$rate;
    @Unique
    private BetterCustomIconButton[] BMT$multiple;
    @Unique
    private BetterCustomIconButton[] BMT$dividing;
    @Unique
    private BetterCustomIconButton BMT$modifyTypeBtn;

    @Unique
    private TargetMode BMT$TargetMode = TargetMode.BOTH;

    public ProcessingEncodingPanelMixin(PatternEncodingTermScreen<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Inject(method = "<init>", at = @At("TAIL"), cancellable = true)
    public void init(PatternEncodingTermScreen<?> screen, WidgetContainer widgets, CallbackInfo ci) {

        BMT$rate = new int[]{2, 3, 5};
        BMT$multiple = new BetterCustomIconButton[3];
        BMT$dividing = new BetterCustomIconButton[3];

        var TARGET_TEXTURE = Blitter.texture(BetterModifyToolMod.id("textures/guis/modify_target_2.png"), 48, 16);
        var STATE_TEXTURE = Blitter.texture(BetterModifyToolMod.id("textures/guis/states.png"), 48, 32);
        BMT$modifyTypeBtn = BetterCustomIconButton.Builder.builder(__ -> BMT$TargetMode = BMT$TargetMode.next())
                .addState(TARGET_TEXTURE.copy()
                        .src(0, 0, 16, 16), Component.translatable("gui.mae2a.target_material"))
                .addState(TARGET_TEXTURE.copy()
                        .src(16, 0, 16, 16), Component.translatable("gui.mae2a.target_product"))
                .addState(TARGET_TEXTURE.copy()
                        .src(32, 0, 16, 16), Component.translatable("gui.bmt.mae2a.target_all"))
                .status(() -> this.BMT$TargetMode.getStatus())
                .size(8, 8)
                .message(Component.translatable("gui.bmt.mae2a.target_mode_tip"))
                .build();
        for (int i = 0; i < BMT$multiple.length; i++) {
            final int index = i;
            BMT$multiple[i] = BetterCustomIconButton.Builder.builder(
                            __ -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(BMT$rate[index], BMT$TargetMode)))
                    .addState(STATE_TEXTURE.copy()
                            .src(index * 16, 0, 16, 16), Component.translatable("tooltip.bmt.ae2.encoding.pattern_both_multiply_" + BMT$rate[index]))
                    .addState(STATE_TEXTURE.copy()
                            .src(index * 16, 0, 16, 16), Component.translatable("tooltip.bmt.ae2.encoding.pattern_output_multiply_" + BMT$rate[index]))
                    .addState(STATE_TEXTURE.copy()
                            .src(index * 16, 0, 16, 16), Component.translatable("tooltip.bmt.ae2.encoding.pattern_input_multiply_" + BMT$rate[index]))
                    .status(() -> this.BMT$TargetMode.getStatus())
                    .size(8, 8)
                    .message(Component.translatable("gui.bmt.ae2.encoding.pattern_recipe_multiply_" + BMT$rate[index]))
                    .build();
        }
        for (int i = 0; i < BMT$multiple.length; i++) {
            final int index = i;
            BMT$dividing[i] = BetterCustomIconButton.Builder.builder(
                            __ -> ((PatterEncodingTermMenuAccessor) this.menu).BMT$modifyPatter(new ModifyPatterParm(-BMT$rate[index], BMT$TargetMode)))
                    .addState(STATE_TEXTURE.copy()
                            .src(index * 16, 16, 16, 16), Component.translatable("tooltip.bmt.ae2.encoding.pattern_both_divide_" + BMT$rate[index]))
                    .addState(STATE_TEXTURE.copy()
                            .src(index * 16, 16, 16, 16), Component.translatable("tooltip.bmt.ae2.encoding.pattern_output_divide_" + BMT$rate[index]))
                    .addState(STATE_TEXTURE.copy()
                            .src(index * 16, 16, 16, 16), Component.translatable("tooltip.bmt.ae2.encoding.pattern_input_divide_" + BMT$rate[index]))
                    .status(() -> this.BMT$TargetMode.getStatus())
                    .size(8, 8)
                    .message(Component.translatable("gui.bmt.ae2.encoding.pattern_recipe_divide_" + BMT$rate[index]))
                    .build();
        }

        widgets.add("modifyType", BMT$modifyTypeBtn);
        widgets.add("modify1", BMT$multiple[0]);
        widgets.add("modify2", BMT$multiple[1]);
        widgets.add("modify3", BMT$multiple[2]);
        widgets.add("modify4", BMT$dividing[0]);
        widgets.add("modify5", BMT$dividing[1]);
        widgets.add("modify6", BMT$dividing[2]);
        ci.cancel();
    }

    @Inject(method = "setVisible", at = @At("TAIL"), remap = false)
    public void setVisibleHooks(boolean visible, CallbackInfo ci) {
        this.BMT$modifyTypeBtn.setVisibility(visible);
        for (int i = 0; i < BMT$rate.length; i++) {
            BMT$multiple[i].setVisibility(visible);
            BMT$dividing[i].setVisibility(visible);
        }
    }

}
