package me.javaee.uhc.menu.menu;

import me.javaee.uhc.UHC;
import me.javaee.uhc.menu.type.ChestMenu;
import me.javaee.uhc.database.profile.Profile;
import me.javaee.uhc.database.profile.ProfileUtils;
import me.javaee.uhc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class StatsMenu extends ChestMenu<UHC> {
    Player player;

    public StatsMenu(Player player) {
        super(9);
        this.player = player;

        update();
    }

    @Override
    public String getTitle() {
        return "Statistics";
    }

    public void update() {
        inventory.clear();
        Profile profile = ProfileUtils.getInstance().getProfile(player.getUniqueId());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        ItemStack combat = new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setName("&aCombat").addLore(" &eKills&7: &c" + profile.getKills()).addLore(" &eDeaths&7: &c" + profile.getDeaths()).addLore(" &eKDR&7: &c" + decimalFormat.format((double) profile.getKills() / (double) profile.getDeaths())).build();
        ItemStack games = new ItemBuilder(Material.GLOWSTONE_DUST).setName("&aGames").addLore(" &eTotal&7: &c" + profile.getTotalGames()).addLore(" &eWins&7: &c" + profile.getWinnedGames()).build();
        ItemStack food = new ItemBuilder(Material.APPLE).setName("&aFood").addLore(" &eGolden Apples&7: &c" + profile.getGoldenApples()).addLore(" &eGolden Heads&7: &c" + profile.getGoldenHeads()).build();
        ItemStack ores = new ItemBuilder(Material.DIAMOND_PICKAXE).setName("&aMined").addLore(" &eIron Ore&7: &c" + profile.getIronMined()).addLore(" &eLapis Ore&7: &c" + profile.getLapisMined()).addLore(" &eRedstone Ore&7: &c" + profile.getRedstoneMined()).addLore(" &eGold Ore&7: &c" + profile.getGoldMined()).addLore(" &eDiamond Ore&7: &c" + profile.getDiamondsMined()).build();

        inventory.setItem(1, combat);
        inventory.setItem(3, games);
        inventory.setItem(5, food);
        inventory.setItem(7, ores);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (clickedInventory == null || topInventory == null || !topInventory.equals(inventory)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() != Material.AIR) {
                if (event.getWhoClicked() == UHC.getInstance().getGameManager().getHost()) {
                    ItemStack item = event.getCurrentItem();
                    player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1F, 1F);
                }
            }
        } else if (!topInventory.equals(clickedInventory) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }
}