package me.javaee.meetup.tasks;

import lombok.Getter;
import me.javaee.meetup.Meetup;
import me.javaee.meetup.events.BorderShrinkEvent;
import me.javaee.meetup.listeners.WorldGenerationListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter public class BorderShrinkTask extends BukkitRunnable {
    public int startTime = 1;
    public int shrinkInterval = 2;
    public int shrinkAmount = 25;
    public int minimumRadius = 25;
    public int currentRadius;
    public int previousRadius;
    public boolean minRadiusHit = false;
    public int extraShrinkCounter = 0;
    public int extraShrinkTime;
    public int extraShrinkTime2;

    private int counter = 0;

    private boolean ranBefore = false;

    public static boolean useBedRockBorder = true;
    public static int EXTRA_BEDROCK_BORDER_HEIGHT = 5;

    public BorderShrinkTask(int startTime, int shrinkInterval, int shrinkAmount, int minimumRadius, int extraShrinkTime, int extraShrinkTime2) {
        this.startTime = startTime;
        this.shrinkInterval = shrinkInterval;
        this.shrinkAmount = shrinkAmount;
        this.minimumRadius = minimumRadius;

        this.currentRadius = 125;
        this.previousRadius = this.currentRadius;
    }

    @Override
    public void run() {
        // First time stuff
        if (!this.ranBefore) {
            BorderShrinkTask ezTask = Meetup.getPlugin().getBorderShrinkTask();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &fThe world border will start shrinking in &6" + this.shrinkInterval + " &fminutes by &6" + this.shrinkAmount + " &fblocks every &6" + ezTask.shrinkInterval + "&f minutes until the border size becomes &6" + ezTask.minimumRadius + "&fx&6" + ezTask.minimumRadius + "&f."));
            this.ranBefore = true;
        }

        this.counter++;

        // counter = # of ticks since we had last shrinkage
        // shrinkinterval is in minutes
        if ((shrinkInterval * 60 - counter) / 60 == 15) {
            if (!this.minRadiusHit) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &fThe border will shrink to &6" + (currentRadius - shrinkAmount) + " &fin &615 minutes&f."));
            }
        }

        if ((shrinkInterval * 60 - counter) / 60 == 30) {
            if (!this.minRadiusHit) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &fThe border will shrink to &6" + (currentRadius - shrinkAmount) + " &fin &630 minutes&f."));
            }
        }

        if (shrinkInterval * 60 - counter == 30) {
            if (!this.minRadiusHit) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &fThe border will shrink to &6" + (currentRadius - shrinkAmount) + " &fin &630 seconds&f.").replace("-400", "50").replace("-450", "25").replace(ChatColor.GOLD + "0 ", ChatColor.GOLD + "100 "));
            }
        }

        if (counter >= shrinkInterval * 60) {
            counter = 0;
            this.shrinkWorld();
        } else if ((!this.minRadiusHit && counter >= (shrinkInterval * 60 - 10)) || (this.minRadiusHit && this.extraShrinkCounter >= 1 && counter >= (shrinkInterval * 60 - 10))) {
            // Don't delay this, use Bukkit.broadcastMessage() (want to give them time to run)
            Bukkit.broadcastMessage((ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &fThe border will shrink to &6" + (currentRadius - shrinkAmount) + " &fin &6" + (shrinkInterval * 60 - counter) + " seconds&f.").replace("-900", "50").replace("-400", "50").replace("-450", "25")).replace(ChatColor.GOLD + "0", ChatColor.GOLD + "100"));

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 100000, 100000);
            }

            // At 10 seconds start adding the initial bedrock border
            if (shrinkInterval * 60 - counter == 10) {
                // Use min radius if final shrink to fix extra 0,0 bedrock 4 block bug
                if (currentRadius == 100) {
                    WorldGenerationListener.addBedrockBorder(75);
                } else if (currentRadius == 75) {
                    WorldGenerationListener.addBedrockBorder(50);
                } else if (currentRadius == 50) {
                    WorldGenerationListener.addBedrockBorder(25);
                }
            }
        }
    }

    public void shrinkWorld() {
        if (currentRadius == 75) {
            setCurrentRadius(50);
        } else if (currentRadius == 50) {
            setCurrentRadius(25);

            ranBefore = false;
            cancel();
        }
    }

    public void setCurrentRadius(int radius) {
        Meetup.getPlugin().getServer().getPluginManager().callEvent(new BorderShrinkEvent(this.previousRadius, radius));
        Meetup.getPlugin().getGameManager().setCurrentRadius2(radius);

        Meetup.getPlugin().getServer().dispatchCommand(Meetup.getPlugin().getServer().getConsoleSender(), "wb world setcorners -" + radius + " -" + radius + " " + radius + " " + radius);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6&lUHCMeetup&7] &fThe border has shrunk to &6" + radius + "&f."));
        Meetup.getPlugin().getBorderManager().setBorder(radius);
        currentRadius = radius;
        WorldGenerationListener.addBedrockBorder(radius, BorderShrinkTask.EXTRA_BEDROCK_BORDER_HEIGHT);
    }

}
