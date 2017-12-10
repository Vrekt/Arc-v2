package me.vrekt.arc.utilties;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

public class LocationHelper {
    private static final double MODIFIER = 0.3;

    /**
     * Check if we are on ground by expanding the location and checking blocks
     * around us.
     *
     * @param location the location we traveled to.
     * @param vertical the players vertical speed.
     * @return whether or not were on the ground. true if we are.
     */
    public static boolean isOnGround(Location location, double vertical) {
        boolean onGround = false;

        // get two different block locations.
        Block oddBlock = location.getBlock().getRelative(BlockFace.DOWN);

        Location subtractedGround = location.clone().subtract(0, MODIFIER, 0);
        Block groundBlock = subtractedGround.getBlock();

        boolean hasClimbable = groundBlock.getType().equals(Material.LADDER);
        boolean isOnOddBlock = isOnStair(oddBlock.getLocation()) || isOnSlab(oddBlock.getLocation()) || (MaterialHelper.isFence(oddBlock.getType())
                || MaterialHelper.isFenceGate(oddBlock.getType()));

        if (groundBlock.getType().isSolid() || hasClimbable && vertical == 0.0 || isOnOddBlock) {
            // solid block found, return.
            return true;
        }
        // expand the location and determine if we are being supported by a block.
        double bit = MODIFIER;
        double xbit = bit;
        double zbit = bit;

        for (int expansion = 0; expansion < 5; expansion++) {
            Location newGround = location.clone().add(xbit, -MODIFIER, zbit);
            Block block = newGround.getBlock();
            if (block.getType().isSolid()) {
                onGround = true;
                break;
            }
            if (expansion == 4) {
                break;
            }


            // never pos and neg
            // this follows the setup 0:(pos, pos) 1:(pos, neg) 2:(neg, pos) 3:(neg, neg)
            xbit = expansion >= 2 ? -bit : bit;
            zbit = expansion == 1 ? -bit : expansion == 2 ? bit : expansion == 3 ? -bit : zbit;
        }

        return onGround;
    }

    /**
     * Check if a block is under the player.
     *
     * @param location   the location
     * @param comparable the class to check against.
     * @return true if the block is under the player.
     */
    public static boolean hasBlock(Location location, Class comparable) {
        boolean hasBlock = false;

        // check if were already under that block.
        Location subtractedGround = location.clone().subtract(0, MODIFIER, 0);
        Block groundBlock = subtractedGround.getBlock();
        if (groundBlock.getType().getData().equals(comparable)) {
            return true;
        }

        double bit = MODIFIER;
        double xbit = bit;
        double zbit = bit;

        for (int expansion = 0; expansion < 5; expansion++) {
            Location newGround = location.clone().add(xbit, -MODIFIER, zbit);
            Block block = newGround.getBlock();
            if (block.getType().getData().equals(comparable)) {
                hasBlock = true;
                break;
            }
            xbit = expansion >= 2 ? -bit : bit;
            zbit = expansion == 1 ? -bit : expansion == 2 ? bit : expansion == 3 ? -bit : zbit;
        }

        return hasBlock;
    }

    /**
     * @param location the location
     * @return if we are under a block.
     */
    public static boolean isUnderBlock(Location location) {
        boolean hasBlock = false;

        // determine if we are already under a block without expanding.
        Location subtracted = location.clone().add(0, 2, 0);
        Block block = subtracted.getBlock();

        if (block.getType().isSolid()) {
            // solid block found, return.
            return true;
        }

        // expand the location and determine if we are being supported by a block.
        double bit = MODIFIER;
        double xbit = bit;
        double zbit = bit;

        for (int expansion = 0; expansion < 5; expansion++) {
            Location newLocation = location.clone().add(xbit, 2, zbit);
            Block newBlock = newLocation.getBlock();
            if (newBlock.getType().isSolid()) {
                hasBlock = true;
                break;
            }
            if (expansion == 4) {
                break;
            }
            // never pos and neg
            // this follows the setup 0:(pos, pos) 1:(pos, neg) 2:(neg, pos) 3:(neg, neg)
            xbit = expansion >= 2 ? -bit : bit;
            zbit = expansion == 1 ? -bit : expansion == 2 ? bit : expansion == 3 ? -bit : zbit;
        }

        return hasBlock;
    }


    /**
     * Distance horizontal from and to.
     *
     * @param from the start location
     * @param to   the final location
     * @return the horizontal distance.
     */
    public static double distanceHorizontal(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dz, 2.0));
    }

    /**
     * Distance vertical from and to.
     *
     * @param from the start location
     * @param to   the final location
     * @return the vertical distance.
     */
    public static double distanceVertical(Location from, Location to) {
        double dy = to.getY() - from.getY();
        return Math.sqrt(Math.pow(dy, 2.0));
    }

    /**
     * Basic distance from and to.
     *
     * @param from the start location
     * @param to   the final location
     * @return the distance.
     */
    public static double distance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0) + Math.pow(dz, 2.0));
    }

    /**
     * @return if we are on a slimeblock.
     */
    public static boolean isOnSlimeblock(Location location) {

        return location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK
                || location.getBlock().getRelative(0, -2, 0).getType() == Material.SLIME_BLOCK;

    }

    /**
     * @return if we are on a slab.
     */
    public static boolean isOnSlab(Location location) {
        return hasBlock(location, Step.class);
    }

    /**
     * @return if we are on a stair.
     */
    public static boolean isOnStair(Location location) {
        return hasBlock(location, Stairs.class);
    }

    /**
     * @return if we are in liquid.
     */
    public static boolean isInLiquid(Location location) {
        return location.getBlock().isLiquid();
    }

}
