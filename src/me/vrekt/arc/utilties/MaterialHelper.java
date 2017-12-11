package me.vrekt.arc.utilties;

import me.vrekt.arc.Arc;
import org.bukkit.Material;
import org.bukkit.material.Gate;


public class MaterialHelper {

    /**
     * @param type the material
     * @return true if the material is a fence.
     */
    public static boolean isFence(Material type) {
        // compatibility fix
        if (Arc.COMPATIBILITY) {
            switch (type) {
                case FENCE:
                case IRON_FENCE:
                case NETHER_FENCE:
                    return true;
            }
        } else {
            switch (type) {
                case FENCE:
                case BIRCH_FENCE:
                case DARK_OAK_FENCE:
                case IRON_FENCE:
                case JUNGLE_FENCE:
                case NETHER_FENCE:
                case SPRUCE_FENCE:
                    return true;
            }
        }
        return false;
    }

    /**
     * @param type the material
     * @return true if the material is a fence-gate.
     */
    public static boolean isFenceGate(Material type) {
        return type.getData() == Gate.class;
    }

}
