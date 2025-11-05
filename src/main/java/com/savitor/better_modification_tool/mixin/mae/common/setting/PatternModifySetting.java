package com.savitor.better_modification_tool.mixin.mae.common.setting;


import com.savitor.better_modification_tool.BetterModifyToolMod;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(com.easterfg.mae2a.common.settings.PatternModifySetting.class)
public class PatternModifySetting implements SettingTargetModeAccessor {

    @Unique
    private TargetMode BMT$targetMode = TargetMode.BOTH;

    @Inject(method = "readFromNBT", at = @At("HEAD"), remap = false)
    public void readFromNBT(CompoundTag data, CallbackInfo ci) {
        if (data.contains("setting", CompoundTag.TAG_COMPOUND)) {
            CompoundTag compound = data.getCompound("setting");
            if (compound.contains("targetMode")) {
                this.BMT$targetMode = TargetMode.fromName(compound.getString("targetMode"));
            }
        }
    }

    @Inject(method = "writeFromNBT", at = @At("HEAD"), remap = false)
    public void writeFromNBT(CompoundTag data, CallbackInfo ci) {
        data.putString("targetMode", this.BMT$targetMode.getName());
    }

    @Override
    public TargetMode BMT$getTargetMode() {
        return this.BMT$targetMode;
    }

    @Override
    public void BMT$setTargetMode(TargetMode targetMode) {
        this.BMT$targetMode = targetMode;
    }

}
