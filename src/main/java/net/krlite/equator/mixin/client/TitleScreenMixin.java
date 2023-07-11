package net.krlite.equator.mixin.client;

import net.krlite.equator.Equator;
import net.krlite.equator.test.CanvasScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(method = "init", at = @At("HEAD"))
	private void init(CallbackInfo info) {
		if (Equator.DEBUG) MinecraftClient.getInstance().setScreen(new CanvasScreen());
	}
}
