package me.javaee.uhc.listeners.scenarios;

import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CutCleanListener implements Listener {
    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        if (Scenario.getByName("Cutclean").isEnabled()) {
            if (event.getEntity() instanceof Cow) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 3));
                event.getDrops().add(new ItemStack(Material.LEATHER));
            } else if (event.getEntity() instanceof Chicken) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 3));
                event.getDrops().add(new ItemStack(Material.FEATHER));
            } else if (event.getEntity() instanceof Pig) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
            } else if (event.getEntity() instanceof Horse) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(Material.LEATHER));
            }
        }
    }

    @EventHandler
    public void onBlockMined(BlockBreakEvent event) {
        if (Scenario.getByName("Cutclean").isEnabled()) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            World world = block.getLocation().getWorld();
            if (world.getName().equals("world") || world.getEnvironment() == World.Environment.NETHER) {

                if (block.getType().name().toLowerCase().contains("ore")) {
                    Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
                        UHC.getInstance().getMineManager().handleDatabaseMine(event);
                    });

                    UHC.getInstance().getMineManager().handleAlertsMine(event);
                }

                if (block.getType() == Material.GOLD_ORE) {
                    if (Scenario.getByName("Goldless").isEnabled()) {
                        return;
                    }
                }

                if (block.getType() == Material.GOLD_ORE && (player.getItemInHand() != null && (player.getItemInHand().getType() == Material.DIAMOND_PICKAXE || player.getItemInHand().getType() == Material.IRON_PICKAXE))) {
                    event.setCancelled(true);

                    block.setType(Material.AIR);
                    block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT));
                    createExpOrb(block.getLocation(), 6);

                    if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                        short dur = event.getPlayer().getItemInHand().getDurability();
                        if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                            player.setItemInHand(null);
                            player.updateInventory();
                        } else {
                            player.getItemInHand().setDurability(dur);
                        }
                    }
                } else if (block.getType() == Material.IRON_ORE) {
                    event.setCancelled(true);

                    block.setType(Material.AIR);
                    block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));

                    createExpOrb(block.getLocation(), 2);
                    if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType().getMaxDurability() > 0) {
                        short dur = event.getPlayer().getItemInHand().getDurability();
                        if (++dur >= event.getPlayer().getItemInHand().getType().getMaxDurability()) {
                            player.setItemInHand(null);
                            player.updateInventory();
                        } else {
                            player.getItemInHand().setDurability(dur);
                        }
                    }
                } else if (block.getType() == Material.GRAVEL) {
                    event.setCancelled(true);

                    block.setType(Material.AIR);
                    block.getLocation().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.FLINT));
                    if (player.getItemInHand() != null && player.getItemInHand().getType().getMaxDurability() > 0) {
                        short dur = player.getItemInHand().getDurability();
                        if (++dur >= player.getItemInHand().getType().getMaxDurability()) {
                            player.setItemInHand(null);
                            player.updateInventory();
                        } else {
                            player.getItemInHand().setDurability(dur);
                        }
                    }
                }
            }
        }
    }

    public void createExpOrb(Location location, int amount) {
        ExperienceOrb experienceOrb = (ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(amount);
    }
}
