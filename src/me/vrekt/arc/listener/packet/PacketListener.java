package me.vrekt.arc.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.data.moving.MovingData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import packetwrapper.WrapperPlayClientFlying;
import packetwrapper.WrapperPlayClientPosition;
import packetwrapper.WrapperPlayClientPositionLook;

public class PacketListener {
    private final MorePackets MORE_PACKETS = (MorePackets) Arc.getCheckManager().getCheck(CheckType.MOREPACKETS);


    public void startListening(Plugin plugin, ProtocolManager manager) {
        /**
         * Listen for onGround packet.
         */
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

        /**
         * Listen for the position packet.
         */
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                MovingData data = MovingData.getData(player);
                if (data.cancelMovingPackets()) {
                    event.setCancelled(true);
                }

                // Update ground info.
                WrapperPlayClientPosition position = new WrapperPlayClientPosition(event.getPacket());
                data.setClientOnGround(position.getOnGround());

                // Update packet info.
                data.setMovingPackets(data.getMovingPackets() + 1);
                if (updateAndCheck(player, data)) {
                    event.setCancelled(true);
                }
            }
        });

        /**
         * Listen for the position packet.
         */
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                MovingData data = MovingData.getData(player);
                if (data.cancelMovingPackets()) {
                    event.setCancelled(true);
                }

                // Update ground info.
                WrapperPlayClientPositionLook position = new WrapperPlayClientPositionLook(event.getPacket());
                data.setClientOnGround(position.getOnGround());

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

}
