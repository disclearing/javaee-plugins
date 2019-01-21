package me.javaee.uhc.managers;

import me.javaee.uhc.UHC;
import me.javaee.uhc.listeners.stats.FoundOresListener;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class MineManager {
    public void handleDatabaseMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        Boolean statLess = (Boolean) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.STATLESS.name()).getValue();
        Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());

        if (block == Material.DIAMOND_ORE) {
            profile.setMatchDiamondsMined(profile.getMatchDiamondsMined() + 1);
            UHC.getInstance().getGameManager().getDiamonds().put(player.getUniqueId(), UHC.getInstance().getGameManager().getDiamonds().get(player.getUniqueId()) + 1);
        } else if (block == Material.GOLD_ORE) {
            profile.setMatchGoldMined(profile.getMatchGoldMined() + 1);
        } else if (block == Material.IRON_ORE) {
            profile.setMatchIronMined(profile.getMatchIronMined() + 1);
        } else if (block == Material.GLOWING_REDSTONE_ORE) {
            profile.setMatchRedstoneMined(profile.getMatchRedstoneMined() + 1);
        } else if (block == Material.LAPIS_ORE) {
            profile.setMatchLapisMined(profile.getMatchLapisMined() + 1);
        } else if (block == Material.COAL_ORE) {
            profile.setMatchCoalMined(profile.getMatchCoalMined() + 1);
        }

        if (!statLess) {
            if (block == Material.DIAMOND_ORE) {
                profile.setDiamondsMined(profile.getDiamondsMined() + 1);
            } else if (block == Material.GOLD_ORE) {
                profile.setGoldMined(profile.getGoldMined() + 1);
            } else if (block == Material.IRON_ORE) {
                profile.setIronMined(profile.getIronMined() + 1);
            } else if (block == Material.GLOWING_REDSTONE_ORE) {
                profile.setRedstoneMined(profile.getRedstoneMined() + 1);
            } else if (block == Material.LAPIS_ORE) {
                profile.setLapisMined(profile.getLapisMined() + 1);
            } else if (block == Material.COAL_ORE) {
                profile.setCoalMined(profile.getCoalMined() + 1);
            }
        }
    }

    public void handleAlertsMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) return;

        Profile tipo = ProfileUtils.getInstance().getProfile(player.getUniqueId());
        if (block.getType() == FoundOresListener.SEARCH_TYPE && FoundOresListener.foundLocations.add(blockLocation.toString())) {
            int diamond = 1;
            for (int x = -5; x < 5; ++x) {
                for (int y = -5; y < 5; ++y) {
                    for (int z = -5; z < 5; ++z) {
                        Block otherBlock = blockLocation.clone().add((double) x, (double) y, (double) z).getBlock();
                        if (!otherBlock.equals(block) && otherBlock.getType() == FoundOresListener.SEARCH_TYPE && FoundOresListener.foundLocations.add(otherBlock.getLocation().toString())) {
                            ++diamond;
                        }
                    }
                }
            }

            for (Player mods : UHC.getInstance().getGameManager().getModerators()) {
                Profile profile = ProfileUtils.getInstance().getProfile(mods.getUniqueId());

                if (block.getType() == Material.DIAMOND_ORE) {
                    if (profile.isAlerts()) {
                        if (mods == UHC.getInstance().getGameManager().getHost()) {
                            return;
                        }

                        ComponentBuilder builder = new ComponentBuilder("");
                        builder.append(player.getName()).color(net.md_5.bungee.api.ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click to teleport to " + player.getName()).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        builder.append(" has found ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(String.valueOf(diamond)).color(net.md_5.bungee.api.ChatColor.AQUA);
                        builder.append(" diamond(s). [" + tipo.getMatchDiamondsMined() + "]").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                        BaseComponent[] components = builder.create();
                        mods.spigot().sendMessage(components);
                    }
                }
            }

            if (UHC.getInstance().getGameManager().getHost() != null) {
                Player host = UHC.getInstance().getGameManager().getHost();
                Profile profile = ProfileUtils.getInstance().getProfile(host.getUniqueId());

                if (block.getType() == Material.DIAMOND_ORE) {
                    if (profile.isAlerts()) {
                        ComponentBuilder builder = new ComponentBuilder("");
                        builder.append(player.getName()).color(net.md_5.bungee.api.ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Click to teleport to " + player.getName()).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName()));
                        builder.append(" has found ").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.append(String.valueOf(diamond)).color(net.md_5.bungee.api.ChatColor.AQUA);
                        builder.append(" diamond(s). [" + tipo.getMatchDiamondsMined() + "]").color(net.md_5.bungee.api.ChatColor.GRAY);
                        builder.retain(ComponentBuilder.FormatRetention.FORMATTING);

                        BaseComponent[] components = builder.create();
                        host.spigot().sendMessage(components);
                    }
                }
            }
        }
    }
}
