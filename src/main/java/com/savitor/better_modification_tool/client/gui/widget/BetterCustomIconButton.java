package com.savitor.better_modification_tool.client.gui.widget;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author EasterFG
 * 此类基于于EasterFG的CustomIconButton类，遵循gpl-3.0协议
 */
public class BetterCustomIconButton extends Button implements ITooltip {

    private final List<Blitter> textures;
    private final List<Component> tooltips;

    @Setter
    private Supplier<Integer> statusSupplier;

    public BetterCustomIconButton(int width, int height, OnPress onPress, List<Blitter> textures, List<Component> tooltips) {
        super(0, 0, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.textures = new ArrayList<>(textures);
        this.tooltips = new ArrayList<>(tooltips);
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            guiGraphics.pose().pushPose();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();

            if (this.isFocused()) {
                guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
            }

            guiGraphics.pose().translate(getX(), getY(), 0);
            float scaleX = (float) width / 16.0F;
            float scaleY = (float) height / 16.0F;
            guiGraphics.pose().scale(scaleX, scaleY, 1.0F);

            Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(0, 0).blit(guiGraphics);

            int currentState = Math.abs(statusSupplier.get()) % textures.size();
            final Blitter currentTexture = textures.get(currentState);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            currentTexture.dest(0, 0).blit(guiGraphics);

            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public List<Component> getTooltipMessage() {
        int currentState = Math.abs(statusSupplier.get()) % tooltips.size();
        Component currentTooltip = tooltips.get(currentState);
        return List.of(this.getMessage(), currentTooltip);
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(this.getX(), this.getY(), 16, 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {

        private int width = 16;
        private int height = 16;

        private final List<Blitter> textures = new ArrayList<>();
        private final List<Component> tooltips = new ArrayList<>();

        private Component message;

        private Supplier<Integer> status = () -> 0;
        private OnPress press;

        public static Builder builder(OnPress press) {
            Builder builder = new Builder();
            builder.press = press;
            return builder;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder addState(Blitter texture, Component tooltip) {
            this.textures.add(texture);
            this.tooltips.add(tooltip);
            return this;
        }

        public Builder status(@NotNull Supplier<Integer> status) {
            this.status = status;
            return this;
        }

        public Builder message(Component message) {
            this.message = message;
            return this;
        }

        public BetterCustomIconButton build() {
            if (textures.isEmpty()) {
                throw new IllegalStateException("至少需要添加一个状态");
            }
            if (textures.size() != tooltips.size()) {
                throw new IllegalStateException("纹理数量和提示数量必须相等");
            }

            BetterCustomIconButton button = new BetterCustomIconButton(width, height, press, textures, tooltips);
            button.setStatusSupplier(status);
            if (message != null) {
                button.setMessage(message);
            }
            return button;
        }
    }
}
