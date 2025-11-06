package com.savitor.better_modification_tool.mixin.mae.common.items;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.IPart;
import appeng.api.util.DimensionalBlockPos;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.encoding.PatternEncodingTerminalPart;
import appeng.util.Platform;
import com.easterfg.mae2a.common.items.PatternModifyToolItem;
import com.easterfg.mae2a.common.menu.PatternModifyMenu;
import com.easterfg.mae2a.common.menu.PatternPreviewListMenu;
import com.easterfg.mae2a.common.settings.PatternModifySetting;
import com.easterfg.mae2a.util.PatternUtils;
import com.savitor.better_modification_tool.common.accessor.SettingTargetModeAccessor;
import com.savitor.better_modification_tool.common.context.PatternTargetModeContext;
import com.savitor.better_modification_tool.util.GTLLoadedUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.easterfg.mae2a.common.items.PatternModifyToolItem.findPartInCable;

@Mixin(PatternModifyToolItem.class)
public abstract class PatternModifyToolItemMixin {

    @Shadow(remap = false)
    protected abstract void applyInAll(Player p, Level level, IInWorldGridNodeHost nodeHost, PatternModifySetting setting);

    /**
     * @author Savitor
     * @reason 新增功能适配
     */
    @Overwrite(remap = false)
    public boolean showToolGui(@NotNull ItemStack stack, @NotNull UseOnContext useContext) {
        if (useContext.getPlayer() == null) {
            return false;
        }

        BlockPos pos = useContext.getClickedPos();
        Player p = useContext.getPlayer();
        Level level = useContext.getLevel();
        if (!Platform.hasPermissions(new DimensionalBlockPos(level, pos), p)) {
            return false;
        }
        var nodeHost = GridHelper.getNodeHost(level, pos);
        if (nodeHost == null) {
            MenuOpener.open(PatternModifyMenu.TYPE, p, MenuLocators.forHand(p, useContext.getHand()));
        } else {
            boolean network = false;
            PatternContainer container = null;
            BlockEntity te = level.getBlockEntity(pos);
            IPart cable = findPartInCable(level, pos, useContext.getClickLocation());
            if (ModList.get().isLoaded("gtceu") && ModList.get().isLoaded("gtlcore")) {
                container = GTLLoadedUtil.gtlMe(te);
            }
            if (cable == null && te instanceof PatternContainer) {
                container = (PatternContainer) te;
            } else {
                if (cable instanceof PatternContainer) {
                    container = (PatternContainer) cable;
                } else if (cable instanceof PatternEncodingTerminalPart) {
                    network = true;
                }
            }
            if (container != null) {
                if (container.getTerminalPatternInventory().isEmpty()) {
                    p.displayClientMessage(Component.translatable("tools.mae2a.no_pattern"), true);
                    return false;
                }
                MenuOpener.open(PatternPreviewListMenu.TYPE, p, MenuLocators.forItemUseContext(useContext));
                return true;
            } else if (!network) {
                return false;
            }
            PatternModifySetting setting = new PatternModifySetting();
            setting.readFromNBT(stack.getOrCreateTag());
            applyInAll(p, level, nodeHost, setting);
        }
        return true;
    }

    @Inject(
            method = "applyInAll",
            at = @At("HEAD"),
            remap = false,
            cancellable = true)
    private void onApplyInAllStart(Player p, Level level, IInWorldGridNodeHost nodeHost, PatternModifySetting setting, CallbackInfo ci) {
        if (!p.isShiftKeyDown())
            ci.cancel();
        PatternTargetModeContext.setTargetMode(((SettingTargetModeAccessor) setting).BMT$getTargetMode());
    }

    @Inject(
            method = "applyInAll",
            at = @At("RETURN"),
            remap = false
    )
    private void onApplyInAllEnd(Player p, Level level, IInWorldGridNodeHost nodeHost, PatternModifySetting setting, CallbackInfo ci) {
        PatternTargetModeContext.clear();
    }

    @Inject(method = "onItemUseFirst", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/context/UseOnContext;getLevel()Lnet/minecraft/world/level/Level;"), remap = false)
    public void onItemUseFirst(ItemStack stack, UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("hitPos1", PatternUtils.writeVec3(context.getClickLocation()));
    }
}
