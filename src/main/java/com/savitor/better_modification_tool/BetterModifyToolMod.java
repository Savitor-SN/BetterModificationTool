package com.savitor.better_modification_tool;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterModifyToolMod.MOD_ID)
public class BetterModifyToolMod
{
    public static final String MOD_ID = "better_modification_tool";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
    public BetterModifyToolMod(FMLJavaModLoadingContext context)
    {

    }

}
