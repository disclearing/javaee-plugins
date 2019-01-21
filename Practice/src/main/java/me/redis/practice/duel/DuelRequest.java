package me.redis.practice.duel;

import lombok.Getter;
import me.redis.practice.ladders.Ladder;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelRequest {

    @Getter private UUID identifier;
    @Getter private UUID sender;
    @Getter private UUID receiver;
    @Getter private Ladder ladder;

    public DuelRequest(Player sender, Player receiver, Ladder ladder) {
        this.identifier = UUID.randomUUID();
        this.sender = sender.getUniqueId();
        this.receiver = receiver.getUniqueId();
        this.ladder = ladder;

        new FancyMessage(
                "You have been sent a ").color(ChatColor.YELLOW)
                .then(ladder.getName()).color(ChatColor.GREEN)
                .then(" duel request by ").color(ChatColor.YELLOW)
                .then(sender.getName()).color(ChatColor.GREEN)
                .then(" (Click here to accept)").color(ChatColor.GREEN).command("/duel accept " + sender.getName())
                .then(".").color(ChatColor.YELLOW)
                .send(receiver);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have sent &a" + receiver.getName() + " &ea duel request."));
    }

}