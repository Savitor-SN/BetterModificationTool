package com.savitor.better_modification_tool.mixin.mae.util;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.ProcessingPatternItem;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.easterfg.mae2a.util.PatternUtils;
import com.savitor.better_modification_tool.common.context.PatternTargetModeContext;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;


@Mixin(PatternUtils.class)
public abstract class PatternUtilsMixin {

    @Shadow(remap = false)
    public static GenericStack[] scaleStacks(GenericStack[] stacks, int times, boolean multiplyMode) {
        return null;
    }

    @Shadow(remap = false)
    private static boolean isScalingApplicable(long currentAmount, long limit, boolean multiplyMode) {
        return false;
    }

    @Shadow(remap = false)
    private static int calculateScalingFactor(long currentAmount, long limit, boolean multiplyMode) {
        return 0;
    }

    /**
     * 处理样板
     *
     * @param level     维度
     * @param itemStack 要处理的样板
     * @param setting   设置
     * @return 处理后的样板, null表示无需处理
     * @author Savitor
     * @reason 修复逻辑错误
     */
    @Overwrite(remap = false)
    public static @Nullable ItemStack processingPattern(Level level, ItemStack itemStack,
                                                        PatternModifySetting setting) {
        if (!(itemStack.getItem() instanceof ProcessingPatternItem item))
            return null;
        AEProcessingPattern patter = item.decode(itemStack, level, false);
        if (patter == null)
            return null;
        GenericStack output = patter.getPrimaryOutput();
        boolean flag = setting.getMode() == PatternModifySetting.ModifyMode.MULTIPLY;
        if (!setting.isLimitMode()) {
            return apply(patter, setting.getRate(), setting.isSaveByProducts(), flag, patter.getPrimaryOutput());
        }
        if (output.what() instanceof AEFluidKey) {
            return BMT$processLimitMode(patter, flag ? setting.getMaxFluidLimit() : setting.getMinFluidLimit(), false,
                    setting.isSaveByProducts(), flag, setting.isProduct());
        } else if (output.what() instanceof AEItemKey) {
            return BMT$processLimitMode(patter, flag ? setting.getMaxItemLimit() : setting.getMinItemLimit(), true,
                    setting.isSaveByProducts(), flag, setting.isProduct());
        }
        return itemStack;
    }

    /**
     * @author Savitor
     * @reason 新增功能适配
     */
    @Overwrite(remap = false)
    public static ItemStack apply(AEProcessingPattern pattern, int times, boolean hasByProducts, boolean multiplyMode, GenericStack primary) {
        if (times <= 1)
            return null;
        TargetMode targetMode = PatternTargetModeContext.getTargetMode();
        GenericStack[] outputs = pattern.getSparseOutputs();
        GenericStack[] inputs = pattern.getSparseInputs();
        GenericStack[] newInputs = targetMode == TargetMode.OUTPUT ? inputs : scaleStacks(inputs, times, multiplyMode);
        GenericStack[] newOutput = targetMode == TargetMode.INPUT ? outputs : scaleOutputs(outputs, primary, times, multiplyMode, hasByProducts);
        if (newInputs == null || newOutput == null) {
            return null;
        }
        return PatternDetailsHelper.encodeProcessingPattern(newInputs, newOutput);
    }

    /**
     * @author Savitor
     * @reason 修复逻辑错误
     */
    @Overwrite(remap = false)
    private static GenericStack[] scaleOutputs(GenericStack[] outputs, GenericStack primary, int times, boolean multiplyMode, boolean hasByProducts) {
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

    @Unique
    private static @Nullable ItemStack BMT$processLimitMode(AEProcessingPattern pattern, long limit, boolean isGetLimitByItem, boolean hasByProducts,
                                                            boolean multiplyMode, boolean productsMode) {
        var primary = pattern.getPrimaryOutput();
        if (!productsMode && !isScalingApplicable(primary.amount(), limit, multiplyMode)) {
            return null;
        }
        int times;
        if (productsMode) {
            times = BMT$calculateScalingFactor(pattern.getSparseInputs(), limit, isGetLimitByItem, multiplyMode);
        } else {
            times = calculateScalingFactor(primary.amount(), limit, multiplyMode);
        }
        return apply(pattern, times, hasByProducts, multiplyMode, primary);
    }

    @Unique
    private static int BMT$calculateScalingFactor(GenericStack[] stacks, long limit, boolean isGetLimitByItem, boolean multiplyMode) {
        int rate = Integer.MAX_VALUE;
        long limitTemp = limit;
        long currentTemp;
        for (GenericStack stack : stacks) {
            if (stack == null || stack.amount() == 0)
                continue;
            final long current = stack.amount();
            currentTemp = current;
            if (isGetLimitByItem && stack.what() instanceof AEFluidKey)
                limitTemp = limit * 1000L;
            else if (!isGetLimitByItem && stack.what() instanceof AEItemKey)
                currentTemp = current * 1000L;
            if (multiplyMode) {
                if (limitTemp < currentTemp)
                    return 0;
                int candidate = (int) (limitTemp / currentTemp);
                rate = Math.min(rate, candidate);
            } else {
                if (currentTemp < limitTemp)
                    return 0;
                int candidate = (int) (currentTemp / limitTemp);
                rate = Math.min(rate, candidate);
            }
        }
        return rate == Integer.MAX_VALUE ? 1 : Math.max(rate, 1);
    }
}
