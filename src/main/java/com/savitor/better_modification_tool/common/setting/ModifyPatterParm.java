package com.savitor.better_modification_tool.common.setting;

import lombok.Data;

@Data
public class ModifyPatterParm {
    private int modifyRatio;
    private TargetMode modifyTarget;

    public ModifyPatterParm() {
    }

    public ModifyPatterParm(int modifyRatio, TargetMode modifyTarget) {
        this.modifyRatio = modifyRatio;
        this.modifyTarget = modifyTarget;
    }
}
