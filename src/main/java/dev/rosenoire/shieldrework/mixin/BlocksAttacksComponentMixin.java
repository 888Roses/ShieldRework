package dev.rosenoire.shieldrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.rosenoire.shieldrework.common.SRHelper;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
    public void hurtBlockingItem$hurtShieldComponent(
            Level level,
            ItemStack item,
            LivingEntity user,
            InteractionHand hand,
            float damage,
            CallbackInfo ci
    ) {
        if (user instanceof Player player) {
            var component = ShieldComponent.get(player);

            if (component.damage(player, hand, item, damage == SRHelper.AXE_BREAKING_DAMAGE ? 0 : damage)) {
                disableSound.ifPresent(sound -> level.playSound(
                        null,
                        user.getX(), user.getY(), user.getZ(),
                        sound, user.getSoundSource(), // Ellie Loves Rose very much, she might never find this put i love her alot :D
                        // Rose loves Ellie so much too omgg wth ><< <3<3
                        0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F
                ));
            }

            ci.cancel();
        }
    }

    @WrapMethod(method = "resolveBlockedDamage")
    public float shield_rework$getDamageReductionAmount(
            DamageSource source,
            float dealtDamage,
            double angle,
            Operation<Float> original
    ) {
        return Mth.clamp(
                original.call(source, dealtDamage, angle) *
                        ShieldComponent.getDamageMultiplier(source.getWeaponItem()),
                0,
                ShieldComponent.MAX_HEALTH
        );
    }
}
