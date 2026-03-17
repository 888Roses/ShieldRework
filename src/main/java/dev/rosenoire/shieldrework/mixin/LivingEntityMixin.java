package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import dev.rosenoire.shieldrework.common.index.ModGameRules;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "getDamageBlockedAmount", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/BlocksAttacksComponent;getDamageReductionAmount(Lnet/minecraft/entity/damage/DamageSource;FD)F"))
    private float shieldRework$getDamageBlockedAmount(BlocksAttacksComponent instance, DamageSource source, float damage, double angle, Operation<Float> original) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof PlayerEntity player) {
            ShieldComponent shield = ShieldComponent.get(player);

            if (Math.toDegrees(angle) > 70) {
                return 0;
            }

            if (player.getEntityWorld() instanceof ServerWorld serverWorld) {
                if (serverWorld.getGameRules().getValue(ModGameRules.SHIELD_DAMAGE_AXE_ONLY_GAMERULE)) {
                    if (source.getWeaponStack() == null || !source.getWeaponStack().isIn(ItemTags.AXES)) {
                        return 999999f;
                    }
                }
            }

            if (shield.isBroken() || shield.currentHealth() <= 0) {
                return original.call(instance, source, damage, angle) / 4.5f;
            }
        }

        return original.call(instance, source, damage, angle);
    }
}
