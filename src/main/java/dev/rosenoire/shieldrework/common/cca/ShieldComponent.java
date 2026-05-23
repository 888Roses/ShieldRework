package dev.rosenoire.shieldrework.common.cca;

import dev.rosenoire.shieldrework.common.index.ModDataComponentTypes;
import dev.rosenoire.shieldrework.common.index.ModEntityComponents;
import dev.rosenoire.shieldrework.common.index.ModItemTags;
import dev.rosenoire.shieldrework.common.index.ModSounds;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class ShieldComponent implements Component, AutoSyncedComponent, CommonTickingComponent {
    public static final float MAX_HEALTH = 60;

    public final Player player;
    private float currentHealth;
    private float lastHitTick;

    public ShieldComponent(Player player) {
        this.player = player;
        this.currentHealth = MAX_HEALTH;
    }

    public static float getDamageMultiplier(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return 1f;
        }

        return itemStack.is(ModItemTags.EXTRA_SHIELD_DAMAGE) ? 4.5f : 1f;
    }

    public void sync() {
        ModEntityComponents.SHIELD.sync(this.player);
    }

    public static ShieldComponent get(Player player) {
        return ModEntityComponents.SHIELD.get(player);
    }

    @Override
    public void readData(ValueInput readView) {
        this.currentHealth = readView.getFloatOr("currentHealth", 0f);
        this.lastHitTick = readView.getFloatOr("lastHitTick", 0f);
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putFloat("currentHealth", this.currentHealth);
        writeView.putFloat("lastHitTick", this.lastHitTick);
    }

    public void setHealth(float health) {
        this.currentHealth = Mth.clamp(health, 0, MAX_HEALTH);
        this.sync();
    }

    public void removeHealth(float amount) {
        lastHitTick = player.level().getGameTime();
        addHealth(-amount);
    }

    public void addHealth(float amount) {
        setHealth(this.currentHealth + amount);
    }

    public boolean isFullHealth() {
        return this.currentHealth == MAX_HEALTH;
    }

    public boolean isBroken() {
        return player.getCooldowns().isOnCooldown(Items.SHIELD.getDefaultInstance());
    }

    public float currentHealth() {
        return this.currentHealth;
    }

    public float currentHealthProgress() {
        return getHealthProgress(this.currentHealth);
    }

    public float getHealthProgress(float health) {
        return health / MAX_HEALTH;
    }

    public boolean canRegenerateHealth() {
        return !isBroken() && player.level().getGameTime() - lastHitTick > 60;
    }

    public float lastHitTick() {
        return lastHitTick;
    }

    @Override
    public void tick() {
        if (player.isUsingItem() && player.getUseItem().is(Items.SHIELD)) {
            player.getUseItem().set(ModDataComponentTypes.OWNER, player.getUUID());
        }

        sync();

        if (!canRegenerateHealth()) {
            return;
        }

        if (!isFullHealth()) {
            addHealth(1);
        }
    }

    public boolean damage(Player player, InteractionHand hand, ItemStack itemStack, float itemDamage) {
        float lastHealth = currentHealth();
        lastHitTick = player.level().getGameTime();
        removeHealth(itemDamage);

        float lastHealthProgress = getHealthProgress(lastHealth);
        float currentHealthProgress = getHealthProgress(currentHealth);
        float closestMultiple = Mth.roundToward(Mth.ceil(currentHealthProgress * 100), 25) / 100f;

        if (lastHealthProgress > closestMultiple && currentHealthProgress <= closestMultiple && currentHealth > 0) {
            float pitchIncrease = (1f - currentHealthProgress) * 0.35f;

            player.level().playSound(
                    null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.SHIELD_DAMAGED, player.getSoundSource(),
                    1.2F, 0.9F + player.level().random.nextFloat() * pitchIncrease
            );
        }

        boolean broke = currentHealth <= 0;

        if (itemStack.is(ModItemTags.EXTRA_SHIELD_DAMAGE)) {
            player.invulnerableTime = 10;
        }

        if (broke) {
            player.getCooldowns().addCooldown(itemStack, 20 * 5);
            player.releaseUsingItem();
        }

        return broke;
    }
}