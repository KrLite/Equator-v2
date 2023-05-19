package net.krlite.equator.render;

import net.krlite.equator.base.Exceptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFWImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

public class RenderManager {
	public static int[] getSize(Identifier identifier) {
		Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
		if (resource.isEmpty()) {
			throw new RuntimeException(new Exceptions.IdentifierNotFoundException(identifier));
		}

		try {
			InputStream stream = resource.get().getInputStream();
			BufferedImage textureImage = ImageIO.read(stream);
			return new int[] { textureImage.getWidth(), textureImage.getHeight() };
		} catch (IOException ioException) {
			throw new RuntimeException(new Exceptions.IdentifierFailedToLoadException(identifier, ioException));
		}
	}

	public static int getWidth(Identifier identifier) {
		return getSize(identifier)[0];
	}

	public static int getHeight(Identifier identifier) {
		return getSize(identifier)[1];
	}

	public static ByteBuffer getByteBuffer(Identifier identifier) {
		Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
		if (resource.isEmpty()) {
			throw new RuntimeException(new Exceptions.IdentifierNotFoundException(identifier));
		}

		try {
			InputStream stream = resource.get().getInputStream();
			BufferedImage textureImage = ImageIO.read(stream);

			int width = textureImage.getWidth();
			int height = textureImage.getHeight();
			int[] pixels = textureImage.getRGB(0, 0, width, height, null, 0, width);
			ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

			for (int y = height - 1; y >= 0; y--) {
				for (int x = 0; x < width; x++) {
					int pixel = pixels[y * width + x];
					buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
					buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
					buffer.put((byte) (pixel & 0xFF));         // Blue component
					buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
				}
			}

			buffer.flip();
			return buffer;
		} catch (IOException ioException) {
			throw new RuntimeException(new Exceptions.IdentifierFailedToLoadException(identifier, ioException));
		}
	}

	public static GLFWImage getGLFWImage(Identifier identifier) {
		int width = getWidth(identifier);
		int height = getHeight(identifier);

		GLFWImage image = GLFWImage.malloc();
		image.set(width, height, getByteBuffer(identifier));

		return image;
	}

	public static int getGlId(Identifier identifier) {
		return MinecraftClient.getInstance().getTextureManager().getTexture(identifier).getGlId();
	}
}
