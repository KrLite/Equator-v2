package net.krlite.equator.render.base;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.Box;
import org.jetbrains.annotations.Nullable;

public interface Renderable {
	boolean isRenderable();

	static boolean isBoxLegal(@Nullable Box box) {
		return box != null && Theory.looseGreater(box.area(), 0);
	}
}
