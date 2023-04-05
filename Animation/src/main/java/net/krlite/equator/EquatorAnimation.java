package net.krlite.equator;

import jdk.jfr.Label;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Label("Animation")
public class EquatorAnimation implements ModInitializer {
	public static final String NAME = "Equator:Animation", ID = "equator-animation";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}
}
