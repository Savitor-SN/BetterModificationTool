package com.savitor.better_modification_tool.util;

import appeng.api.inventories.InternalInventory;
import appeng.helpers.patternprovider.PatternContainer;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.gtlcore.gtlcore.common.machine.multiblock.part.ae.MEPatternBufferPartMachine;

public class GTLLoadedUtil {

    public static PatternContainer gtlMe(BlockEntity blockEntity) {
        if (blockEntity instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof MEPatternBufferPartMachine me)
            return me;
        return null;
    }

    public static InternalInventory gtlInventory(BlockEntity blockEntity) {
        if (blockEntity instanceof MetaMachineBlockEntity mmbe && mmbe.getMetaMachine() instanceof MEPatternBufferPartMachine me)
            return me.getTerminalPatternInventory();
        return null;
    }
}
