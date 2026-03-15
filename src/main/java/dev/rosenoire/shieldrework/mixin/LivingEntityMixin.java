package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "getDamageBlockedAmount", at= @At(value = "INVOKE", target = "Lnet/minecraft/component/type/BlocksAttacksComponent;getDamageReductionAmount(Lnet/minecraft/entity/damage/DamageSource;FD)F"))
    private float shieldRework$getDamageBlockedAmount(BlocksAttacksComponent instance, DamageSource source, float damage, double angle, Operation<Float> original) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof PlayerEntity player) {
            ShieldComponent shield = ShieldComponent.get(player);

            if (shield.isBroken() || shield.currentHealth() <= 0) {
                return original.call(instance, source, damage, angle) / 4.5f;
            }
        }

        return original.call(instance, source, damage, angle);
    }
}
