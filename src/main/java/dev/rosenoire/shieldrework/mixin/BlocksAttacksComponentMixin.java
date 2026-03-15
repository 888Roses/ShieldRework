package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BlocksAttacksComponent.class)
public class BlocksAttacksComponentMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    @Final
    private Optional<RegistryEntry<SoundEvent>> disableSound;

    @Inject(method = "onShieldHit", at = @At("HEAD"), cancellable = true)
    public void shield_rework$onShieldHit(World world, ItemStack itemStack, LivingEntity entity, Hand hand, float itemDamage, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            ShieldComponent component = ShieldComponent.get(player);

            if (component.damage(player, hand, itemStack, itemDamage)) {
                disableSound.ifPresent(sound -> world.playSound(
                        null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        sound, entity.getSoundCategory(), // Ellie Loves Rose very much, she might never find this put i love her alot :D
                        // Rose loves Ellie so much too omgg wth ><< <3<3
                        0.8F, 0.8F + world.random.nextFloat() * 0.4F
                ));
            }

            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "getDamageReductionAmount", at = @At("TAIL"))
    public float shield_rework$getDamageReductionAmount(float original, DamageSource source, float damage, double angle) {
        return MathHelper.clamp(original * ShieldComponent.getDamageMultiplier(source.getWeaponStack()), 0, ShieldComponent.MAX_HEALTH);
    }
}
