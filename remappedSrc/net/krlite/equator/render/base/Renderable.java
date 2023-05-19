package net.krlite.equator.render.base;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.flat.Box;
import org.jetbrains.annotations.Nullable;

public interface Renderable {
	void render();
	boolean isRenderable();

	static boolean isLegal(@Nullable Box box) {
		return box != null && Theory.looseGreater(box.area(), 0);
	}
}
