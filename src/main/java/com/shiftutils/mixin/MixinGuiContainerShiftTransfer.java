package com.shiftutils.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainerShiftTransfer {
    private static final long CLICK_DELAY_MS = 85L;

    @Shadow
    private Slot theSlot;

    @Shadow
    public Container inventorySlots;

    @Shadow
    protected abstract void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType);

    private long shiftutils$lastClickTime;

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void shiftutils$autoShiftTransfer(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            return;
        }

        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            return;
        }

        if (!(this.inventorySlots instanceof ContainerPlayer) && !(this.inventorySlots instanceof ContainerChest)) {
            return;
        }

        Slot hovered = this.theSlot;
        if (hovered == null || !hovered.getHasStack()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - this.shiftutils$lastClickTime < CLICK_DELAY_MS) {
            return;
        }

        this.shiftutils$lastClickTime = now;
        this.handleMouseClick(hovered, hovered.slotNumber, 0, 1);
    }
}
