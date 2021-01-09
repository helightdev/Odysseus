package dev.helight.odysseus.item;

import com.google.common.collect.ForwardingObject;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.helight.odysseus.Odysseus;
import lombok.Cleanup;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import net.minecraft.server.v1_16_R2.NBTTagString;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class Item extends ForwardingObject {

    private ItemStack itemStack;

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item(Material material) {
        this.itemStack = new ItemStack(material, 1);
    }

    public static Item builder(Material material) {
        return new Item(material);
    }

    public static Item builder(ItemStack itemStack) {
        return new Item(itemStack);
    }

    public Item amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public Item name(String name) {
        this.changeMeta(itemMeta -> {
            itemMeta.setDisplayName(name);
        });
        return this;
    }

    public Item lore(String... strings) {
        this.changeMeta(itemMeta -> {
            itemMeta.setLore(Arrays.asList(strings));
        });
        return this;
    }

    public Item lore(Collection<String> strings) {
        this.changeMeta(itemMeta -> {
            itemMeta.setLore(new ArrayList<>(strings));
        });
        return this;
    }

    public Item enchant(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public Item flag(ItemFlag itemFlag) {
        itemStack.addItemFlags(itemFlag);
        return this;
    }


    public Item baseDamage(double damage) {
        changeMeta(meta -> {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ATTACK_DAMAGE,
                    new AttributeModifier("generic.attack_damage", Math.max(damage - 1, 0), AttributeModifier.Operation.ADD_NUMBER)
            );
        });
        return this;
    }

    public Item armor(double damage) {
        changeMeta(meta -> {
            meta.addAttributeModifier(
                    Attribute.GENERIC_ARMOR,
                    new AttributeModifier("generic.armor", Math.max(damage - 1, 0), AttributeModifier.Operation.ADD_NUMBER)
            );
        });
        return this;
    }

    public Item canPlaceOn(Material material) {
        editNMS(compound -> {
            NBTTagList attributes = compound.getList("CanPlaceOn", 8);
            attributes.add(NBTTagString.a("minecraft:"+material.name().toLowerCase()));
            compound.set("CanPlaceOn", attributes);
        });
        return this;
    }

    public Item canDestroy(Material material) {
        editNMS(compound -> {
            NBTTagList attributes = compound.getList("CanDestroy", 8);
            attributes.add(NBTTagString.a("minecraft:"+material.name().toLowerCase()));
            compound.set("CanPlaceOn", attributes);
        });
        return this;
    }

    @SneakyThrows
    public Item storeJson(Object o) {
        Gson gson = new Gson();
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        @Cleanup OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        gson.toJson(o,writer);
        changeMeta(itemMeta -> {
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Odysseus.getPlugin(), o.getClass().getName()), PersistentDataType.BYTE_ARRAY, outputStream.toByteArray());
        });
        return this;
    }

    @SneakyThrows
    public Item storeJson(String key, Object o) {
        Gson gson = new Gson();
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        @Cleanup OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        gson.toJson(o,writer);
        changeMeta(itemMeta -> {
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(Odysseus.getPlugin(), key), PersistentDataType.BYTE_ARRAY, outputStream.toByteArray());
        });
        return this;
    }

    @SneakyThrows
    public <K> K loadJson(Class<K> clazz) {
        Gson gson = new Gson();
        byte[] array = itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Odysseus.getPlugin(), clazz.getName()), PersistentDataType.BYTE_ARRAY);
        @Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
        @Cleanup InputStreamReader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, clazz);
    }


    @SneakyThrows
    public <K> K loadJson(String key, Class<K> clazz) {
        Gson gson = new Gson();
        byte[] array = itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Odysseus.getPlugin(), key), PersistentDataType.BYTE_ARRAY);
        @Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
        @Cleanup InputStreamReader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, clazz);
    }

    @SneakyThrows
    public String load(String key) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Odysseus.getPlugin(), key), PersistentDataType.STRING);
    }
    
    public void editNMS(Consumer<NBTTagCompound> function) {
        net.minecraft.server.v1_16_R2.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = stack.getOrCreateTag();
        function.accept(compound);
        stack.setTag(compound);
        itemStack = CraftItemStack.asCraftMirror(stack);

    }

    public void changeMeta(Function<ItemMeta, ItemMeta> function) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta = function.apply(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public void changeMeta(Consumer<ItemMeta> function) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        function.accept(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public ItemStack delegate() {
        return itemStack;
    }

    public static boolean remove(Player player, ItemStack itemStack, int amount) {
        if (player.getInventory().contains(itemStack.asOne(),amount)) {
            player.getInventory().remove(itemStack.asQuantity(amount));
            return true;
        } else {
            return false;
        }
    }

    public static void add(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack).forEach((integer, itemStack1) -> player.getLocation().getWorld().dropItem(player.getLocation().add(0,0.5,0), itemStack));
    }

    public static ItemStack getCustomTextureHead(String value) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }

    public static Item fromHead(String value) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return Item.builder(head);
    }

}
