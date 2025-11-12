package com.savitor.better_modification_tool.mixin.gtlcore.ae2.client.gui;

import appeng.client.gui.style.StyleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = StyleManager.class,priority = 2000)
public abstract class StyleManagerMixin {

    @ModifyVariable(method = "loadStyleDoc", at = @At("HEAD"), argsOnly = true, remap = false)
    private static String loadStyleDocHooks(String path) {
        if (path.contains("wireless_pattern_encoding_terminal.json")) {
            return "/screens/wtlib/more_modify_wireless_pattern_encoding_terminal.json";
        } else if (path.contains("pattern_encoding_terminal.json")) {
            return "/screens/terminals/more_modify_pattern_encoding_terminal.json";
        }
        return path;
    }
}
