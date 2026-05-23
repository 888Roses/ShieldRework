package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.rosenoire.shieldrework.common.ShieldRework;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import dev.rosenoire.shieldrework.common.index.ModDataComponentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;
import java.util.UUID;

@Mixin(ShieldSpecialRenderer.class)
public class ShieldModelRendererMixin {
    @Shadow
    @Final
    private ShieldModel model;

    @Shadow
    @Final
    private MaterialSet materials;

    @Unique
    private static final Material[] SHIELDS = new Material[]{
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 1)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 2)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 3)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 4)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 5)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 6)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 7)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 8)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 9)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_" + 10)),
    };

    @Unique
    private static final Material[] PATTERN_SHIELDS = new Material[]{
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 1)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 2)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 3)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 4)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 5)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 6)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 7)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 8)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 9)),
            new Material(Sheets.SHIELD_SHEET, ShieldRework.id("entity/shield/shield_base_pattern_" + 10)),
    };

    @WrapMethod(method = "submit(Lnet/minecraft/core/component/DataComponentMap;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V")
    private void shieldRework$render(DataComponentMap componentMap,
                                     ItemDisplayContext itemDisplayContext,
                                     PoseStack matrixStack,
                                     SubmitNodeCollector orderedRenderCommandQueue,
                                     int light,
                                     int overlay,
                                     boolean hasGlint,
                                     int outline,
                                     Operation<Void> original) {

        matrixStack.pushPose();
        matrixStack.scale(1.0F, -1.0F, -1.0F);

        BannerPatternLayers bannerPatternsComponent = componentMap != null
                ? componentMap.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
                : BannerPatternLayers.EMPTY;

        DyeColor dyeColor = componentMap != null ? componentMap.get(DataComponents.BASE_COLOR) : null;

        boolean hasPattern = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;
        Material shieldSprite = hasPattern ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;

        @Nullable UUID ownerUuid = componentMap != null
                ? componentMap.getOrDefault(ModDataComponentTypes.OWNER, null)
                : null;

        if (ownerUuid != null) {
            Minecraft client = Minecraft.getInstance();
            if (client.level != null) {
                Player owner = client.level.getPlayerByUUID(ownerUuid);
                ShieldComponent component = ShieldComponent.get(owner);

                int multiple = Mth.clamp(11 - Mth.roundToward(Mth.ceil(component.currentHealthProgress() * 100), 10) / 10, 1, 10);

                if (!hasPattern) {
                    shieldSprite = SHIELDS[multiple - 1];
                } else {
                    shieldSprite = PATTERN_SHIELDS[multiple - 1];
                }
            }
        }

        orderedRenderCommandQueue.submitModelPart(
                this.model.handle(),
                matrixStack,
                this.model.renderType(shieldSprite.atlasLocation()),
                light,
                overlay,
                this.materials.get(shieldSprite),
                false,
                false,
                -1,
                null,
                outline
        );

        if (hasPattern) {
            BannerRenderer.submitPatterns(
                    this.materials,
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
                    this.model.plate(),
                    matrixStack,
                    this.model.renderType(shieldSprite.atlasLocation()),
                    light,
                    overlay,
                    this.materials.get(shieldSprite),
                    false,
                    hasGlint,
                    -1,
                    null,
                    outline
            );
        }

        matrixStack.popPose();
    }
}
