package me.vrekt.arc.utilties;

import me.vrekt.arc.Arc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

public class LocationHelper {

    /**
     * Check if we are on ground by expanding the location and checking blocks
     * around us.
     *
     * @param location the location we traveled to.
     * @param vertical the players vertical speed.
     * @return whether or not were on the ground. true if we are.
     */
    public static boolean isOnGround(Location location, double vertical) {
        LocationBit bit = new LocationBit(0.3);

        // get two different block locations.
        Location subtractedGround = location.clone().subtract(0, 0.5, 0);
        Block oddBlock = location.getBlock().getRelative(BlockFace.DOWN);
        Block groundBlock = subtractedGround.getBlock();

        boolean hasClimbable = groundBlock.getType().equals(Material.LADDER);
        boolean isOnOddBlock = isOnStair(oddBlock.getLocation()) || isOnSlab(oddBlock.getLocation()) || (MaterialHelper.isFence(oddBlock.getType())
                || MaterialHelper.isFenceGate(oddBlock.getType()));

        if (groundBlock.getType().isSolid() || hasClimbable && vertical == 0.0 || isOnOddBlock) {
            // solid block found, return.
            return true;
        }
        // expand the location and determine if we are being supported by a block.

        for (int i = 1; i <= 4; i++) {
            Location newGround = location.clone().add(bit.getX(), -0.5, bit.getZ());
            Block block = newGround.getBlock();
            if (block.getType().isSolid() && !hasClimbable) {
                Arc.getPlugin().getLogger().info("T: " + block.getType().toString());
                return true;
            }

            bit.shift(i);
        }
        return false;
    }

    /**
     * Check if a block is under the player.
     *
     * @param location   the location
     * @param comparable the class to check against.
     * @return true if the block is under the player.
     */
    public static boolean hasBlock(Location location, Class comparable) {
        LocationBit bit = new LocationBit(0.3);

        // check if were already under that block.
        Location subtracted = location.clone().subtract(0, 0.3, 0);
        Block subtractedBlock = subtracted.getBlock();

        if (subtractedBlock.getType().getData().equals(comparable)) {
            return true;
        }

        for (int i = 1; i <= 4; i++) {
            Location newLocation = location.clone().add(bit.getX(), -0.3, bit.getZ());
            Block block = newLocation.getBlock();
            if (block.getType().getData().equals(comparable)) {
                return true;
            }
            bit.shift(i);
        }
        return false;
    }

    /**
     * Check if a block is under the player.
     *
     * @param location   the location
     * @param comparable the class to check against.
     * @param vertical   the y amount to subtract/add by.
     * @return true if the block is under the player.
     */
    public static boolean hasBlock(Location location, Class comparable, double vertical) {
        LocationBit bit = new LocationBit(0.5);

        // check if were already under that block.
        Location subtracted = location.clone().subtract(0, vertical, 0);
        Block subtractedBlock = subtracted.getBlock();

        if (subtractedBlock.getType().getData().equals(comparable)) {
            return true;
        }

        for (int i = 1; i <= 4; i++) {
            Location newLocation = location.clone().add(bit.getX(), -vertical, bit.getZ());
            Block block = newLocation.getBlock();
            if (block.getType().getData().equals(comparable)) {
                return true;
            }
            bit.shift(i);
        }
        return false;
    }

    /**
     * @param location the location
     * @return if we are under a block.
     */
    public static boolean isUnderBlock(Location location) {
        LocationBit bit = new LocationBit(0.5);

        // check if were already under that block.
        Location added = location.clone().add(0, 2, 0);
        Block addedBlock = added.getBlock();
        if (addedBlock.getType().isSolid()) {
            return true;
        }

        for (int i = 1; i <= 4; i++) {
            Location newLocation = location.clone().add(bit.getX(), 2, bit.getZ());
            Block block = newLocation.getBlock();
            if (block.getType().isSolid()) {
                return true;
            }
            bit.shift(i);
        }
        return false;
    }

