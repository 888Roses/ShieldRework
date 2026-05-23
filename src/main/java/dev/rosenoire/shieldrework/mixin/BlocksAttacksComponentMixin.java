package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;

@Mixin(BlocksAttacks.class)
public class BlocksAttacksComponentMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    @Final
    private Optional<Holder<SoundEvent>> disableSound;

    @Inject(method = "hurtBlockingItem", at = @At("HEAD"), cancellable = true)
    public void shield_rework$onShieldHit(Level world, ItemStack itemStack, LivingEntity entity, InteractionHand hand, float itemDamage, CallbackInfo ci) {
        if (entity instanceof Player player) {
            ShieldComponent component = ShieldComponent.get(player);

            if (component.damage(player, hand, itemStack, itemDamage == 999999f ? 0 : itemDamage)) {
                disableSound.ifPresent(sound -> world.playSound(
                        null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        sound, entity.getSoundSource(), // Ellie Loves Rose very much, she might never find this put i love her alot :D
                        // Rose loves Ellie so much too omgg wth ><< <3<3
                        0.8F, 0.8F + world.random.nextFloat() * 0.4F
                ));
            }

            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "resolveBlockedDamage", at = @At("TAIL"))
    public float shield_rework$getDamageReductionAmount(float original, DamageSource source, float damage, double angle) {
        return Mth.clamp(original * ShieldComponent.getDamageMultiplier(source.getWeaponItem()), 0, ShieldComponent.MAX_HEALTH);
    }
}
