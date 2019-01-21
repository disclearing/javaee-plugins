package me.redis.practice.kit.commands;

import me.redis.practice.Practice;
import me.redis.practice.kit.Kit;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.profile.Profile;
import me.redis.practice.utils.SerializationUtils;
import me.redis.practice.utils.command.ExecutableCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand extends ExecutableCommand {
    public KitCommand() {
        super("kitgg");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Practice.getPlugin().getProfileManager().getProfile(player);

            if (args.length == 1) {
                String ladderName = args[0];

                if (Practice.getPlugin().getLadderManager().getLadder(ladderName) != null) {
                    Ladder ladder = Practice.getPlugin().getLadderManager().getLadder(ladderName);
                  //  Kit kit = new Kit("yeah", ladder);
                   // kit.setInventory(SerializationUtils.playerInventoryToString(player.getInventory()));

                  //  profile.getKits().add(kit);
                  //  profile.save();

                    System.out.println("Saved successfully");
                }
            }
        }

        return true;
    }
}
