package com.savitor.better_modification_tool.common.accessor;

import appeng.client.gui.WidgetContainer;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Map;

public interface WidgetContainerAccessor {
    void BMT$remove(String id);

    Map<String, AbstractWidget> BMT$getWidgets();
}
