package net.krlite.equator;

import net.fabricmc.api.ModInitializer;
import net.krlite.equator.base.Animation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Animation("main")
public class EquatorAnimation implements ModInitializer {
	public static final String NAME = "Equator:Animation", ID = "equator-animation";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}
}
