package com.savitor.better_modification_tool.mixin.ae2.client.gui;

import appeng.api.stacks.GenericStack;
import appeng.api.storage.ITerminalHost;
import appeng.helpers.IMenuCraftingPacket;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.util.ConfigInventory;
import com.google.common.math.LongMath;
import com.savitor.better_modification_tool.common.accessor.PatterEncodingTermMenuAccessor;
import com.savitor.better_modification_tool.common.setting.ModifyPatterParm;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Savitor
 * 代码参考于gtlcore
 */
@Mixin(value = PatternEncodingTermMenu.class,priority = 2000)
public abstract class PatternEncodingTermMenuMixin extends MEStorageMenu implements IMenuCraftingPacket, PatterEncodingTermMenuAccessor {

    @Shadow(remap = false)
    @Final
    private ConfigInventory encodedInputsInv;
    @Shadow(remap = false)
    @Final
    private ConfigInventory encodedOutputsInv;

    public PatternEncodingTermMenuMixin(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/IPatternTerminalMenuHost;Z)V",
            at = @At("TAIL"),
            remap = false)
    public void initHooks(MenuType<?> menuType, int id, Inventory ip, IPatternTerminalMenuHost host, boolean bindInventory, CallbackInfo ci) {
        this.registerClientAction("BMT$modifyPatter", ModifyPatterParm.class,
                this::BMT$modifyPatter);
    }

    @Override
    public void BMT$modifyPatter(ModifyPatterParm parm) {
        if (this.isClientSide()) {
            this.sendClientAction("BMT$modifyPatter", parm);
        } else {
            // modify
            if (parm.getModifyTarget() == TargetMode.BOTH || parm.getModifyTarget() == TargetMode.OUTPUT) {
                var output = BMT$valid(this.encodedOutputsInv, parm.getModifyRatio());
                if (output == null) {
                    return;
                }
                for (int slot = 0; slot < output.length; slot++) {
                    if (output[slot] != null) {
                        this.encodedOutputsInv.setStack(slot, output[slot]);
                    }
                }
            }
            if (parm.getModifyTarget() == TargetMode.BOTH || parm.getModifyTarget() == TargetMode.INPUT) {
                var input = BMT$valid(this.encodedInputsInv, parm.getModifyRatio());
                if (input == null) {
                    return;
                }
                for (int slot = 0; slot < input.length; slot++) {
                    if (input[slot] != null) {
                        this.encodedInputsInv.setStack(slot, input[slot]);
                    }
                }
            }
        }
    }

    @Unique
    private GenericStack[] BMT$valid(ConfigInventory inv, int data) {
        // data 错误的被修改为正数, 在有多个多个材料时
        boolean flag = data > 0;
        if (!flag) {
            data = -data;
        }
        GenericStack[] result = new GenericStack[inv.size()];
        for (int slot = 0; slot < inv.size(); slot++) {
            GenericStack stack = inv.getStack(slot);
            if (stack != null) {
                if (flag) {
                    long modify = LongMath.saturatedMultiply(data, stack.amount());
                    if (modify == Long.MAX_VALUE || modify == Long.MIN_VALUE) {
                        return null;
                    } else {
                        result[slot] = new GenericStack(stack.what(), modify);
                    }
                } else {
                    if (stack.amount() % data != 0) {
                        return null;
                    } else {
                        // 除尽
                        result[slot] = new GenericStack(stack.what(), stack.amount() / data);
                    }
                }
            }
        }
        return result;
    }
}
