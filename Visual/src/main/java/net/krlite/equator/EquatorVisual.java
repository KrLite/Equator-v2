package net.krlite.equator;

import jdk.jfr.Label;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Label("Visual")
public class EquatorVisual implements ModInitializer {
	public static final String NAME = "Equator:Visual", ID = "equator-visual";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}
}
