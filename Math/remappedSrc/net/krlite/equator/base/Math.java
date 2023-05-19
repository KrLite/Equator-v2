package net.krlite.equator.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <b>Math</b> module of <b>Equator.</b>
 */
@Target(ElementType.TYPE_USE)
public @interface Math {
	/**
	 * The latest updated version.
	 */
	String value();
}
