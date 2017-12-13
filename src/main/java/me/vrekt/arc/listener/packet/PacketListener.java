package me.vrekt.arc.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.data.combat.FightData;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.ACheckListener;
import me.vrekt.arc.wrappers.WrapperPlayClientFlying;
import me.vrekt.arc.wrappers.WrapperPlayClientPosition;
import me.vrekt.arc.wrappers.WrapperPlayClientPositionLook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketListener implements ACheckListener {
    private final MorePackets MORE_PACKETS = (MorePackets) Arc.getCheckManager().getCheck(CheckType.MOREPACKETS);
    private boolean listening = false;

    public void startListening(Plugin plugin, ProtocolManager manager) {
        listening = true;
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
                data.setFlyingClientOnGround(flying.getOnGround());

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
                    data.setPositionClientOnGround(position.getOnGround());
                } else {
                    WrapperPlayClientPositionLook position = new WrapperPlayClientPositionLook(event.getPacket());
                    data.setPositionClientOnGround(position.getOnGround());
                }

                // Update packet info.
                data.setMovingPackets(data.getMovingPackets() + 1);
                if (updateAndCheck(player, data)) {
                    event.setCancelled(true);
                }
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                FightData data = FightData.getData(player);
                data.setLastArmSwing(System.currentTimeMillis());
            }
        });


    }

    /**
     * Checks MorePackets and updates/resets data.
     *
     * @param player the player
     * @param data   the MovingData
     * @return true if we should cancel.
     */
    private boolean updateAndCheck(Player player, MovingData data) {
        if (data.getLastPacketUpdate() == 0) {
            data.setLastPacketUpdate(System.currentTimeMillis());
            return false;
        }

        boolean canCheckMorePackets = Arc.getCheckManager().canCheckPlayer(player, CheckType.MOREPACKETS);

        // fix for MorePackets flagging when logging in, even when exempted.
        if (!canCheckMorePackets) {
            data.setMovingPackets(0);
            data.setLastPacketUpdate(System.currentTimeMillis());
            return false;
        }

        long time = System.currentTimeMillis() - data.getLastPacketUpdate();
        // check if its been more than a second.
        if (time >= 1000) {
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

    /**
     * @return if we are listening for packets.
     */
    public boolean isListening() {
        return listening;
    }
}
