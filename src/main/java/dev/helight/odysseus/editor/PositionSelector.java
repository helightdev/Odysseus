package dev.helight.odysseus.editor;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.Raycast;
import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.events.BetterListener;
import dev.helight.odysseus.item.Item;
import dev.helight.odysseus.region.MathUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PositionSelector {

    public static final ItemStack negX = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==")
            .name("§eDecrement X")
            .lore("§7Decrements the X-Position by 0.05")
            .delegate();
    public static final ItemStack negY = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDg4ZjZlZTI2ZmI3NGM3YjJlMmJhM2UzYzRhNTg3MjE2YTFmZDZjYmNjNmRmZjljZDM1MGNiMjY5YWY5M2I3In19fQ==")
            .name("§eDecrement Y")
            .lore("§7Decrements the Y-Position by 0.05")
            .delegate();
    public static final ItemStack negZ = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzgzY2UwODcyNTQ4YTlkMWVlYzcyZTViMTk1YWE5OWMzYjAyZjY2NTg5MmI5ODcyNzIyYWNiY2UyMWU5MyJ9fX0=")
            .name("§eDecrement Z")
            .lore("§7Decrements the Z-Position by 0.05")
            .delegate();

    public static final ItemStack cursor = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMyYTU4MzhmZDljZDRjOTc3ZjE1MDcxZDY5OTdmZjVjN2Y5NTYwNzRhMmRhNTcxYTE5Y2NlZmIwM2M1NyJ9fX0=")
            .name("§eFollow Cursor")
            .lore("§7Makes the point follow your cursor")
            .delegate();
    public static final ItemStack check = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmIwYzE2NDdkYWU1ZDVmNmJjNWRjYTU0OWYxNjUyNTU2YzdmMWJjMDhhZGVlMzdjY2ZjNDA5MGJjMjBlNjQ3ZSJ9fX0=")
            .name("§eDone")
            .lore("§7Finishes the edition session")
            .delegate();

    public static final ItemStack posX = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FiYTJkNWE1Y2E4NDJhMmRhN2QyNWEyNTllNzdiNmNiYTVhZTY1NWRkZGMyNGVhNDk1NjQxZDI5ZWZjIn19fQ==")
            .name("§eIncrement X")
            .lore("§7Increments the X-Position by 0.05")
            .delegate();
    public static final ItemStack posY = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUxNDQ4ZjU5YjA3ZTZlOWRlOGIxODZhZjU5YjhmYzc2MTJkNjJiNDM4MmFlOTc2YmRmYjE0ZGFmOTQ5ODk4In19fQ==")
            .name("§eIncrement Y")
            .lore("§7Increments the Y-Position by 0.05")
            .delegate();
    public static final ItemStack posZ = Item.fromHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA1YTVlYzA3NGNlMzI0OThkNDg0YTkxOWMxZWYwYTEzMTYzMTUzYTIzNTU0YzZmMmEzMWQxMGUxYjJkNyJ9fX0=")
            .name("§eIncrement Z")
            .lore("§7Increments the Z-Position by 0.05")
            .delegate();

    public static Map<UUID, PositionEditingSession> sessionMap = new ConcurrentHashMap<>();

    public static CompletableFuture<Location> start(Player player) {
        PositionEditingSession session = new PositionEditingSession(player, Raycast.getLookingAt(player, 20, 255, false));
        sessionMap.put(player.getUniqueId(), session);
        return session.future;
    }

    public static class PositionEditingSession {

        public Player player;
        public Location current;
        public boolean followCursor = true;
        public int followCursorTaskId = -1;

        public List<ItemStack> itemBuffer = new ArrayList<>();

        private SinglePointSelectorEffect effect;

        @Getter
        private CompletableFuture<Location> future = new CompletableFuture<>();

        public PositionEditingSession(Player player, Location current) {
            this.player = player;
            this.current = current;
            effect = new SinglePointSelectorEffect(Odysseus.getInstance().getEffectManager());
            effect.pointLocation = current;
            effect.setLocation(effect.pointLocation);
            effect.setTargetPlayer(player);
            effect.start();

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                itemBuffer.add(player.getInventory().getItem(i));
            }
            player.getInventory().clear();

            player.getInventory().setItem(0, negX);
            player.getInventory().setItem(1, negY);
            player.getInventory().setItem(2, negZ);
            player.getInventory().setItem(3, cursor.clone());
            player.getInventory().setItem(4, null);
            player.getInventory().setItem(5, check.clone());
            player.getInventory().setItem(6, posX);
            player.getInventory().setItem(7, posY);
            player.getInventory().setItem(8, posZ);

            player.getInventory().setHeldItemSlot(4);

            update();
        }

        public void update() {
            effect.pointLocation = current;
            current.setPitch(0);
            current.setYaw(0);
            current.setX(Math.round(current.getX() * 100D) / 100D);
            current.setY(Math.round(current.getY() * 100D) / 100D);
            current.setZ(Math.round(current.getZ() * 100D) / 100D);
            if (followCursor && followCursorTaskId == -1) {
                followCursorTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Odysseus.getPlugin(), () -> {
                    current = Raycast.getLookingAt(player, 20, 255, false);
                    update();
                }, 1, 1);
                player.sendActionBar("§aFollowing Cursor");
                Item.builder(player.getInventory().getItem(3)).changeMeta(meta -> {
                    meta.addEnchant(Enchantment.LUCK, 1, true);
                });
            } else if (!followCursor && followCursorTaskId != -1) {
                Bukkit.getScheduler().cancelTask(followCursorTaskId);
                followCursorTaskId = -1;
                player.sendActionBar("§cStopped following Cursor");
                Item.builder(player.getInventory().getItem(3)).changeMeta(meta -> {
                    meta.removeEnchant(Enchantment.LUCK);
                });
            }
            TextComponent textComponent = new TextComponent();
            textComponent.setText(String.format("§eLocation§7: §c%s §a%s §b%s §8[CLIPBOARD]",
                    MathUtils.toDoubleString(current.getX(), 2),
                    MathUtils.toDoubleString(current.getY(), 2),
                    MathUtils.toDoubleString(current.getZ(), 2)
            ));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.format("%s %s %s", current.getX(), current.getY(), current.getZ())));
            Chat.clearSend(player, textComponent);
            player.sendMessage("");
            BaseComponent[] toolbar = new ComponentBuilder()
                    .append(new ComponentBuilder()
                            .append("§8[§7§lCENTER§r§8]")
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/editor center"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/editor center")))
                            .create())
                    .append(" ")
                    .append(new ComponentBuilder()
                            .append("§8[§7§lALIGN§r§8]")
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/editor align"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/editor align")))
                            .create())
                    .create();
            player.sendMessage(toolbar);
        }

        public void stop() {
            effect.cancel();
            if (followCursorTaskId != -1) {
                Bukkit.getScheduler().cancelTask(followCursorTaskId);
            }
            for (int i = 0; i < itemBuffer.size(); i++) {
                player.getInventory().setItem(i, itemBuffer.get(i));
            }
            sessionMap.remove(player.getUniqueId());
            future.complete(current);
        }

    }

    public static class PositionEditingListener extends BetterListener {

        @EventHandler
        public void dropItem(PlayerDropItemEvent event) {
            if (sessionMap.containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void switchSlot(PlayerItemHeldEvent event) {
            if (sessionMap.containsKey(event.getPlayer().getUniqueId())) {
                PositionEditingSession session = sessionMap.get(event.getPlayer().getUniqueId());
                event.setCancelled(true);
                switch (event.getNewSlot()) {
                    case 0:
                        session.current.subtract(0.05,0,0);
                        session.update();
                        break;
                    case 1:
                        session.current.subtract(0,0.05,0);
                        session.update();
                        break;
                    case 2:
                        session.current.subtract(0,0,0.05);
                        session.update();
                        break;

                    case 3:
                        session.followCursor = !session.followCursor;
                        session.update();
                        break;

                    case 5:
                        session.stop();
                        System.out.println(session.current);
                        break;

                    case 6:
                        session.current.add(0.05,0,0);
                        session.update();
                        break;
                    case 7:
                        session.current.add(0,0.05,0);
                        session.update();
                        break;
                    case 8:
                        session.current.add(0,0,0.05);
                        session.update();
                        break;
                }
            }
        }

    }

    public static class SinglePointSelectorEffect extends Effect {

        public Location pointLocation;

        public SinglePointSelectorEffect(EffectManager effectManager) {
            super(effectManager);
            setLocation(pointLocation);
            period = 1;
            type = EffectType.REPEATING;
            iterations = Integer.MAX_VALUE;
        }


        @Override
        public void onRun() {
            display(Particle.SOUL_FIRE_FLAME, pointLocation.clone().add(0,0.1,0), 0, 1);
        }

    }

}
