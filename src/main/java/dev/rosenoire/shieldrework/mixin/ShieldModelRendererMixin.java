package dev.rosenoire.shieldrework.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.rosenoire.shieldrework.common.SRHelper;
import dev.rosenoire.shieldrework.common.ShieldRework;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ShieldSpecialRenderer.class)
public class ShieldModelRendererMixin {
    @Shadow
    @Final
    private ShieldModel model;

    @Shadow
    @Final
    private SpriteGetter sprites;

    @Unique
    private static Optional<SpriteId> createBrokenShieldSpriteId(int index) {
        return Optional.of(Sheets.SHIELD_MAPPER.apply(ShieldRework.id("broken_shield_" + index)));
    }

    @Unique
    private static final ImmutableList<Optional<SpriteId>> BROKEN_SHIELDS = ImmutableList.<Optional<SpriteId>>builder()
            .add(Optional.empty())
            .add(createBrokenShieldSpriteId(0))
            .add(createBrokenShieldSpriteId(1))
            .add(createBrokenShieldSpriteId(2))
            .add(createBrokenShieldSpriteId(3))
            .add(createBrokenShieldSpriteId(4))
            .add(createBrokenShieldSpriteId(5))
            .add(createBrokenShieldSpriteId(6))
            .add(createBrokenShieldSpriteId(7))
            .add(createBrokenShieldSpriteId(8))
            .add(createBrokenShieldSpriteId(9))
            .build();

    @Unique
    private static @Nullable SpriteId getBrokenShieldSprite(int index) {
        index = Math.clamp(index, 0, BROKEN_SHIELDS.size() - 1);
        return BROKEN_SHIELDS.get(index).orElse(null);
    }

    @Unique
    private static @Nullable SpriteId getBrokenShieldSprite(float delta) {
        return getBrokenShieldSprite(Mth.ceil((1f - SRHelper.clamp01(delta)) * (BROKEN_SHIELDS.size() - 1)));
    }

    @Inject(
            method = "submit(Lnet/minecraft/core/component/DataComponentMap;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V",
            at = @At("TAIL")
    )
    private void shieldRework$render(
            DataComponentMap components,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor,
            CallbackInfo ci
    ) {
        var client = Minecraft.getInstance();
        var level = client.level;

        if (level == null) {
            return;
        }

        Optional.ofNullable(components)
                .flatMap(SRHelper::optionalOwner)
                .ifPresent(uuid -> {
                    var owner = level.getPlayerByUUID(uuid);
                    if (owner == null) return;

                    var component = ShieldComponent.get(owner);
                    var sprite = getBrokenShieldSprite(component.currentHealthProgress());

                    if (sprite == null) {
                        return;
                    }

                    var texture = this.sprites.get(sprite);
                    var atlasLocation = texture.atlasLocation();

                    submitNodeCollector.submitModel(
                            this.model,
                            Unit.INSTANCE,
                            poseStack,
                            RenderTypes.entityTranslucent(atlasLocation),
                            lightCoords,
                            overlayCoords,
                            -1,
                            texture,
                            outlineColor,
                            null
                    );
                });
    }
}
