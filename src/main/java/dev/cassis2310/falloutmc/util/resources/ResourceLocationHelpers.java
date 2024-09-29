package dev.cassis2310.falloutmc.util.resources;

import dev.cassis2310.falloutmc.FalloutMc;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationHelpers {
    /**
     * Returns a {@link ResourceLocation} with the "falloutmc" namespace.
     * This method is commonly used to create resource paths specific to the FalloutMC mod.
     *
     * @param name The path of the resource.
     * @return A {@link ResourceLocation} with the "falloutmc" namespace.
     */
    public static ResourceLocation identifier(String name) {
        return resourceLocation(FalloutMc.MOD_ID, name);
    }

    /**
     * Returns a {@link ResourceLocation} with the "minecraft" namespace.
     * This method is useful when referencing vanilla Minecraft resources.
     *
     * @param name The path of the resource.
     * @return A {@link ResourceLocation} with the "minecraft" namespace.
     */
    public static ResourceLocation identifierMc(String name) {
        return resourceLocation("minecraft", name);
    }

    /**
     * Returns a {@link ResourceLocation} by inferring the namespace from the provided name.
     * If no namespace is specified, "minecraft" will be used by default.
     *
     * @param name The full name of the resource, including optional namespace.
     * @return A {@link ResourceLocation} with the inferred namespace.
     */
    public static ResourceLocation resourceLocation(String name) {
        return ResourceLocation.parse(name);
    }

    /**
     * Returns a {@link ResourceLocation} with the specified namespace and path.
     * This method allows for explicit control over the namespace and path of a resource.
     *
     * @param namespace The namespace for the resource (for example, "minecraft" or "falloutmc").
     * @param path      The path of the resource within the namespace.
     * @return A {@link ResourceLocation} with the given namespace and path.
     */
    public static ResourceLocation resourceLocation(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

}
