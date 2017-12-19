package me.vrekt.arc.listener.packet;

import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.packetwrapper.WrapperPlayClientFlying;
import com.comphenix.packetwrapper.WrapperPlayClientHeldItemSlot;
import com.comphenix.packetwrapper.WrapperPlayClientPosition;
import com.comphenix.packetwrapper.WrapperPlayClientPositionLook;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.vrekt.arc.Arc;
import me.vrekt.arc.check.CheckType;
import me.vrekt.arc.check.combat.KillAura;
import me.vrekt.arc.check.inventory.AutoHeal;
import me.vrekt.arc.check.moving.MorePackets;
import me.vrekt.arc.data.combat.FightData;
import me.vrekt.arc.data.inventory.InventoryData;
import me.vrekt.arc.data.moving.MovingData;
import me.vrekt.arc.listener.ACheckListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PacketListener implements ACheckListener {
    private final MorePackets MORE_PACKETS = (MorePackets) Arc.getCheckManager().getCheck(CheckType.MOREPACKETS);
    private final KillAura KILL_AURA = (KillAura) Arc.getCheckManager().getCheck(CheckType.KILLAURA);
    private final AutoHeal AUTO_HEAL = (AutoHeal) CHECK_MANAGER.getCheck(CheckType.AUTOHEAL);

    private boolean listening = false;

    /**
     * Stop listening.
     */
    public void stopListening() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(Arc.getPlugin());
    }

    public void startListening(Plugin plugin, ProtocolManager manager) {
        listening = true;

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.FLYING) {
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
                data.setFlyingPackets(data.getFlyingPackets() + 1);
                boolean cancel = updateAndCheck(player, data);
                if (cancel) {
                    event.setCancelled(true);
                }

            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION,
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
                data.setPositionPackets(data.getPositionPackets() + 1);
                boolean cancel = updateAndCheck(player, data);
                if (cancel) {
                    event.setCancelled(true);
                }
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                FightData data = FightData.getData(player);
                data.setTotalAttacks(data.getTotalAttacks() + 1);

            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                FightData data = FightData.getData(player);
                data.setLastArmSwing(System.currentTimeMillis());
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.HELD_ITEM_SLOT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                InventoryData data = InventoryData.getData(player);
                WrapperPlayClientHeldItemSlot itemSlotChange = new WrapperPlayClientHeldItemSlot(event.getPacket());
                int slot = itemSlotChange.getSlot();
                ItemStack item = player.getInventory().getItem(slot);
                if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
                    data.setLastItemSwitch(System.currentTimeMillis());
                }

            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                InventoryData data = InventoryData.getData(player);
                WrapperPlayClientBlockPlace blockPlace = new WrapperPlayClientBlockPlace(event.getPacket());
                ItemStack item = blockPlace.getHeldItem();

                if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
                    AUTO_HEAL.check(data, player);
                }
            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                FightData data = FightData.getData(player);
                WrapperPlayClientUseEntity useEntity = new WrapperPlayClientUseEntity(event.getPacket());
                if (useEntity.getType() == EnumWrappers.EntityUseAction.ATTACK) {
                    // we attacked, lets update our data and check.
                    data.setAttackPackets(data.getAttackPackets() + 1);
                    boolean failed = KILL_AURA.checkFrequency(data, player);
                    if (failed) {
                        event.setCancelled(true);
                    }
                }
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

        // if we cant check reset data and return.
        if (!canCheckMorePackets) {
            data.setLastPacketUpdate(System.currentTimeMillis());
            data.setPositionPackets(0);
            data.setFlyingPackets(0);

            data.setCancelMovingPackets(false);
            return false;
        }

        long timeElapsed = System.currentTimeMillis() - data.getLastPacketUpdate();
        if (timeElapsed >= 1000) {
            data.setCancelMovingPackets(false);
            // its been a second lets check.
            boolean check = MORE_PACKETS.check(player, data);

            data.setPositionPackets(0);
            data.setFlyingPackets(0);
            data.setLastPacketUpdate(System.currentTimeMillis());
            return check;
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
