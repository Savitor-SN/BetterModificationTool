package com.savitor.better_modification_tool.mixin.mae.common.menu.host;

import appeng.api.inventories.InternalInventory;
import com.easterfg.mae2a.common.items.PatternModifyToolItem;
import com.easterfg.mae2a.common.menu.host.PatternModifyHost;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.easterfg.mae2a.util.PatternUtils;
import com.savitor.better_modification_tool.common.accessor.HostInternalInventoryAccessor;
import com.savitor.better_modification_tool.common.accessor.HostTargetModeAccessor;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import com.savitor.better_modification_tool.util.ExtraPatternUtils;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(PatternModifyHost.class)
public abstract class PatternModifyHostMixin implements HostTargetModeAccessor, HostInternalInventoryAccessor {


    @Shadow(remap = false)
    @Final
    private PatternModifySetting setting;

    @Shadow(remap = false)
    public abstract void saveSetting();

    @Shadow(remap = false)
    private List<ItemStack> patterns;

    @Getter
    @Nullable
    @Unique
    private InternalInventory BMT$internalInventory;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    public void PatternModifyHost(Player player, Integer slot, ItemStack itemStack, BlockPos clickPos, CallbackInfo ci) {
        patterns = null;
        Vec3 hitPos = null;
        if (itemStack.hasTag()) {
            CompoundTag tag = itemStack.getOrCreateTag();
            this.setting.readFromNBT(tag);
            hitPos = PatternUtils.readVec3(tag.getCompound("hitPos1"));
            tag.remove("hitPos1");
        }
        if (hitPos != null && clickPos != null) {
            Level level1 = player.getCommandSenderWorld();
            BMT$internalInventory = ExtraPatternUtils.findInternalInventory(level1, clickPos, hitPos);
            if (BMT$internalInventory != null) {
                patterns = ExtraPatternUtils.getProcessingPatterns(level1, BMT$internalInventory, setting, player.isShiftKeyDown());
            }
        }
    }

    @Override
    public void BMT$setTargetMode(TargetMode targetMode) {
        ((SettingTargetModeAccessor) setting).BMT$setTargetMode(targetMode);
        saveSetting();
    }

    @Override
    public TargetMode BMT$getTargetMode() {
        return ((SettingTargetModeAccessor) setting).BMT$getTargetMode();
    }

    @Override
    public void BMT$setInternalInventory(InternalInventory internalInventory) {
        this.BMT$internalInventory = internalInventory;
    }

    @Override
    public InternalInventory BMT$getInternalInventory() {
        return this.BMT$internalInventory;
    }
}
