package com.savitor.better_modification_tool.mixin.mae.client.screen;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import appeng.client.gui.widgets.AETextField;
import com.easterfg.mae2a.MoreAE2Additions;
import com.easterfg.mae2a.client.gui.widget.CustomIconButton;
import com.easterfg.mae2a.client.screen.PatternModifyScreen;
import com.easterfg.mae2a.common.menu.PatternModifyMenu;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.easterfg.mae2a.common.settings.PatternModifySetting.ModifyMode;
import com.savitor.better_modification_tool.BetterModifyToolMod;
import com.savitor.better_modification_tool.client.gui.widget.BetterCustomIconButton;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;


@Mixin(PatternModifyScreen.class)
public abstract class PatternModifyScreenMixin extends AEBaseScreen<PatternModifyMenu> {


    @Unique
    private BetterCustomIconButton BMT$targetModeButton;

    @Final
    @Shadow(remap = false)
    private PatternModifySetting setting;

    @Shadow(remap = false)
    protected abstract int getItemLimit(ModifyMode mode);

    @Shadow(remap = false)
    @Final
    private static DecimalFormat NUMBER_FORMAT;

    @Shadow(remap = false)
    protected abstract int getFluidLimit(ModifyMode mode);

    @Shadow(remap = false)
    protected abstract void updateInputFields(AETextField input, String placeholder, String tooltip, String value);

    @Shadow(remap = false)
    @Final
    private AETextField itemInput;

    @Shadow(remap = false)
    @Final
    private AETextField fluidInput;

    @Shadow(remap = false)
    @Final
    private AETextField rateInput;

    @Shadow(remap = false)
    @Final
    private AECheckbox saveByProducts;

    @Shadow(remap = false)
    @Final
    private CustomIconButton switchTarget;

    @Shadow(remap = false)
    protected abstract void updateInput(boolean visible);

    public PatternModifyScreenMixin(PatternModifyMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)

    private void PatternModifyScreen(PatternModifyMenu menu, Inventory playerInventory, Component title, ScreenStyle style, CallbackInfo ci) {


        var TARGET_TEXTURE = Blitter.texture(BetterModifyToolMod.id("textures/guis/modify_target_2.png"), 48, 16);

        //创建按钮，点击时循环切换模式
        BMT$targetModeButton = BetterCustomIconButton.Builder.builder(__ -> {
                    ((SettingTargetModeAccessor) setting).BMT$setTargetMode(((SettingTargetModeAccessor) setting).BMT$getTargetMode()
                            .next());
                    menu.saveSetting(setting);
                    updateState(setting.getMode(), setting.isLimitMode());
                })
                .addState(TARGET_TEXTURE.copy()
                        .src(0, 0, 16, 16), Component.translatable("gui.mae2a.target_material"))
                .addState(TARGET_TEXTURE.copy()
                        .src(16, 0, 16, 16), Component.translatable("gui.mae2a.target_product"))
                .addState(TARGET_TEXTURE.copy()
                        .src(32, 0, 16, 16), Component.translatable("gui.mae2a.target_all"))
                .status(() -> ((SettingTargetModeAccessor) setting).BMT$getTargetMode().getStatus())
                .message(Component.translatable("gui.mae2a.target_mode_tip"))
                .build();
        addToLeftToolbar(BMT$targetModeButton);
        updateState(setting.getMode(), setting.isLimitMode());
    }

    /**
     * @author Savitor
     * @reason 新增功能适配
     */
    @Overwrite(remap = false)
    private void updateState(ModifyMode mode, boolean isLimitMode) {
        String itemPlaceholderKey, fluidPlaceholderKey, ratePlaceholderTooltip, tooltipKey, itemValue, fluidValue;
        String tooltipA;
        String tooltipB;
        switch (mode) {
            case MULTIPLY -> {
                tooltipA = "gui.mae2a.pattern_max_item_limit";
                tooltipB = "gui.mae2a.pattern_max_fluid_limit";
                itemPlaceholderKey = "gui.mae2a.pattern_max_item_limit";
                fluidPlaceholderKey = "gui.mae2a.pattern_max_fluid_limit";
                ratePlaceholderTooltip = "gui.mae2a.pattern_rate_" + ((SettingTargetModeAccessor) setting).BMT$getTargetMode()
                        .getName() + "_multiply";
                tooltipKey = "gui.mae2a.max_input_tip";

            }
            case DIVIDE -> {
                tooltipA = "gui.mae2a.pattern_min_item_limit";
                tooltipB = "gui.mae2a.pattern_min_fluid_limit";
                itemPlaceholderKey = "gui.mae2a.pattern_min_item_limit";
                fluidPlaceholderKey = "gui.mae2a.pattern_min_fluid_limit";
                ratePlaceholderTooltip = "gui.mae2a.pattern_rate_" + ((SettingTargetModeAccessor) setting).BMT$getTargetMode()
                        .getName() + "_divide";
                tooltipKey = "gui.mae2a.min_input_tip";
            }
            default -> {
                MoreAE2Additions.LOGGER.warn("Unknown ModifyMode: {}", mode);
                return;
            }
        }

        if (isLimitMode) {
            this.setTextHidden("tooltip_2", false);
            this.setTextContent("tooltip_1", Component.translatable(tooltipA));
            this.setTextContent("tooltip_2", Component.translatable(tooltipB));
        } else {
            this.setTextHidden("tooltip_2", true);
            this.setTextContent("tooltip_1", Component.translatable("gui.mae2a.pattern_rate"));
        }

        itemValue = String.valueOf(getItemLimit(mode));
        fluidValue = NUMBER_FORMAT.format(getFluidLimit(mode) / 1000D);
        updateInputFields(itemInput, itemPlaceholderKey, tooltipKey, itemValue);
        updateInputFields(fluidInput, fluidPlaceholderKey, tooltipKey, fluidValue);
        updateInputFields(rateInput, "gui.mae2a.pattern_rate", ratePlaceholderTooltip,
                String.valueOf(setting.getRate()));
        this.setTextContent("dialog_title", Component.translatable("gui.mae2a.pattern_tool_setting",
                Component.translatable(
                        setting.getMode() == ModifyMode.MULTIPLY ? "gui.mae2a.multiply" : "gui.mae2a.divide")));
        saveByProducts.setSelected(setting.isSaveByProducts());
        this.switchTarget.setVisibility(setting.isLimitMode());
        if (BMT$targetModeButton != null)
            BMT$targetModeButton.setVisibility(!setting.isLimitMode());
        updateInput(setting.isLimitMode());
    }

}
