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

/**
 * <h1>ModelRenderer</h1>
 * Based on {@link Box} and contains an {@link ItemStack} <b>or</b> {@link BlockState} to hold the model of an {@link Item}
 * <b>or</b> a {@link Block}. That is, it is not possible to render both an item and a block at the same time in the same
 * model renderer. A {@link Quaterniondc Quaternion} modifier can be applied during rendering.
 * @see Box
 * @see Quaterniondc
 * @see ItemStack
 * @see BlockState
 * @param box			The {@link Box} to render the model in.
 * @param modifier		The {@link Quaterniondc Quaternion} modifier to apply during rendering.
 * @param itemStack		The {@link ItemStack} to hold the model of an item. Cannot coexist with {@link #blockState}.
 * @param blockState	The {@link BlockState} to hold the model of a block. Cannot coexist with {@link #itemStack}.
 * @param leftHanded	Whether the model should be rendered left-handed. This only affects the model's lighting.
 */
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

	@Override
	public Box box() {
		return box;
	}

	@Override
	public Quaterniondc modifier() {
		return modifier;
	}

	@Override
	public ItemStack itemStack() {
		return itemStack;
	}

	@Override
	public BlockState blockState() {
		return blockState;
	}

	@Override
	public boolean leftHanded() {
		return leftHanded;
	}

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
		if (!isRenderable()) return;

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
