package com.savitor.better_modification_tool.mixin.mae.common.menu;

import appeng.core.definitions.AEItems;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import com.easterfg.mae2a.api.slot.PreviewSlot;
import com.easterfg.mae2a.common.menu.PatternPreviewListMenu;
import com.easterfg.mae2a.common.menu.host.PatternModifyHost;
import com.savitor.better_modification_tool.common.accessor.HostInternalInventoryAccessor;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.context.PatternTargetModeContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PatternPreviewListMenu.class)
public class PatternPreviewListMenuMixin extends AEBaseMenu {

    @Shadow(remap = false)
    @Final
    protected PatternModifyHost host;

    @Shadow(remap = false)
    @Final
    private static String ACTION_CONFIRM;

    @Shadow(remap = false)
    private boolean select;

    public PatternPreviewListMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    /**
     * @author Savitor
     * @reason 新增功能适配
     */
    @Overwrite(remap = false)
    public void confirm() {
        if (isClientSide()) {
            sendClientAction(ACTION_CONFIRM);
            return;
        }
        int count = 0;
        var internalInventory = ((HostInternalInventoryAccessor) host).BMT$getInternalInventory();
        if (internalInventory != null) {
            List<Slot> patterns = this.getSlots(SlotSemantics.ENCODED_PATTERN);
            for (Slot slot : patterns) {
                if (slot instanceof PreviewSlot previewSlot) {
                    if (!slot.hasItem() || select != previewSlot.isEnable())
                        continue;
                    ItemStack slotItem = previewSlot.getItem();
                    if (slotItem.getItem() != AEItems.PROCESSING_PATTERN.asItem()) {
                        continue;
                    }
                    internalInventory.setItemDirect(slot.getSlotIndex(), slotItem);
                    count++;
                }
            }
        }
        if (count > 0) {
            getPlayer().displayClientMessage(Component.translatable("tools.mae2a.one_patter_result", count), true);
        }
    }

    @Inject(
            method = "multiply",
            at = @At("HEAD"),
            remap = false
    )
    private void onMultiplyStart(int times, CallbackInfo ci) {
        PatternTargetModeContext.setTargetMode(((SettingTargetModeAccessor) host.getPatternModifySetting()).BMT$getTargetMode());
    }

    @Inject(
            method = "multiply",
            at = @At("RETURN"),
            remap = false
    )
    private void onMultiplyEnd(int times, CallbackInfo ci) {
        PatternTargetModeContext.clear();
    }

    @Inject(
            method = "divide",
            at = @At("HEAD"),
            remap = false
    )
    private void onDivideStart(int times, CallbackInfo ci) {
        PatternTargetModeContext.setTargetMode(((SettingTargetModeAccessor) host.getPatternModifySetting()).BMT$getTargetMode());
    }

    @Inject(
            method = "divide",
            at = @At("RETURN"),
            remap = false
    )
    private void onDivideEnd(int times, CallbackInfo ci) {
        PatternTargetModeContext.clear();
    }
}