    /**
     * @return if we are climbing on ladder/vine.
     */
    public static boolean isClimbing(Location location) {
        boolean alreadyHasLadder = location.getBlock().getType() == Material.LADDER;
        boolean alreadyHasVine = location.getBlock().getType() == Material.VINE;
        if (alreadyHasLadder || alreadyHasVine) {
            return true;
        }

        LocationBit bit = new LocationBit(0.1);
        for (int i = 1; i <= 4; i++) {
            Location newLocation = location.clone().add(bit.getX(), -0.06, bit.getZ());
            Block block = newLocation.getBlock();
            if (block.getType() == Material.LADDER || block.getType() == Material.VINE) {
                return true;
            }
            bit.shift(i);
        }
        return false;
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
        return Math.sqrt(dx * dx + dz * dz);
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
        return Math.sqrt(dy * dy);
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
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
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
        LocationBit bit = new LocationBit(0.5);

        // check if were already under that block.
        Location subtracted = location.clone().subtract(0, 0.1, 0);
        Block subtractedBlock = subtracted.getBlock();

        if (subtractedBlock.getType().getData().equals(Step.class)) {
            return true;
        }

        for (int i = 1; i <= 4; i++) {
            Location newLocation = location.clone().add(bit.getX(), -0.1, bit.getZ());
            Block block = newLocation.getBlock();
            if (block.getType().getData().equals(Step.class)) {
                return true;
            }
            bit.shift(i);
        }
        return false;
    }

    /**
     * @return if we are on a stair.
     */
    public static boolean isOnStair(Location location) {
        return hasBlock(location, Stairs.class);
    }

    /**
     * @return if we are on a stair but its lower than 0.3
     */
    public static boolean isOnStairJump(Location location) {
        return hasBlock(location, Stairs.class, 1);
    }

    /**
     * @return if we are in liquid.
     */
    public static boolean isInLiquid(Location location) {
        return location.getBlock().isLiquid() || location.getBlock().getRelative(BlockFace.DOWN).isLiquid();
    }

    /**
     * @return if we are on ice.
     */
    public static boolean isOnIce(Location location) {
        Block down = location.getBlock().getRelative(BlockFace.DOWN);
        boolean relativeDown = down.getType() == Material.ICE || down.getType() == Material.PACKED_ICE;

        Block low = location.getBlock().getRelative(0, -2, 0);
        boolean relativeLow = low.getType() == Material.ICE || low.getType() == Material.PACKED_ICE;
        return relativeDown || relativeLow;
    }

    /**
     * @return if we have walked onto a fence.
     */
    public static boolean walkedOnFence(Location location) {
        // check if were already under that block.
        Location subtracted = location.clone().subtract(0, 1, 0);
        Block groundBlock = subtracted.getBlock();
        if (MaterialHelper.isFence(groundBlock.getType()) || MaterialHelper.isFenceGate(groundBlock.getType())) {
            return true;
        }

        LocationBit bit = new LocationBit(0.5);
        for (int i = 1; i <= 4; i++) {
            Location newLocation = location.clone().add(bit.getX(), -1, bit.getZ());
            Block block = newLocation.getBlock();
            if (MaterialHelper.isFence(block.getType()) || MaterialHelper.isFenceGate(block.getType())) {
                return true;
            }
            bit.shift(i);
        }
        return false;
    }

    public static class LocationBit {

        private double modifier;
        private double xBit, zBit;

        public LocationBit(double modifier) {
            this.modifier = modifier;
            this.xBit = modifier;
            this.zBit = modifier;
        }

        /**
         * Shift the X and Y.
         *
         * @param bound the index in an array of 5.
         */
        public void shift(int bound) {
            xBit = bound >= 2 ? -modifier : modifier;
            zBit = bound == 1 ? -modifier : bound == 2 ? modifier : bound == 3 ? -modifier : zBit;
        }

        public double getX() {
            return xBit;
        }

        public double getZ() {
            return zBit;
        }
    }

}
