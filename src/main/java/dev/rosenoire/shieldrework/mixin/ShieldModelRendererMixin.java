package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.rosenoire.shieldrework.common.ShieldRework;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import dev.rosenoire.shieldrework.common.index.ModDataComponentTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;
import java.util.UUID;

@Mixin(ShieldModelRenderer.class)
public class ShieldModelRendererMixin {
    @Shadow
    @Final
    private ShieldEntityModel model;

    @Shadow
    @Final
    private SpriteHolder spriteHolder;

    @Unique
    private static final SpriteIdentifier[] SHIELDS = new SpriteIdentifier[]{
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 1)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 2)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 3)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 4)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 5)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 6)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 7)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 8)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 9)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_" + 10)),
    };

    @Unique
    private static final SpriteIdentifier[] PATTERN_SHIELDS = new SpriteIdentifier[]{
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 1)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 2)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 3)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 4)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 5)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 6)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 7)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 8)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 9)),
            new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, ShieldRework.id("entity/shield/shield_base_pattern_" + 10)),
    };

    @WrapMethod(method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V")
    private void shieldRework$render(ComponentMap componentMap,
                                     ItemDisplayContext itemDisplayContext,
                                     MatrixStack matrixStack,
                                     OrderedRenderCommandQueue orderedRenderCommandQueue,
                                     int light,
                                     int overlay,
                                     boolean hasGlint,
                                     int outline,
                                     Operation<Void> original) {

        matrixStack.push();
        matrixStack.scale(1.0F, -1.0F, -1.0F);

        BannerPatternsComponent bannerPatternsComponent = componentMap != null
                ? componentMap.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
                : BannerPatternsComponent.DEFAULT;

        DyeColor dyeColor = componentMap != null ? componentMap.get(DataComponentTypes.BASE_COLOR) : null;

        boolean hasPattern = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;
        SpriteIdentifier shieldSprite = hasPattern ? ModelBaker.SHIELD_BASE : ModelBaker.SHIELD_BASE_NO_PATTERN;

        @Nullable UUID ownerUuid = componentMap != null
                ? componentMap.getOrDefault(ModDataComponentTypes.OWNER, null)
                : null;

        if (ownerUuid != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null) {
                PlayerEntity owner = client.world.getPlayerByUuid(ownerUuid);
                ShieldComponent component = ShieldComponent.get(owner);

                int multiple = MathHelper.clamp(11 - MathHelper.roundUpToMultiple(MathHelper.ceil(component.currentHealthProgress() * 100), 10) / 10, 1, 10);

                if (!hasPattern) {
                    shieldSprite = SHIELDS[multiple - 1];
                } else {
                    shieldSprite = PATTERN_SHIELDS[multiple - 1];
                }
            }
        }

        orderedRenderCommandQueue.submitModelPart(
                this.model.getHandle(),
                matrixStack,
                this.model.getLayer(shieldSprite.getAtlasId()),
                light,
                overlay,
                this.spriteHolder.getSprite(shieldSprite),
                false,
                false,
                -1,
                null,
                outline
        );

        if (hasPattern) {
            BannerBlockEntityRenderer.renderCanvas(
                    this.spriteHolder,
                    matrixStack,
                    orderedRenderCommandQueue,
                    light,
                    overlay,
                    this.model,
                    Unit.INSTANCE,
                    shieldSprite,
                    false,
                    Objects.requireNonNullElse(dyeColor, DyeColor.WHITE),
                    bannerPatternsComponent,
                    hasGlint,
                    null,
                    outline
            );
        } else {
            orderedRenderCommandQueue.submitModelPart(
                    this.model.getPlate(),
                    matrixStack,
                    this.model.getLayer(shieldSprite.getAtlasId()),
                    light,
                    overlay,
                    this.spriteHolder.getSprite(shieldSprite),
                    false,
                    hasGlint,
                    -1,
                    null,
                    outline
            );
        }

        matrixStack.pop();
    }
}
