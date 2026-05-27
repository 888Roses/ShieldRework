package dev.rosenoire.shieldrework.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin {
    @Inject(method = "blockUsingItem", at = @At("HEAD"), cancellable = true)
    public void shield_rework$takeShieldHit(ServerLevel level, LivingEntity attacker, CallbackInfo ci) {
        attacker.blockedByItem((Player) (Object) this);
        ci.cancel();
    }
}
