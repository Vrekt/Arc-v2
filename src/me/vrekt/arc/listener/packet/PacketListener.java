package me.vrekt.arc.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckResult;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.Flight;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.check.moving.NoFall;
import me.vrekt.arc.check.moving.Speed;
import me.vrekt.arc.check.moving.compatibility.Flight17;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.ACheckListener;
import me.vrekt.arc.utilties.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import packetwrapper.WrapperPlayClientFlying;
import packetwrapper.WrapperPlayClientPosition;
import packetwrapper.WrapperPlayClientPositionLook;

public class PacketListener implements ACheckListener {
    private final MorePackets MORE_PACKETS = (MorePackets) Arc.getCheckManager().getCheck(CheckType.MOREPACKETS);
    private final Flight FLIGHT = (Flight) CHECK_MANAGER.getCheck(CheckType.FLIGHT);
    private final Flight17 FLIGHT_17 = (Flight17) CHECK_MANAGER.getCheck(CheckType.FLIGHT_17);

    private final NoFall NO_FALL = (NoFall) CHECK_MANAGER.getCheck(CheckType.NOFALL);
    private final Speed SPEED = (Speed) CHECK_MANAGER.getCheck(CheckType.SPEED);


    public void startListening(Plugin plugin, ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.FLYING) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                MovingData data = MovingData.getData(player);
                if (data.cancelMovingPackets()) {
                    event.setCancelled(true);
                }

                // Update ground info.
                WrapperPlayClientFlying flying = new WrapperPlayClientFlying(event.getPacket());
                data.setClientOnGround(flying.getOnGround());

                // Update packet info.
                data.setMovingPackets(data.getMovingPackets() + 1);
                if (updateAndCheck(player, data)) {
                    event.setCancelled(true);
                }

            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                MovingData data = MovingData.getData(player);
                if (data.cancelMovingPackets()) {
                    event.setCancelled(true);
                }

                // Update ground info.
                if (event.getPacket().getType().equals(PacketType.Play.Client.POSITION)) {
                    WrapperPlayClientPosition position = new WrapperPlayClientPosition(event.getPacket());
                    data.setClientOnGround(position.getOnGround());
                } else {
                    WrapperPlayClientPositionLook position = new WrapperPlayClientPositionLook(event.getPacket());
                    data.setClientOnGround(position.getOnGround());
                }

                // update moving data.
                Location location = player.getLocation();
                if (check(player, data)) {
                    event.setCancelled(true);
                }

                // Update packet info.
                data.setMovingPackets(data.getMovingPackets() + 1);
                if (updateAndCheck(player, data)) {
                    event.setCancelled(true);
                }
            }
        });


    }

    /**
     * Checks MorePackets and updates/resets data.
     *
     * @param player
     * @param data
     * @return true if we should cancel.
     */
    private boolean updateAndCheck(Player player, MovingData data) {
        if (data.getLastPacketUpdate() == 0) {
            data.setLastPacketUpdate(System.currentTimeMillis());
            return false;
        }

        boolean canCheckMorePackets = Arc.getCheckManager().canCheckPlayer(player, CheckType.MOREPACKETS);
        long time = System.currentTimeMillis() - data.getLastPacketUpdate();
        // check if its been more than a second.
        if (time >= 1000 && canCheckMorePackets) {
            boolean check = MORE_PACKETS.check(player, data);
            data.setMovingPackets(0);
            data.setLastPacketUpdate(System.currentTimeMillis());
            if (check) {
                return true;
            } else {
                data.setCancelMovingPackets(false);
                return false;
            }
        }

        return false;
    }

    private boolean check(Player player, MovingData data) {

        boolean compatibility = Arc.COMPATIBILITY;

        CheckResult result = new CheckResult();

        Location from = data.getCurrentLocation();
        Location to = player.getLocation();

        data.setPreviousLocation(from);
        data.setCurrentLocation(to);

        // check if we have swapped worlds, if so stop here.
        if (from.getWorld() != to.getWorld()) {
            return false;
        }

        // check if we have moved.
        boolean hasMoved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();

        // check if we have moved but only from block to another block.
        boolean hasMovedByBlock = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();

        boolean canCheckFlight = CHECK_MANAGER.canCheckPlayer(player, CheckType.FLIGHT);

        if (hasMoved) {
            result.reset();
            // update our last move time.
            data.setLastMovingUpdate(System.currentTimeMillis());

            // update our distances.
            double vertical = Math.abs(LocationHelper.distanceVertical(from, to));
            data.setLastVerticalSpeed(data.getVerticalSpeed());
            data.setVerticalSpeed(vertical);

            // recheck if we are on ground and set it.
            boolean prevGround = data.isOnGround();
            boolean onGround = LocationHelper.isOnGround(to, vertical);
            data.setOnGround(onGround);
            if (onGround) {
                data.setGroundLocation(to);
            }

            // update wasOnGround.
            boolean accAirTime = data.getAirTicks() < 20 && prevGround;
            data.setWasOnGround(accAirTime);


            // update ascending/descending.
            boolean ascending = to.getY() > from.getY() && vertical > 0.0;
            boolean descending = from.getY() > to.getY() && vertical > 0.0;
            data.setAscending(ascending);
            data.setDescending(descending);

            // check if we are climbing or not.
            boolean isClimbing = (from.getBlock().getType() == Material.LADDER || from.getBlock().getType() == Material.VINE
                    || to.getBlock().getType() == Material.LADDER || to.getBlock().getType() == Material.VINE);
            data.setClimbing(isClimbing);

            double velocity = player.getVelocity().length();
            data.getVelocityData().setLastVelocity(data.getVelocityData().getCurrentVelocity());
            data.getVelocityData().setCurrentVelocity(velocity);

            if (canCheckFlight) {
                result.set(FLIGHT.check(player, data));
            }

        }

        if (hasMovedByBlock) {
            result.reset();
            if (canCheckFlight) {
                if (compatibility) {
                    result.set(FLIGHT_17.runBlockChecks(player, data));
                } else {
                    result.set(FLIGHT.runBlockChecks(player, data));
                }
            }

            boolean canCheckNoFall = CHECK_MANAGER.canCheckPlayer(player, CheckType.NOFALL);
            if (canCheckNoFall) {
                NO_FALL.check(player, data);
            }

            //SPEED.check(player, data);

        }
        return result.failed();
    }

}
