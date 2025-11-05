package com.savitor.better_modification_tool.common.context;

import com.savitor.better_modification_tool.common.setting.TargetMode;

public class PatternTargetModeContext {
    private static final ThreadLocal<TargetMode> CURRENT_TARGET_MODE = new ThreadLocal<>();

    public static void setTargetMode(TargetMode targetMode) {
        CURRENT_TARGET_MODE.set(targetMode);
    }

    public static TargetMode getTargetMode() {
        TargetMode targetMode = CURRENT_TARGET_MODE.get();
        // 如果未设置，默认返回BOTH，保持原有行为
        return targetMode != null ? targetMode : TargetMode.BOTH;
    }

    public static void clear() {
        CURRENT_TARGET_MODE.remove();
    }
}