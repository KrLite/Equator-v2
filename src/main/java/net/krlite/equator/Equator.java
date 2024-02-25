package net.krlite.equator;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Equator implements ClientModInitializer {
	public static final String NAME = "Equator", ID = "equator";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final boolean DEBUG = true;

	private static long lastFrame = 0, frameDiff = 0;

	@Override
	public void onInitializeClient() {
	}

	public static void updateFrame(long currentFrame) {
		frameDiff = currentFrame - lastFrame;
		lastFrame = currentFrame;
	}

	public static Optional<Double> fps() {
		if (frameDiff <= 0) return Optional.empty();

		return Optional.of(1000.0 / frameDiff);
	}
}
