package com.savitor.better_modification_tool.mixin.mae.util;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.helpers.patternprovider.PatternProviderLogic;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.easterfg.mae2a.util.PatternUtils;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.context.PatternTargetModeContext;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(PatternUtils.class)
public abstract class PatternUtilsMixin {

    @Shadow(remap = false)
    public static GenericStack[] scaleStacks(GenericStack[] stacks, int times, boolean multiplyMode) {
        return null;
    }

    @Inject(method = "apply", at = @At("HEAD"), remap = false, cancellable = true)
    private static void apply(AEProcessingPattern pattern, int times, boolean hasByProducts, boolean multiplyMode, GenericStack primary, CallbackInfoReturnable<ItemStack> cir) {
        if (times <= 1) {
            cir.setReturnValue(null);
        }
        TargetMode targetMode = PatternTargetModeContext.getTargetMode();
        GenericStack[] outputs = pattern.getSparseOutputs();
        GenericStack[] inputs = pattern.getSparseInputs();
        GenericStack[] newInputs;
        GenericStack[] newOutput;
        newInputs = targetMode == TargetMode.OUTPUT ? inputs : scaleStacks(inputs, times, multiplyMode);
        newOutput = targetMode == TargetMode.INPUT ? outputs : scaleOutputs(outputs, primary, times, multiplyMode, hasByProducts);
        if (newInputs == null || newOutput == null) {
            cir.setReturnValue(null);
        } else
            cir.setReturnValue(PatternDetailsHelper.encodeProcessingPattern(newInputs, newOutput));
        cir.cancel();
    }

    /**
     * @author Savitor
     * @reason 修复逻辑错误
     */
    @Overwrite(remap = false)
    private static GenericStack[] scaleOutputs(GenericStack[] outputs, GenericStack primary,
                                               int times, boolean multiplyMode, boolean hasByProducts) {
        GenericStack[] scaled = new GenericStack[outputs.length];

        // primary
        long newAmount;
        if (multiplyMode) {
            newAmount = primary.amount() * times;
        } else {
            if (primary.amount() % times != 0) {
                return null;
            }
            newAmount = primary.amount() / times;
        }
        if (newAmount <= 0)
            return null;
        scaled[0] = new GenericStack(primary.what(), newAmount);

        // other
        if (hasByProducts && outputs.length > 1) {
            System.arraycopy(outputs, 1, scaled, 1, outputs.length - 1);
        }

        return scaled;
    }

//    @Inject(
//            method = "getProcessingPatterns",
//            at = @At("HEAD"),
//            remap = false
//    )
//    private static void onGetProcessingPatternsStart(Level level, PatternProviderLogic logic, PatternModifySetting
//            setting, boolean direct, CallbackInfoReturnable<List<ItemStack>> cir) {
//        PatternTargetModeContext.setTargetMode(((SettingTargetModeAccessor) setting).BMT$getTargetMode());
//    }
//
//    @Inject(
//            method = "getProcessingPatterns",
//            at = @At("RETURN"),
//            remap = false
//    )
//    private static void onGetProcessingPatternsEnd(Level level, PatternProviderLogic logic, PatternModifySetting
//            setting, boolean direct, CallbackInfoReturnable<List<ItemStack>> cir) {
//        PatternTargetModeContext.clear();
//    }
}
