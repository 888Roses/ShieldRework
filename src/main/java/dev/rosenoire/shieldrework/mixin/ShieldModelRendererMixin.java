package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.rosenoire.shieldrework.common.SRHelper;
import dev.rosenoire.shieldrework.common.ShieldRework;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ShieldSpecialRenderer.class)
public class ShieldModelRendererMixin {
    @Unique
    private static final SpriteId[] SHIELDS = SRHelper.make(10, SpriteId[]::new, i ->
            Sheets.SHIELD_MAPPER.apply(ShieldRework.id("broken_shield_" + (i + 1)))
    );

    @Unique
    private static final SpriteId[] PATTERN_SHIELDS = SRHelper.make(10, SpriteId[]::new, i ->
            Sheets.SHIELD_MAPPER.apply(ShieldRework.id("broken_shield_pattern_" + (i + 1)))
    );

    @WrapOperation(
            method = "submit(Lnet/minecraft/core/component/DataComponentMap;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;IIILnet/minecraft/client/resources/model/sprite/SpriteId;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
                    ordinal = 0
            )
    )
    private <S> void shieldRework$render(
            SubmitNodeCollector instance,
            Model<S> model,
            S state,
            PoseStack poseStack,
            int lightCoords,
            int overlayCoords,
            int tintedColor,
            SpriteId sprite,
            SpriteGetter sprites,
            int outlineColor,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay,
            Operation<Void> original,
            @Local(argsOnly = true) @Nullable DataComponentMap components
    ) {
        var isPattern = sprite != Sheets.SHIELD_BASE_NO_PATTERN;
        var client = Minecraft.getInstance();
        var level = client.level;

        if (level != null) {
            sprite = Optional.ofNullable(components)
                    .flatMap(SRHelper::optionalOwner)
                    .map(uuid -> {
                        var owner = level.getPlayerByUUID(uuid);
                        if (owner == null) {
                            return null;
                        }

                        var component = ShieldComponent.get(owner);
                        var healthPercentage = Mth.ceil(10 - component.currentHealthProgress() * 10) - 1;

                        if (healthPercentage < 0) {
                            return null;
                        }

                        var brokenIndex = Mth.clamp(healthPercentage, 1, 10);
                        return isPattern ? PATTERN_SHIELDS[brokenIndex] : SHIELDS[brokenIndex];
                    })
                    .orElse(sprite);
        }

        original.call(
                instance,
                model,
                state,
                poseStack,
                lightCoords,
                overlayCoords,
                tintedColor,
                sprite,
                sprites,
                outlineColor,
                crumblingOverlay
        );
    }
}
