package dev.rosenoire.shieldrework.common;

import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import dev.rosenoire.shieldrework.common.index.ModItemTags;
import dev.rosenoire.shieldrework.common.index.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ShieldRework implements ModInitializer {
    public static final String MOD_ID = "shield_rework";

    public static Identifier id(String shield) {
        return Identifier.of(MOD_ID, shield);
    }

    @Override
    public void onInitialize() {
        ModItemTags.register();
        ModSounds.register();

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((livingEntity, damageSource, v) -> {
            if (livingEntity instanceof PlayerEntity player) {
                ShieldComponent component = ShieldComponent.get(player);
                float delay = livingEntity.getEntityWorld().getTime() - component.lastHitTick();

                // if (damageSource.getAttacker() instanceof PlayerEntity attacker) {
                //     attacker.sendMessage(Text.literal("Delay: " + delay), false);
                // }

                if (player.isUsingItem() || component.currentHealth() <= 0) {
                    return delay > 10;
                }
            }

            return true;
        });
    }
}
