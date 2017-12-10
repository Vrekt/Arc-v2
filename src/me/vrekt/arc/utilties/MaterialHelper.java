package me.vrekt.arc.utilties;

import org.bukkit.Material;


public class MaterialHelper {

    public static final Material[] FENCES = new Material[]{Material.FENCE, Material.ACACIA_FENCE, Material.BIRCH_FENCE, Material
            .DARK_OAK_FENCE, Material.IRON_FENCE, Material.JUNGLE_FENCE, Material.NETHER_FENCE, Material.SPRUCE_FENCE};
    public static final Material[] FENCE_GATES = new Material[]{Material.FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE,
            Material
                    .DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE};

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
            case FENCE_GATE:
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
