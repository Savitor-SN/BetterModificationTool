package com.savitor.better_modification_tool.mixin;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MainMixinConfig implements IMixinConfigPlugin {

    private static final ModList modList = ModList.get();
    private static final LoadingModList loadingModList = LoadingModList.get();

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
//        if (mixinClassName.contains("com.savitor.better_modification_tool.mixin.mae")) {
//            return modList != null ? modList.getModContainerById("mae2a")
//                    .isPresent() :
//                    loadingModList.getModFileById("mae2a") != null;
//        } else
        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode classNode, String mixinClassName, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode classNode, String mixinClassName, IMixinInfo iMixinInfo) {
    }
}
