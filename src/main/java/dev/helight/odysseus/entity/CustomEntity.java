package dev.helight.odysseus.entity;

import dev.helight.odysseus.item.Persistence;
import dev.helight.odysseus.registry.Registrable;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CustomEntity implements Registrable {

    public final EntityType type;

    private Double health;
    private Double speed;
    private Double damage;
    private Double knockback;

    @Override
    public String registeredId() {
        return getClass().getName();
    }

    @Override
    public UUID registeredUuid() {
        return UUID.nameUUIDFromBytes(getClass().getName().getBytes());
    }

    @Getter
    private final List<EntityAbility> abilities = new ArrayList<>();

    public CustomEntity(EntityType type) {
        this.type = type;
    }

    public final void warmup(Entity entity) {
        for (EntityAbility ability : getAbilities()) {
            ability.start(this, asLiving(entity));
        }
    }

    public void loadEntity(Entity entity) {}

    public void unloadEntity(Entity entity) {}

    public void setupEntity(Entity entity) {}

    public CustomEntity health(double health) {
        this.health = health;
        return this;
    }

    public CustomEntity speed(double speed) {
        this.speed = speed;
        return this;
    }

    public CustomEntity damage(double addition) {
        this.damage = addition;
        return this;
    }

    public CustomEntity knockback(double knockback) {
        this.knockback = knockback;
        return this;
    }

    private Attributable asAttributable(Entity entity) {
        return (Attributable) entity;
    }

    private LivingEntity asLiving(Entity entity) {
        return (LivingEntity) entity;
    }

    public void spawn(Location location) {
        Entity entity = location.getWorld().spawnEntity(location, type);
        Persistence.store("CEType", getClass().getName(), entity.getPersistentDataContainer());

        if (health != null) {
            asAttributable(entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            asLiving(entity).setHealth(health);
        }

        if (damage != null) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "DmgIncrement", damage, AttributeModifier.Operation.ADD_NUMBER);
            asAttributable(entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(modifier);
        }

        if (speed != null) asAttributable(entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        if (knockback != null) asAttributable(entity).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockback);

        warmup(entity);
        setupEntity(entity);
    }

    public void register() {
        CustomEntityRegistry.registry.put(getClass().getName(), this);
    }

}
