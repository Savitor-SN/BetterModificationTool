package com.savitor.better_modification_tool.common.setting;

import lombok.Getter;

public enum TargetMode {
    INPUT(0, "input"),
    OUTPUT(1, "output"),
    BOTH(2, "both");

    @Getter
    final int status;
    @Getter
    final String name;

    TargetMode(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public TargetMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static TargetMode fromName(String name) {
        for (TargetMode mode : values()) {
            if (mode.name.equals(name)) {
                return mode;
            }
        }
        return null;
    }
}
