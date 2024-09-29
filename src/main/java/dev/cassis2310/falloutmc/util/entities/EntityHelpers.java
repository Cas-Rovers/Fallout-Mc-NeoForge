package dev.cassis2310.falloutmc.util.entities;

import dev.cassis2310.falloutmc.util.lists.RandomHelpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class EntityHelpers {

    /**
     * Checks if an entity has moved since the last tick.
     * This method is particularly useful for blocks or items that react to movement, such as Powder Snow.
     *
     * @param entity The entity to check.
     * @return True if the entity has moved, otherwise false.
     */
    public static boolean hasMoved(Entity entity) {
        return entity.xOld != entity.getX() && entity.zOld != entity.getZ();
    }

    /**
     * Rotates an entity around a specified origin at a given speed if the entity is on the ground.
     * This method ensures the entity's rotation and movement are handled properly both on the client and server sides.
     *
     * @param level  The level (world) where the entity is located.
     * @param entity The entity to rotate.
     * @param origin The origin point around which the entity should rotate.
     * @param speed  The speed of rotation.
     */
    public static void rotateEntity(Level level, Entity entity, Vec3 origin, float speed) {
        if (!entity.onGround() || entity.getDeltaMovement().y > 0 || speed == 0f) {
            return;
        }
        final float rot = (entity.getYHeadRot() + speed) % 360f;
        entity.setYRot(rot);
        if (level.isClientSide && entity instanceof Player) {
            final Vec3 offset = entity.position().subtract(origin).normalize();
            final Vec3 movement = new Vec3(-offset.z, 0, offset.x).scale(speed / 48f);
            entity.hurtMarked = true; // rsync movement
            return;
        }
        if (entity instanceof LivingEntity living) {
            entity.setYHeadRot(rot);
            entity.setYBodyRot(rot);
            entity.setOnGround(false);
            living.setNoActionTime(20);
            living.hurtMarked = true;
        }
    }

    /**
     * Checks if the given Entity matches the specified Tag.
     *
     * @param entity the Entity to check
     * @param tag    the Tag to match
     * @return true if the Entity matches the Tag, false otherwise
     */
    public static boolean isEntity(Entity entity, TagKey<EntityType<?>> tag) {
        return isEntity(entity.getType(), tag);
    }

    /**
     * Checks if the given EntityType matches the specified Tag.
     *
     * @param entity the EntityType to check
     * @param tag    the Tag to match
     * @return true if the EntityType matches the Tag, false otherwise
     */
    public static boolean isEntity(EntityType<?> entity, TagKey<EntityType<?>> tag) {
        return entity.is(tag);
    }

    /**
     * Returns a random EntityType from the specified Tag.
     *
     * @param tag    the Tag to select from
     * @param random the RandomSource to use for selection
     * @return a random EntityType from the Tag, or an empty Optional if the Tag is empty
     */
    public static Optional<EntityType<?>> randomEntity(TagKey<EntityType<?>> tag, RandomSource random) {
        return RandomHelpers.getRandomElement(BuiltInRegistries.ENTITY_TYPE, tag, random);
    }

}
