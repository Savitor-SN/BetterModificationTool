package com.savitor.better_modification_tool.util;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPart;
import appeng.core.definitions.AEItems;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.easterfg.mae2a.util.PatternUtils;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.context.PatternTargetModeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.gtlcore.gtlcore.common.machine.multiblock.part.ae.MEPatternBufferPartMachine;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.easterfg.mae2a.common.items.PatternModifyToolItem.findPartInCable;

public class ExtraPatternUtils {
    public static List<ItemStack> getProcessingPatterns(Level level, InternalInventory internalInventory, PatternModifySetting setting, boolean direct) {
        PatternTargetModeContext.setTargetMode(((SettingTargetModeAccessor) setting).BMT$getTargetMode());
        List<ItemStack> result = new ArrayList<>(internalInventory.size());
        for (int slot = 0; slot < internalInventory.size(); slot++) {
            var stack = internalInventory.getStackInSlot(slot);
            if (stack != null) {
                if (stack.isEmpty()) {
                    result.add(ItemStack.EMPTY);
                    continue;
                }
                if (direct) {
                    result.add(stack.copy());
                    continue;
                }
                var pattern = PatternUtils.processingPattern(level, stack, setting);
                if (pattern != null) {
                    result.add(pattern);
                } else {
                    result.add(AEItems.BLANK_PATTERN.stack());
                }
                continue;
            }
            result.add(ItemStack.EMPTY);
        }
        PatternTargetModeContext.clear();
        return result;
    }

    @Nullable
    public static InternalInventory findInternalInventory(Level level, BlockPos pos, Vec3 hit) {
        var te = level.getBlockEntity(pos);
        if (te instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof MEPatternBufferPartMachine me) {
            return me.getTerminalPatternInventory();
        }
        if (te instanceof PatternProviderLogicHost host) {
            return host.getLogic().getPatternInv();
        } else {
            IPart cable = findPartInCable(level, pos, hit);
            if (cable instanceof PatternProviderLogicHost host) {
                return host.getLogic().getPatternInv();
            }
        }
        return null;
    }
}
