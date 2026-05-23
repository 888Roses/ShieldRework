package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import dev.rosenoire.shieldrework.common.cca.ShieldComponent;
import net.minecraft.world.entity.player.Player;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class ModEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<ShieldComponent> SHIELD = ComponentRegistry.getOrCreate(
            ShieldRework.id("shield"),
            ShieldComponent.class
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Player.class, SHIELD, ShieldComponent::new);
    }
}
