package dev.helight.odysseus.item;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class SerializedItem {

    private String name;

    private List<String> lore = new ArrayList<>();

    private Integer model = 0;

    private String material;

    private Integer amount = 1;

    private List<SerializedEnchantment> enchantments = new ArrayList<>();

    private List<SerializedAttribute> attributes = new ArrayList<>();

    private List<String> canPlaceOn = new ArrayList<>();

    private List<String> canDestroy = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuppressWarnings("unused")
    public static class SerializedEnchantment {
        private String name;
        private Integer level = 1;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuppressWarnings("unused")
    public static class SerializedAttribute {
        private String name;

        private Double value;

        private String operation = "ADD";
    }

    public ItemStack build() {
        Item item = Item.builder(Material.matchMaterial(material))
                .amount(amount)
                .name(name)
                .lore(lore);

        item.changeMeta(meta -> {
            meta.setCustomModelData(0);
        });
        
        for (String s : canDestroy) item.canDestroy(Material.matchMaterial(s));
        for (String s : canDestroy) item.canPlaceOn(Material.matchMaterial(s));

        for (SerializedEnchantment enchantment : enchantments) {
            Enchantment bukkit = Arrays.stream(Enchantment.values())
                    .filter(query -> query.getKey().getKey().endsWith(enchantment.name.toLowerCase()))
                    .findFirst().orElse(Enchantment.LUCK);
            item.enchant(bukkit, enchantment.level);
        }
        
        for (SerializedAttribute attribute : attributes) {
            item.changeMeta(meta -> {
                meta.addAttributeModifier(
                        Attribute.valueOf(attribute.name.replaceAll("\\.","_").toUpperCase()),
                        new AttributeModifier(attribute.name, attribute.value, attribute.operation.toLowerCase().equals("add") ? 
                                AttributeModifier.Operation.ADD_NUMBER : AttributeModifier.Operation.ADD_SCALAR)
                );
            });
        }
        
        return item.delegate();
    }

    public static ItemStack deserialize(String content) {
        Gson gson = new Gson();
        SerializedItem item = gson.fromJson(content, SerializedItem.class);
        return item.build();
    }
}
