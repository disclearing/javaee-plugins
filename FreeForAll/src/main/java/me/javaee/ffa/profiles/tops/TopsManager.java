package me.javaee.ffa.profiles.tops;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import me.javaee.ffa.FFA;
import me.javaee.ffa.profiles.Profile;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Stream;

public class TopsManager implements Listener {
    private static final BlockFace[] BLOCK_FACES = { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

    @Getter private List<Profile> topKills = new ArrayList<>();
    @Getter private List<Profile> topElo = new ArrayList<>();
    @Getter private Set<Sign> signs = new HashSet<>();

    private Block getNearSkull(Block block) {
        return Stream.of(BLOCK_FACES).map(block::getRelative).filter(relative -> relative.getState() instanceof Skull).findFirst().orElse(null);
    }

    public TopsManager() {
        Bukkit.getPluginManager().registerEvents(this, FFA.getPlugin());

        for (Chunk chunk : FFA.getPlugin().getInformationManager().getInformation().getLobbyCuboid().getChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (state instanceof Sign) {
                    Sign sign = (Sign) state;

                    if (!signs.contains(sign) && sign.getLine(0).equalsIgnoreCase("[topkills]")) {
                        signs.add(sign);
                    }

                    if (!signs.contains(sign) && sign.getLine(0).equalsIgnoreCase("[topelo]")) {
                        signs.add(sign);
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Sign> iterator = signs.iterator();
                while (iterator.hasNext()) {
                    Sign sign = iterator.next();

                    if (!sign.getBlock().getType().name().contains("SIGN")) {
                        iterator.remove();
                        continue;
                    }

                    if (sign.getLine(0).equalsIgnoreCase("[topkills]")) {
                        String position = sign.getLine(1);

                        if (position.equalsIgnoreCase("1")) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().distance(sign.getLocation()) < 9)
                                    player.sendSignChange(sign.getLocation(), new String[]{ChatColor.BLACK + ChatColor.BOLD.toString() + "Top #1", "", ChatColor.DARK_PURPLE + topKills.get(0).getName(), ChatColor.BLUE.toString() + topKills.get(0).getKills() + " kills"});
                            }
                        } else if (position.equalsIgnoreCase("2")) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().distance(sign.getLocation()) < 9)
                                    player.sendSignChange(sign.getLocation(), new String[]{ChatColor.BLACK + ChatColor.BOLD.toString() + "Top #2", "", ChatColor.DARK_PURPLE + topKills.get(1).getName(), ChatColor.BLUE.toString() + topKills.get(1).getKills() + " kills"});
                            }
                        } else if (position.equalsIgnoreCase("3")) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().distance(sign.getLocation()) < 9)
                                    player.sendSignChange(sign.getLocation(), new String[]{ChatColor.BLACK + ChatColor.BOLD.toString() + "Top #3", "", ChatColor.DARK_PURPLE + topKills.get(2).getName(), ChatColor.BLUE.toString() + topKills.get(2).getKills() + " kills"});
                            }
                        }
                    }

                    if (sign.getLine(0).equalsIgnoreCase("[topelo]")) {
                        String position = sign.getLine(1);

                        if (position.equalsIgnoreCase("1")) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().distance(sign.getLocation()) < 9)
                                    player.sendSignChange(sign.getLocation(), new String[]{ChatColor.BLACK + ChatColor.BOLD.toString() + "Top #1", "", ChatColor.DARK_PURPLE + topElo.get(0).getName(), ChatColor.BLUE.toString() + topElo.get(0).getElo() + " elo"});
                            }
                        } else if (position.equalsIgnoreCase("2")) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().distance(sign.getLocation()) < 9)
                                    player.sendSignChange(sign.getLocation(), new String[]{ChatColor.BLACK + ChatColor.BOLD.toString() + "Top #2", "", ChatColor.DARK_PURPLE + topElo.get(1).getName(), ChatColor.BLUE.toString() + topElo.get(1).getElo() + " elo"});
                            }
                        } else if (position.equalsIgnoreCase("3")) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getLocation().distance(sign.getLocation()) < 9)
                                    player.sendSignChange(sign.getLocation(), new String[]{ChatColor.BLACK + ChatColor.BOLD.toString() + "Top #3", "", ChatColor.DARK_PURPLE + topElo.get(2).getName(), ChatColor.BLUE.toString() + topElo.get(2).getElo() + " elo"});
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(FFA.getPlugin(), 2L, 2L);

        Bukkit.getScheduler().runTaskTimer(FFA.getPlugin(), () -> {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aThe leaderboards have been updated..."));

            topKills.clear();
            for (Map.Entry<UUID, Object> kills : FFA.getPlugin().getProfileManager().getSortedValues("kills", Sorts.descending("kills"), Filters.gte("kills", 1), 3).entrySet()) {
                Profile profile = new Profile(kills.getKey());
                topKills.add(profile);
            }

            topElo.clear();
            for (Map.Entry<UUID, Object> kills : FFA.getPlugin().getProfileManager().getSortedValues("elo", Sorts.descending("elo"), Filters.gte("elo", 1), 3).entrySet()) {
                Profile profile = new Profile(kills.getKey());
                topElo.add(profile);
            }

            Iterator<Sign> iterator = signs.iterator();
            while (iterator.hasNext()) {
                Sign sign = iterator.next();

                if (!sign.getBlock().getType().name().contains("SIGN")) {
                    iterator.remove();
                    continue;
                }

                if (sign.getLine(0).equalsIgnoreCase("[topkills]")) {
                    String position = sign.getLine(1);
                    Location location = sign.getBlock().getLocation().add(-1, 1, 0);

                    if (position.equalsIgnoreCase("1")) {
                        Block b = location.getBlock();
                        b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                        Skull skull = (Skull) b.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(topKills.get(0).getName());
                        skull.setRotation(BlockFace.EAST);
                        skull.update(true);
                    } else if (position.equalsIgnoreCase("2")) {
                        Block b = location.getBlock();
                        b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                        Skull skull = (Skull) b.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(topKills.get(1).getName());
                        skull.setRotation(BlockFace.EAST);
                        skull.update(true);
                    } else if (position.equalsIgnoreCase("3")) {
                        Block b = location.getBlock();
                        b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                        Skull skull = (Skull) b.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(topKills.get(2).getName());
                        skull.setRotation(BlockFace.EAST);
                        skull.update(true);
                    }
                } else if (sign.getLine(0).equalsIgnoreCase("[topelo]")) {
                    String position = sign.getLine(1);
                    Location location = sign.getBlock().getLocation().add(1, 1, 0);

                    if (position.equalsIgnoreCase("1")) {
                        Block b = location.getBlock();
                        b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                        Skull skull = (Skull) b.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(topElo.get(0).getName());
                        skull.setRotation(BlockFace.WEST);
                        skull.update(true);
                    } else if (position.equalsIgnoreCase("2")) {
                        Block b = location.getBlock();
                        b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                        Skull skull = (Skull) b.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(topElo.get(1).getName());
                        skull.setRotation(BlockFace.WEST);
                        skull.update(true);
                    } else if (position.equalsIgnoreCase("3")) {
                        Block b = location.getBlock();
                        b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                        Skull skull = (Skull) b.getState();
                        skull.setSkullType(SkullType.PLAYER);
                        skull.setOwner(topElo.get(2).getName());
                        skull.setRotation(BlockFace.WEST);
                        skull.update(true);
                    }
                }
            }
        }, 0L, 20 * 5 * 60L);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        BlockState state = event.getBlock().getState();
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            if (signs.contains(sign)) {
                signs.remove(sign);
            }
        }
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                BlockState state = event.getBlock().getState();
                if (state instanceof Sign) {
                    Sign sign = (Sign) state;
                    if (!signs.contains(sign) && sign.getLine(0).equalsIgnoreCase("[topkills]")) {
                        signs.add(sign);
                    }

                    if (!signs.contains(sign) && sign.getLine(0).equalsIgnoreCase("[topelo]")) {
                        signs.add(sign);
                    }
                }
            }
        }.runTaskLater(FFA.getPlugin(), 2L);

        BlockState state = event.getBlock().getState();

        if (event.getLine(0).equalsIgnoreCase("[warp]") && event.getLine(1).equalsIgnoreCase("fullfps")) {
            event.setLine(0, ChatColor.translateAlternateColorCodes('&', "&7&m-----------"));
            event.setLine(1, ChatColor.translateAlternateColorCodes('&', "&aClick to warp"));
            event.setLine(2, ChatColor.translateAlternateColorCodes('&', "(FullFPS arena)"));
            event.setLine(3, ChatColor.translateAlternateColorCodes('&', "&7&m-----------"));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)) {
            BlockState state = event.getClickedBlock().getState();

            if (state instanceof Sign) {
                Sign sign = (Sign) state;

                if (sign.getLine(2).contains("FullFPS")) {
                    event.getPlayer().performCommand("warp fullfps");
                }
            }
        }
    }

    public void check() {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&aThe leaderboards have been updated..."));

        topKills.clear();
        for (Map.Entry<UUID, Object> kills : FFA.getPlugin().getProfileManager().getSortedValues("kills", Sorts.descending("kills"), Filters.gte("kills", 1), 3).entrySet()) {
            Profile profile = new Profile(kills.getKey());
            topKills.add(profile);
        }

        topElo.clear();
        for (Map.Entry<UUID, Object> kills : FFA.getPlugin().getProfileManager().getSortedValues("elo", Sorts.descending("elo"), Filters.gte("elo", 1), 3).entrySet()) {
            Profile profile = new Profile(kills.getKey());
            topElo.add(profile);
        }

        Iterator<Sign> iterator = signs.iterator();
        while (iterator.hasNext()) {
            Sign sign = iterator.next();

            if (!sign.getBlock().getType().name().contains("SIGN")) {
                iterator.remove();
                continue;
            }

            if (sign.getLine(0).equalsIgnoreCase("[topkills]")) {
                String position = sign.getLine(1);
                Location location = sign.getLocation().add(-1, 1, 0);

                if (position.equalsIgnoreCase("1")) {
                    Block b = location.getBlock();
                    b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                    Skull skull = (Skull) b.getState();
                    skull.setSkullType(SkullType.PLAYER);
                    skull.setOwner(topKills.get(0).getName());
                    skull.setRotation(BlockFace.valueOf("EAST"));
                    skull.update(true);
                } else if (position.equalsIgnoreCase("2")) {
                    Block b = location.getBlock();
                    b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                    Skull skull = (Skull) b.getState();
                    skull.setSkullType(SkullType.PLAYER);
                    skull.setOwner(topKills.get(1).getName());
                    skull.setRotation(BlockFace.valueOf("EAST"));
                    skull.update(true);
                } else if (position.equalsIgnoreCase("3")) {
                    Block b = location.getBlock();
                    b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                    Skull skull = (Skull) b.getState();
                    skull.setSkullType(SkullType.PLAYER);
                    skull.setOwner(topKills.get(2).getName());
                    skull.setRotation(BlockFace.valueOf("EAST"));
                    skull.update(true);
                }
            } else if (sign.getLine(0).equalsIgnoreCase("[topelo]")) {
                String position = sign.getLine(1);
                Location location = sign.getLocation().add(1, 1, 0);

                if (position.equalsIgnoreCase("1")) {
                    Block b = location.getBlock();
                    b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                    Skull skull = (Skull) b.getState();
                    skull.setSkullType(SkullType.PLAYER);
                    skull.setOwner(topElo.get(0).getName());
                    skull.setRotation(BlockFace.valueOf("WEST"));
                    skull.update(true);
                } else if (position.equalsIgnoreCase("2")) {
                    Block b = location.getBlock();
                    b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                    Skull skull = (Skull) b.getState();
                    skull.setSkullType(SkullType.PLAYER);
                    skull.setOwner(topElo.get(1).getName());
                    skull.setRotation(BlockFace.valueOf("WEST"));
                    skull.update(true);
                } else if (position.equalsIgnoreCase("3")) {
                    Block b = location.getBlock();
                    b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);

                    Skull skull = (Skull) b.getState();
                    skull.setSkullType(SkullType.PLAYER);
                    skull.setOwner(topElo.get(2).getName());
                    skull.setRotation(BlockFace.valueOf("WEST"));
                    skull.update(true);
                }
            }
        }
    }

    public Block getAttachedBlock(Block sb) {
        if (sb.getType() == Material.WALL_SIGN || sb.getType() == Material.SIGN_POST) {
            org.bukkit.material.Sign s = (org.bukkit.material.Sign) sb.getState().getData();  // org.bukkit.material.Sign

            return sb.getRelative(s.getAttachedFace());
        } else {
            return null;
        }
    }

    public BlockFace getFace(Block sb) {
        if (sb.getType() == Material.WALL_SIGN || sb.getType() == Material.SIGN_POST) {
            org.bukkit.material.Sign s = (org.bukkit.material.Sign) sb.getState().getData();  // org.bukkit.material.Sign

            return s.getFacing();
        } else {
            return null;
        }
    }
}
