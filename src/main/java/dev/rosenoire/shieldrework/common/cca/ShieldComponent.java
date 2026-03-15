package dev.rosenoire.shieldrework.common.cca;

import dev.rosenoire.shieldrework.common.index.ModDataComponentTypes;
import dev.rosenoire.shieldrework.common.index.ModEntityComponents;
import dev.rosenoire.shieldrework.common.index.ModItemTags;
import dev.rosenoire.shieldrework.common.index.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.Gizmo;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class ShieldComponent implements Component, AutoSyncedComponent, CommonTickingComponent {
    public static final float MAX_HEALTH = 80;

    public final PlayerEntity player;
    private float currentHealth;
    private float lastHitTick;

    public ShieldComponent(PlayerEntity player) {
        this.player = player;
        this.currentHealth = MAX_HEALTH;
    }

    public static float getDamageMultiplier(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return 1f;
        }

        return itemStack.isIn(ModItemTags.EXTRA_SHIELD_DAMAGE) ? 4.5f : 1f;
    }

    public void sync() {
        ModEntityComponents.SHIELD.sync(this.player);
    }

    public static ShieldComponent get(PlayerEntity player) {
        return ModEntityComponents.SHIELD.get(player);
    }

    @Override
    public void readData(ReadView readView) {
        this.currentHealth = readView.getFloat("currentHealth", 0f);
        this.lastHitTick = readView.getFloat("lastHitTick", 0f);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putFloat("currentHealth", this.currentHealth);
        writeView.putFloat("lastHitTick", this.lastHitTick);
    }

    public void setHealth(float health) {
        this.currentHealth = MathHelper.clamp(health, 0, MAX_HEALTH);
        this.sync();
    }

    public void removeHealth(float amount) {
        lastHitTick = player.getEntityWorld().getTime();
        addHealth(-amount);
    }

    public void addHealth(float amount) {
        setHealth(this.currentHealth + amount);
    }

    public boolean isFullHealth() {
        return this.currentHealth == MAX_HEALTH;
    }

    public boolean isBroken() {
        return player.getItemCooldownManager().isCoolingDown(Items.SHIELD.getDefaultStack());
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
        return !isBroken() && player.getEntityWorld().getTime() - lastHitTick > 40;
    }

    public float lastHitTick() {
        return lastHitTick;
    }

    @Override
    public void tick() {
        // if (player.getEntityWorld().isClient()) {
        //     GizmoDrawing.text("Current Health: " + MathHelper.roundDownToMultiple(currentHealth() * 10f, 10) / 10f, player.getEntityPos().add(0, 3.2, 0), TextGizmo.Style.left(0xffffffff));
        //     GizmoDrawing.text("Current Progress: " + MathHelper.roundDownToMultiple(currentHealthProgress() * 10f, 10) / 10f, player.getEntityPos().add(0, 3, 0), TextGizmo.Style.left(0xffffffff));
        //     GizmoDrawing.text("Can Regenerate Health: " + canRegenerateHealth(), player.getEntityPos().add(0, 2.8, 0), TextGizmo.Style.left(0xffffffff));
        //
        //     float closestMultiple = MathHelper.roundUpToMultiple(MathHelper.ceil(currentHealthProgress() * 100), 10) / 100f;
        //     int multiple = MathHelper.clamp(11 - MathHelper.roundUpToMultiple(MathHelper.ceil(currentHealthProgress() * 100), 10) / 10, 1, 10);
        //     GizmoDrawing.text("Closest Multiple: " + multiple, player.getEntityPos().add(0, 2.6, 0), TextGizmo.Style.left(0xffffffff));
        // }

        if (player.isUsingItem() && player.getActiveItem().isOf(Items.SHIELD)) {
            player.getActiveItem().set(ModDataComponentTypes.OWNER, player.getUuid());
        }

        sync();

        if (!canRegenerateHealth()) {
            return;
        }

        if (!isFullHealth()) {
            addHealth(1);
        }
    }

    public boolean damage(PlayerEntity player, Hand hand, ItemStack itemStack, float itemDamage) {
        float lastHealth = currentHealth();
        removeHealth(itemDamage);

        float lastHealthProgress = getHealthProgress(lastHealth);
        float currentHealthProgress = currentHealthProgress();
        float closestMultiple = MathHelper.roundUpToMultiple(MathHelper.ceil(currentHealthProgress * 100), 25) / 100f;

        if (lastHealthProgress > closestMultiple && currentHealthProgress <= closestMultiple && currentHealth() > 0) {
            float pitchIncrease = (1f - currentHealthProgress) * 0.35f;

            player.getEntityWorld().playSound(
                    null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.SHIELD_DAMAGED, player.getSoundCategory(),
                    1.2F, 0.9F + player.getEntityWorld().random.nextFloat() * pitchIncrease
            );
        }

        boolean broke = currentHealth <= 0;

        if (itemStack.isIn(ModItemTags.EXTRA_SHIELD_DAMAGE)) {
            player.timeUntilRegen = 10;
        }

        if (broke) {
            player.getItemCooldownManager().set(itemStack, 20 * 5);
            player.stopUsingItem();
        }

        return broke;
    }
}