package dev.cassis2310.falloutmc.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

public class ClientHelpers {
    /**
     * Safely retrieves the {@link RecipeManager} from the client-side Minecraft instance.
     *
     * @return The {@link RecipeManager} if available, otherwise null.
     */
    @Nullable
    @SuppressWarnings("ConstantValue")
    public static RecipeManager tryGetSafeRecipeManager() {
        final @Nullable Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.level != null ? mc.level.getRecipeManager() : null;
    }
}
