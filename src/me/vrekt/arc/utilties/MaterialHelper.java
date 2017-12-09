package me.vrekt.arc.utilties;

import org.bukkit.Material;

public class MaterialHelper {

    /**
     * @param type the material
     * @return true if the material is a fence.
     */
    public static boolean isFence(Material type) {
        switch (type) {
            case FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case IRON_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
                return true;
        }
        return false;
    }

    /**
     * @param type the material
     * @return true if the material is a fence-gate.
     */
    public static boolean isFenceGate(Material type) {
        switch (type) {
            case FENCE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                return true;
        }
        return false;
    }

}
