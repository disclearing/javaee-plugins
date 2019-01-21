package me.redis.practice.spectator;

import me.redis.practice.Practice;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.match.IMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.PracticeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class SpectatorManager implements Listener {

    public SpectatorManager() {
        Bukkit.getPluginManager().registerEvents(this, Practice.getPlugin());
    }

    public void startSpectating(Player player, Player target) {
        Profile practiceProfile = Practice.getPlugin().getProfileManager().getProfile(player);

        if (practiceProfile.getStatus() == ProfileStatus.MATCH || practiceProfile.getStatus() == ProfileStatus.SPECTATOR) {
            player.sendMessage(ChatColor.RED + "You can't spectate a match if you are playing or spectating.");
            return;
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player is not online.");
            return;
        }

        Profile targetProfile = Practice.getPlugin().getProfileManager().getProfile(target);

        if (targetProfile.getStatus() == ProfileStatus.EDITING) {
            player.sendMessage(ChatColor.RED + "That player is in the kit editor.");
            return;
        }

        IMatch match = targetProfile.getCurrentMatch();

        if (match == null) {
            player.sendMessage(ChatColor.RED + "That match is no longer available.");
            return;
        }

        /*if (practiceProfile.getStatus() == ProfileStatus.EDITING) {
            ManagerHandler.getKitEditManager().getEditKits().remove(player.getUniqueId());
        }TODO*/

        practiceProfile.setStatus(ProfileStatus.SPECTATOR);
        practiceProfile.setSpectatingMatch(match);

        PracticeUtils.resetPlayer(player);

        player.getInventory().setContents(PracticeUtils.getSpectatorInventory());
        player.updateInventory();
        player.setAllowFlight(true);
        player.teleport(target);

        Practice.getPlugin().getEntityHider().hideAllPlayers(player);

        for (Player p : match.getPlayers()) {
            Practice.getPlugin().getEntityHider().showEntity(player, p);
        }
    }

    public void stopSpectating(Player player, boolean sendMsg) {
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

        profile.getSpectatingMatch().getSpectators().remove(player.getUniqueId());
        profile.setStatus(ProfileStatus.LOBBY);
        profile.setSpectatingMatch(null);

        PracticeUtils.resetPlayer(player);

        if (profile.getTeam() != null) {
            if (profile.getTeam().getLeader() == player) {
                player.getInventory().setContents(PracticeUtils.getTeamLeaderInventory());
            } else {
                player.getInventory().setContents(PracticeUtils.getTeamMemberInventory());
            }
        } else {
            player.getInventory().setContents(PracticeUtils.getLobbyInventory());
        }

        player.updateInventory();
        player.setAllowFlight(false);

        player.teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        Practice.getPlugin().getEntityHider().hideAllPlayers(player);
    }
}