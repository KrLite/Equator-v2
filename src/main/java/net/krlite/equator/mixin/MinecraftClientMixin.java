package net.krlite.equator.mixin;

import net.krlite.equator.util.InputEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	/**
	 * Injects the GLFW callback initializations into the MinecraftClient class.
	 */
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(RunArgs args, CallbackInfo ci) {
		// Mouse
		InputEvents.Mouse.initMouseCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Mouse.initMouseScrollCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Mouse.initCursorPositionCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Mouse.initCursorEnterCallback(MinecraftClient.getInstance().getWindow().getHandle());

		// Keyboard
		InputEvents.Keyboard.initKeyboardCallback(MinecraftClient.getInstance().getWindow().getHandle());

		// Window
		InputEvents.Window.initWindowCloseCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Window.initWindowFocusCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Window.initWindowIconifyCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Window.initWindowMaximizeCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Window.initWindowPositionCallback(MinecraftClient.getInstance().getWindow().getHandle());
		InputEvents.Window.initWindowSizeCallback(MinecraftClient.getInstance().getWindow().getHandle());
	}
}
