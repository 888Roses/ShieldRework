package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.rosenoire.shieldrework.common.SRHelper;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import dev.rosenoire.shieldrework.common.index.ModGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.BlocksAttacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(
            method = "applyItemBlocking",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/component/BlocksAttacks;resolveBlockedDamage(Lnet/minecraft/world/damagesource/DamageSource;FD)F"
            )
    )
    private float applyItemBlocking$getBlockedAmount(
            BlocksAttacks instance,
            DamageSource source,
            float dealtDamage,
            double angle,
            Operation<Float> original
    ) {
        var livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof Player player) {
            var component = ShieldComponent.get(player);

            if (Math.toDegrees(angle) > 70) {
                return 0;
            }

            if (player.level() instanceof ServerLevel serverWorld) {
                if (serverWorld.getGameRules().get(ModGameRules.SHIELD_DAMAGE_AXE_ONLY_GAMERULE)) {
                    if (source.getWeaponItem() == null || !source.getWeaponItem().is(ItemTags.AXES)) {
                        return SRHelper.AXE_BREAKING_DAMAGE;
                    }
                }
            }

            if (component.isBroken() || component.currentHealth() <= 0) {
                return original.call(instance, source, dealtDamage, angle) / 4.5f;
            }
        }

        return original.call(instance, source, dealtDamage, angle);
    }
}
