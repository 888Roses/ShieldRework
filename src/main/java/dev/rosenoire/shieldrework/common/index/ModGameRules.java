package dev.rosenoire.shieldrework.common.index;

import dev.rosenoire.shieldrework.common.ShieldRework;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

public interface ModGameRules {
    Identifier SHIELD_DAMAGE_AXE_ONLY = ShieldRework.id("shield_damage_axe_only");
    GameRule<Boolean> SHIELD_DAMAGE_AXE_ONLY_GAMERULE = GameRuleBuilder.forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(SHIELD_DAMAGE_AXE_ONLY);

    static void register() {
    }
}
