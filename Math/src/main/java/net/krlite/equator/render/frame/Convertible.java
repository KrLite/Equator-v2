package net.krlite.equator.render.frame;

/**
 * A {@link Convertible} object can be <b>converted to</b> and <b>from</b> the
 * {@link FrameInfo.Convertor Scaled Coordinate},
 * {@link FrameInfo.Convertor Screen Coordinate}, and
 * {@link FrameInfo.Convertor OpenGL Coordinate}.
 * @see FrameInfo.Convertor
 * @param <C>	The type of the convertible object.
 */
public interface Convertible<C> {
	/**
	 * Fits the coordinate to the {@link FrameInfo.Convertor Scaled Coordinate}.
	 * @return	The converted coordinate.
	 * @see FrameInfo.Convertor
	 */
	C fitToScaled();

	/**
	 * Fits the coordinate to the {@link FrameInfo.Convertor Screen Coordinate}.
	 * @return	The converted coordinate.
	 * @see FrameInfo.Convertor
	 */
	C fitToScreen();

	/**
	 * Fits the coordinate to the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 * @return	The converted coordinate.
	 * @see FrameInfo.Convertor
	 */
	C fitToOpenGL();

	/**
	 * Fits the coordinate from the {@link FrameInfo.Convertor Scaled Coordinate}.
	 * @return	The converted coordinate.
	 * @see FrameInfo.Convertor
	 */
	C fitFromScaled();

	/**
	 * Fits the coordinate from the {@link FrameInfo.Convertor Screen Coordinate}.
	 * @return	The converted coordinate.
	 * @see FrameInfo.Convertor
	 */
	C fitFromScreen();

	/**
	 * Fits the coordinate from the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 * @return	The converted coordinate.
	 * @see FrameInfo.Convertor
	 */
	C fitFromOpenGL();

	/**
	 * A {@link Convertible} that is already in the {@link FrameInfo.Convertor Scaled Coordinate}.
	 * @param <C>	The type of the convertible object.
	 */
	interface Scaled<C> extends Convertible<C> {
		@Override
		default C fitToScaled() {
			return (C) this;
		}

		@Override
		default C fitFromScaled() {
			return (C) this;
		}
	}

	/**
	 * A {@link Convertible} that is already in the {@link FrameInfo.Convertor Screen Coordinate}.
	 * @param <C>	The type of the convertible object.
	 */
	interface Screen<C> extends Convertible<C> {
		@Override
		default C fitToScreen() {
			return (C) this;
		}

		@Override
		default C fitFromScreen() {
			return (C) this;
		}
	}

	/**
	 * A {@link Convertible} that is already in the {@link FrameInfo.Convertor OpenGL Coordinate}.
	 * @param <C>	The type of the convertible object.
	 */
	interface OpenGL<C> extends Convertible<C> {
		@Override
		default C fitToOpenGL() {
			return (C) this;
		}

		@Override
		default C fitFromOpenGL() {
			return (C) this;
		}
	}
}
