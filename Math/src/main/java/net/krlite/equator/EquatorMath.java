package net.krlite.equator;

import jdk.jfr.Label;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Label("Math")
public class EquatorMath implements ModInitializer {
	public static final String NAME = "Equator:Math", ID = "equator-math";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}
}
