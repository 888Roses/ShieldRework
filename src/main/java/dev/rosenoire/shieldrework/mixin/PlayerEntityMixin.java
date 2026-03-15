package dev.rosenoire.shieldrework.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "takeShieldHit", at = @At("HEAD"), cancellable = true)
    public void shield_rework$takeShieldHit(ServerWorld world, LivingEntity attacker, CallbackInfo ci) {
        attacker.knockback((PlayerEntity) (Object) this);
        ci.cancel();
    }
}
