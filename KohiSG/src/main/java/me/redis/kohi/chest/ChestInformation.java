package me.redis.kohi.chest;

import me.redis.kohi.SurvivalGames;
import me.redis.kohi.chest.ChestLoot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

public class ChestInformation {
    private final SurvivalGames plugin;
    private final String fileName;
    private List<ChestLoot> itemList;

    public ChestInformation(SurvivalGames plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public SurvivalGames getPlugin() {
        return this.plugin;
    }

    public void load() {
        this.itemList = new ArrayList();
        File file = new File(this.plugin.getDataFolder(), this.fileName);
        if (!file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                Throwable localThrowable2 = null;
                try {
                    IOUtils.copy(this.plugin.getResource(this.fileName), fos);
                } catch (Throwable localThrowable1) {
                    localThrowable2 = localThrowable1;
                    throw localThrowable1;
                } finally {
                    if (fos != null) {
                        if (localThrowable2 != null) {
                            try {
                                fos.close();
                            } catch (Throwable x2) {
                                localThrowable2.addSuppressed(x2);
                            }
                        } else {
                            fos.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), this.fileName));
        ConfigurationSection config = configuration.getConfigurationSection("chests");
        List<Map<?, ?>> listmap = config.getMapList("item");
        for (int i = 0; i < listmap.size(); i++) {
            Map<?, ?> map = (Map) listmap.get(i);
            if (!map.containsKey("material")) {
                throw new RuntimeException("chests.yml is missing requried 'material' for index " + i + "the map that is invalid is " + map.toString());
            }
            if (!map.containsKey("chance")) {
                throw new RuntimeException("chests.yml is missing requried 'chance' for index " + i + "the map that is invalid is " + map.toString());
            }
            if (!map.containsKey("min")) {
                getPlugin().getLogger().severe("missing min field for chests.yml . This is really bad configuration. This configuration is located at index " + i + " with map" + map.toString());
            }
            if (!map.containsKey("max")) {
                getPlugin().getLogger().severe("missing max field for chests.yml . This is really bad configuration. This configuration is located at index " + i + " with map" + map.toString());
            }
            if (!(map.get("material") instanceof String)) {
                throw new RuntimeException("chests.yml's material field can only map Strings. excepted string but found " + map.get("material").getClass() + "for index" + i + "with map" + map.toString());
            }
            Material material = null;
            try {
                material = Material.valueOf(((String) map.get("material")).toUpperCase());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new RuntimeException("chests.yml's material field contained string that is not Material. excepted material but found " + map.get("material") + "for index" + i + "with map" + map.toString());
            }
            double chance = -1.0D;
            try {
                chance = Double.valueOf(map.get("chance").toString()).doubleValue();
            } catch (NumberFormatException e) {
                throw new RuntimeException("chests.yml's chance could not be converted to number for index " + i + "with map " + map.toString());
            }
            int min = 1;
            int max = 1;
            try {
                min = Integer.valueOf(map.get("min").toString()).intValue();
            } catch (NumberFormatException e) {
                throw new RuntimeException("chests.yml's min could not be converted to number for index " + i + "with map " + map.toString());
            }
            try {
                max = Integer.valueOf(map.get("max").toString()).intValue();
            } catch (NumberFormatException e) {
                throw new RuntimeException("chests.yml's max could not be converted to number for index " + i + "with map " + map.toString());
            }
            if (min > max) {
                throw new RuntimeException("chests.yml's min was higher than max for index " + i + "with map " + map.toString());
            }
            if (min <= 0) {
                throw new RuntimeException("chests.yml's min was higher less or equal to zero for index " + i + "with map " + map.toString());
            }
            if (max <= 0) {
                throw new RuntimeException("chests.yml's max was higher less or equal to zero for index " + i + "with map " + map.toString());
            }
            short damage = 0;
            if (map.containsKey("damage")) {
                try {
                    damage = Short.valueOf(map.get("damage").toString()).shortValue();
                } catch (NumberFormatException e) {
                    throw new RuntimeException("chests.yml's damage could not be converted to number for index " + i + "with map " + map.toString());
                }
            }
            Map<Enchantment, Integer> enchant = null;
            if (map.containsKey("enchant")) {
                enchant = new HashMap();
                if (!(map.get("enchant") instanceof Map)) {
                    throw new RuntimeException("chests.yml's enchant was not a map. was " + map.get("enchant").getClass() + " for index " + i + " with map " + map.toString());
                }
                Map<String, ?> enchantmap = (Map) map.get("enchant");
                for (Map.Entry<String, ?> entry : enchantmap.entrySet()) {
                    int level = -1;
                    try {
                        level = Integer.valueOf(entry.getValue().toString()).intValue();
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("chests.yml's enchantment level could not be converted to number for index " + i + "with map " + map.toString());
                    }
                    enchant.put(Enchantment.getByName(((String) entry.getKey()).toUpperCase()), Integer.valueOf(level));
                }
            }
            String name = null;
            if (map.containsKey("displayname")) {
                Object obj = map.get("displayname");
                if (!(obj instanceof String)) {
                    throw new RuntimeException("chests.yml's name was not a string for index " + i + " with map " + map.toString());
                }
                name = (String) obj;
            }
            List<String> lore = null;
            if (map.containsKey("lore")) {
                Object obj = map.get("lore");
                if (!(obj instanceof List)) {
                    throw new RuntimeException("chests.yml's lore was not a list for index " + i + " with map " + map.toString());
                }
                lore = (List) obj;
            }
            ChestLoot itemstack = new ChestLoot(material, chance, min, max, damage);
            if (enchant != null) {
                itemstack.applyEnchantments(enchant);
            }
            if (name != null) {
                itemstack.applyName(name);
            }
            if (lore != null) {
                itemstack.applyLore(lore);
            }
            this.itemList.add(itemstack);
        }
    }

    public List<ChestLoot> getItemList() {
        return this.itemList;
    }
}
