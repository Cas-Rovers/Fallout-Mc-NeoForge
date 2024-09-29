package dev.cassis2310.falloutmc.util.recipes;

import dev.cassis2310.falloutmc.util.client.ClientHelpers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class RecipeHelpers {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(RecipeHelpers.class);
    private static RecipeManager CACHED_RECIPE_MANAGER;

    /**
     * Retrieves the RecipeManager unsafely, which might return null.
     *
     * @return The RecipeManager instance if available.
     * @throws IllegalStateException if no RecipeManager is found.
     */
    public static RecipeManager getUnsafeRecipeManager() {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getRecipeManager();
        }

        try {
            final RecipeManager client = ClientHelpers.tryGetSafeRecipeManager();
            if (client != null) {
                return client;
            }
        } catch (Throwable t) {
            LOGGER.info("^ This is fine - No client or server recipe manager present upon initial resource reload on physical server");
        }

        if (CACHED_RECIPE_MANAGER != null) {
            return CACHED_RECIPE_MANAGER;
        }

        throw new IllegalStateException("No recipe manager was present - tried server, client, and captured value. This will cause problems!");
    }

    /**
     * Caches the RecipeManager instance for later use.
     * This method is useful for optimizing repeated access to the recipe manager.
     *
     * @param manager The RecipeManager to cache.
     */
    public static void setCachedRecipeManager(RecipeManager manager) {
        CACHED_RECIPE_MANAGER = manager;
    }
}
