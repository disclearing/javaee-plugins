package me.redis.kohi.schematic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.redis.kohi.SurvivalGames;
import me.redis.kohi.chest.ChestInformation;
import me.redis.kohi.chest.ChestLoot;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SchematicPopulator extends BlockPopulator {
    private ChestInformation chestConfiguration;
    private List<SchematicHandler> list;
    private List<Material> ok;

    public SchematicPopulator(ChestInformation chestConfiguration) {
        this.list = new ArrayList();
        this.ok = Arrays.asList(new Material[]{Material.AIR, Material.SNOW, Material.SNOW_BLOCK, Material.SNOW_BLOCK, Material.DIRT, Material.GRASS, Material.DEAD_BUSH, Material.RED_ROSE, Material.YELLOW_FLOWER, Material.LONG_GRASS, Material.SAND});
        this.chestConfiguration = chestConfiguration;
        File schematicFolder = new File((SurvivalGames.getPlugin()).getDataFolder(), "schematic");
        schematicFolder.mkdirs();
        File configuration = new File(schematicFolder, "config");
        configuration.mkdirs();
        File[] listFiles;
        int length = (listFiles = schematicFolder.listFiles()).length;
        for (int i = 0; i < length; i++) {
            File schematic = listFiles[i];
            if (schematic.getName().endsWith(".schematic")) {
                String name = schematic.getName().replace(".schematic", "");
                File config = new File(configuration, String.valueOf(name) + ".yml");
                SchematicHandler hold = new SchematicHandler();
                try {
                    hold.loadSchmeatic(schematic);
                    YamlConfiguration yamlConfig = ensureCorrect(config);
                    hold.setYOffset(yamlConfig.getInt("YOffset"));
                    hold.setPercentage(yamlConfig.getInt("Percentage"));
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                this.list.add(hold);
            }
        }
    }

    public YamlConfiguration ensureCorrect(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.contains("YOffset")) {
            configuration.set("YOffset", Integer.valueOf(-1));
        }

        if (!configuration.contains("Percentage")) {
            configuration.set("Percentage", Integer.valueOf(2500));
        }

        configuration.save(file);
        return configuration;
    }

    public void populate(World world, Random rand, Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        if (chunkX >= -1 && chunkX <= 1) {
            return;
        }

        if (chunkZ >= -1 && chunkZ <= 1) {
            return;
        }

        int skipZ = 0;
        int skipX = 0;
        for (int x = 0; x < 16; x++) {
            if (skipX > 0) {
                skipX--;
            } else {
                for (int z = 0; z < 16; z++) {
                    if (skipZ > 0) {
                        skipZ--;
                    } else {
                        Collections.shuffle(this.list);
                        for (SchematicHandler man2 : this.list) {
                            if ((canPlace(man2, chunk, x, z)) && (rand.nextInt(man2.getPercentage()) == 0)) {
                                paste(man2, chunk.getWorld(), chunk.getX() * 16 + x, chunk.getZ() * 16 + z, rand);
                                skipZ = man2.getLength() + 1;
                                skipX = man2.getWidth() + 1;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void paste(SchematicHandler man, World world, int blockX, int blockZ, Random rand) {
        man.rotateRandomly();
        int height = man.getHeight();
        int starty = world.getHighestBlockAt(blockX, blockZ).getY() + man.getYOffset();

        for (int x = 0; x < man.getWidth(); x++) {
            for (int z = 0; z < man.getLength(); z++) {
                int realX = x + blockX;
                int realZ = z + blockZ;
                for (int y = 0; y < height; y++) {
                    int id = man.getBlockIDAt(x, y, z);
                    int data = man.getDataAt(x, y, z);
                    int realY = starty + y;
                    if (id > 0) {
                        Block block = world.getBlockAt(realX, realY, realZ);
                        if (id == 63) {
                            block.setTypeId(0);
                            block.setData((byte) 0);
                        } else {
                            block.setTypeId(id);
                            block.setData((byte) data);
                            if (id == 52) {
                                BlockState spawner = block.getState();
                                if ((spawner instanceof CreatureSpawner)) {
                                    CreatureSpawner creatureSpawner = (CreatureSpawner) spawner;
                                    if (block.getRelative(BlockFace.DOWN).getType() == Material.WOOD) {
                                        creatureSpawner.setSpawnedType(EntityType.CREEPER);
                                    } else {
                                        creatureSpawner.setSpawnedType(EntityType.ZOMBIE);
                                    }
                                }
                            } else if (world.getBlockAt(realX, realY, realZ).getState() instanceof Chest) {
                                Chest chest = (Chest) world.getBlockAt(realX, realY, realZ).getState();

                                if (getAmountOfItems(chest.getInventory()) >= 4) {
                                    return;
                                }

                                for (ChestLoot item : this.chestConfiguration.getItemList()) {
                                    if (item.hasChance(rand)) {
                                        ItemStack items = item.getRandomItemStack(rand);
                                        chest.getInventory().addItem(items);
                                    }
                                }

                                boolean empty = true;
                                ItemStack[] contents;
                                int length = (contents = chest.getInventory().getContents()).length;
                                for (int i = 0; i < length; i++) {
                                    ItemStack item2 = contents[i];
                                    if ((item2 == null) || (item2.getType() == Material.AIR)) {
                                        empty = false;
                                        break;
                                    }
                                }

                                int i = 0;
                                for (ItemStack is : chest.getInventory().getContents()) {
                                    if (is != null && is.getType() == Material.STICK) {
                                        i = i + is.getAmount();
                                    }
                                }

                                if (empty) {
                                    for (ChestLoot item3 : this.chestConfiguration.getItemList()) {
                                        if (item3.hasChance(rand)) {
                                            ItemStack items2 = item3.getRandomItemStack(rand);
                                            chest.getInventory().addItem(items2);
                                        }
                                    }
                                }

                                if (getAmountOfItems(chest.getInventory()) <= 0) {
                                    for (ChestLoot item : this.chestConfiguration.getItemList()) {
                                        chest.getInventory().addItem(item.getRandomItemStack(rand));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean canPlace(SchematicHandler schematic, Chunk chunk, int placeX, int placeZ) {
        int foundY = -1;
        for (int x = 0; x < schematic.getWidth(); x++) {
            for (int z = 0; z < schematic.getLength(); z++) {
                Block block = chunk.getWorld().getHighestBlockAt(chunk.getX() * 16 + placeX + x, chunk.getZ() * 16 + placeZ + z);
                if ((!this.ok.contains(block.getType())) || (!this.ok.contains(block.getRelative(BlockFace.DOWN).getType()))) {
                    return false;
                }
                if ((foundY != -1) && (block.getY() != foundY)) {
                    return false;
                }
                foundY = block.getY();
            }
        }
        return true;
    }

    public int getAmountOfItems(Inventory inventory) {
        int toReturn = 0;

        for (ItemStack items : inventory.getContents()) {
            if (items != null) {
                toReturn++;
            }
        }

        return toReturn;
    }
}