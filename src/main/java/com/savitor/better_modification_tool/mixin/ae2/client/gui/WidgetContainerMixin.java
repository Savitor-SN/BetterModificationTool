package com.savitor.better_modification_tool.mixin.ae2.client.gui;

import appeng.client.gui.WidgetContainer;
import com.savitor.better_modification_tool.common.accessor.WidgetContainerAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = WidgetContainer.class)
public class WidgetContainerMixin implements WidgetContainerAccessor {
    @Shadow(remap = false)
    @Final
    private Map<String, AbstractWidget> widgets;


    @Override
    public void BMT$remove(String id){
        widgets.remove(id);
    }

    @Override
    public Map<String, AbstractWidget> BMT$getWidgets() {
        return widgets;
    }
}
