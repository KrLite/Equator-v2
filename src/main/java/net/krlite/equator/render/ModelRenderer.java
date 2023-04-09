package net.krlite.equator.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.math.geometry.Box;
import net.krlite.equator.render.base.Renderable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

import java.util.function.UnaryOperator;

public record ModelRenderer(
		Box box, Quaterniondc modifier,
		@Nullable ItemStack itemStack, @Nullable BlockState blockState, boolean leftHanded
) implements Renderable {
	public ModelRenderer(Box box, Quaterniondc modifier, @Nullable ItemStack itemStack, boolean leftHanded) {
		this(box, modifier, itemStack, null, leftHanded);
	}

	public ModelRenderer(Box box, Quaterniondc modifier, @Nullable BlockState blockState, boolean leftHanded) {
		this(box, modifier, null, blockState, leftHanded);
	}

	public ModelRenderer(Box box) {
		this(box, new Quaterniond(), null, null, false);
	}

	// box() is a record method

	// modifier() is a record method

	// itemStack() is a record method

	// blockState() is a record method

	// leftHanded() is a record method

	public ModelRenderer modifier(Quaterniondc modifier) {
		return hasItem()
					   ? new ModelRenderer(box, modifier, itemStack, leftHanded)
					   : new ModelRenderer(box, modifier, blockState, leftHanded);
	}

	public ModelRenderer modifier(UnaryOperator<Quaterniondc> modifier) {
		return modifier(modifier.apply(modifier()));
	}

	public ModelRenderer removeModifier() {
		return new ModelRenderer(box, new Quaterniond(), itemStack, leftHanded);
	}

	public ModelRenderer model(ItemStack itemStack) {
		return new ModelRenderer(box, modifier, itemStack, leftHanded);
	}

	public ModelRenderer model(Item item) {
		return model(item.getDefaultStack());
	}

	public ModelRenderer model(BlockState blockState) {
		return new ModelRenderer(box, modifier, blockState, leftHanded);
	}

	public ModelRenderer model(Block block) {
		return model(block.getDefaultState());
	}

	public ModelRenderer removeModel() {
		return new ModelRenderer(box).modifier(modifier).leftHanded(leftHanded);
	}

	public ModelRenderer leftHanded(boolean leftHanded) {
		return new ModelRenderer(box, modifier, itemStack, leftHanded);
	}

	public boolean hasItem() {
		return itemStack != null;
	}

	public boolean hasBlock() {
		return blockState != null;
	}

	@Override
	public boolean isRenderable() {
		return Renderable.isBoxLegal(box()) && (hasItem() || hasBlock()) && !(hasItem() && hasBlock());
	}

	@SuppressWarnings("deprecation")
	private void prepareModel() {
		MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	private void applyModelView(MatrixStack matrixStack) {
		matrixStack.scale(1, -1, 1);
		matrixStack.scale((float) box().w(), (float) box().h(), 1);
		matrixStack.multiply(new Quaternionf(modifier()));

		RenderSystem.applyModelViewMatrix();
	}

	public void render(float z) {
		if (!isRenderable()) {
			return;
		}

		BakedModel bakedModel = null;
		if (hasItem()) // Item: bake model
			bakedModel = MinecraftClient.getInstance().getItemRenderer().getModel(itemStack(), null, null, 0);

		prepareModel();
		MatrixStack matrixStack = RenderSystem.getModelViewStack();

		matrixStack.push();
		matrixStack.translate(box().center().x(), box().center().y(), z);
		applyModelView(matrixStack);
		MatrixStack modelMatrixStack = new MatrixStack();

		if (bakedModel == null) // Block: translate to center
			modelMatrixStack.translate(-0.5, -0.5, -0.5);

		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

		if (bakedModel != null && !bakedModel.isSideLit()) // Item: disable lighting
			DiffuseLighting.disableGuiDepthLighting();

		if (bakedModel != null) // Item: render item model
			MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack(), ModelTransformationMode.GUI,
					leftHanded(), modelMatrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel);
		else // Block: render block model
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState(), modelMatrixStack,
					immediate, 0xF000F0, OverlayTexture.DEFAULT_UV);

		immediate.draw();
		RenderSystem.enableDepthTest();

		if (bakedModel != null && !bakedModel.isSideLit()) // Item: re-enable lighting
			DiffuseLighting.enableGuiDepthLighting();

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
	}

	public void render() {
		render(100);
	}
}
