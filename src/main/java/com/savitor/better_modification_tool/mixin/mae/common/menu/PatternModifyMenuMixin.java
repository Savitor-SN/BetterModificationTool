package com.savitor.better_modification_tool.mixin.mae.common.menu;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import com.easterfg.mae2a.common.menu.PatternModifyMenu;
import com.easterfg.mae2a.common.menu.host.PatternModifyHost;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.savitor.better_modification_tool.common.accessor.HostTargetModeAccessor;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.setting.TargetMode;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternModifyMenu.class)
public abstract class PatternModifyMenuMixin extends AEBaseMenu {

    @Shadow(remap = false)
    @Final
    protected PatternModifyHost host;
    @Shadow(remap = false)
    public PatternModifySetting.ModifyMode mode;

    @Shadow(remap = false)
    public abstract void setMode(PatternModifySetting.ModifyMode mode);

    @Shadow(remap = false)
    public boolean limitMode;

    @Shadow(remap = false)
    public abstract void setLimitMode(boolean limitMode);

    @Unique
    private static final String ACTION_SET_TARGET_MODE = "set_target_mode";

    @Unique
    private TargetMode BMT$currentTargetMode;
    @Unique
    @Getter
    @GuiSync(3)
    public TargetMode BMT$targetMode = TargetMode.BOTH;

    public PatternModifyMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
    public void PatternModifyMenu(CallbackInfo ci) {
        registerClientAction(ACTION_SET_TARGET_MODE, TargetMode.class, ((HostTargetModeAccessor) host)::BMT$setTargetMode);
    }

    @Unique
    public void BMT$setModifyTarget(TargetMode targetMode) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_TARGET_MODE, targetMode);
        } else {
            this.BMT$targetMode = targetMode;
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (isServerSide()) {
            if (this.mode != host.getMode()) {
                this.setMode(host.getMode());
            }

            if (this.limitMode != host.isLimitMode()) {
                this.setLimitMode(host.isLimitMode());
            }
            if (this.BMT$targetMode != ((HostTargetModeAccessor) host).BMT$getTargetMode()) {
                this.BMT$setModifyTarget(((HostTargetModeAccessor) host).BMT$getTargetMode());
            }
        }
    }

    @Inject(method = "onServerDataSync", at = @At(value = "TAIL"), remap = false)
    public void onServerDataSync(CallbackInfo ci) {
        if (this.BMT$currentTargetMode != this.BMT$targetMode) {
            ((HostTargetModeAccessor) this.host).BMT$setTargetMode(this.BMT$targetMode);
            this.BMT$currentTargetMode = this.BMT$targetMode;
        }
    }
}
