package net.krlite.equator.mixin;

import net.krlite.equator.input.InputEvent;
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
		InputEvent.initCallbacks(MinecraftClient.getInstance().getWindow().getHandle());
	}
}
