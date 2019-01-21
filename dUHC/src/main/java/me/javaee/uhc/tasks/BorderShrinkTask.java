package me.javaee.uhc.tasks;

import lombok.Getter;
import me.javaee.uhc.UHC;
import me.javaee.uhc.events.BorderShrinkEvent;
import me.javaee.uhc.listeners.misc.WorldGenerationListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class BorderShrinkTask extends BukkitRunnable {
    public int startTime = 40;

    public int borderShrinkInterval = 5;
    public int borderShrinkBlocks = 500;
    public int borderMinimunRadius = 50;

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

    public BorderShrinkTask(int startTime, int borderShrinkInterval, int shrinkAmount, int borderMinimunRadius, int extraShrinkTime, int extraShrinkTime2) {
        this.startTime = startTime;
        this.borderShrinkInterval = borderShrinkInterval;
        this.borderShrinkBlocks = shrinkAmount;
        this.borderMinimunRadius = borderMinimunRadius;

        this.currentRadius = (int) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue();
        this.previousRadius = this.currentRadius;
    }

    @Override
    public void run() {
        // First time stuff
        if (!this.ranBefore) {
            BorderShrinkTask ezTask = UHC.getInstance().getBorderShrinkTask();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe world border will start shrinking in &6" + this.borderShrinkInterval + " &fminutes by &6" + this.borderShrinkBlocks + " &fblocks every &6" + ezTask.borderShrinkInterval + "&f minutes until the border size becomes &6" + ezTask.borderMinimunRadius + "&fx&6" + ezTask.borderMinimunRadius + "&f."));

            this.ranBefore = true;
        }

        this.counter++;

        // counter = # of ticks since we had last shrinkage
        // shrinkinterval is in minutes
         if ((borderShrinkInterval * 60 - counter) / 60 == 15) {
            if (!this.minRadiusHit) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe border will shrink to &6" + (currentRadius - borderShrinkBlocks) + " &fin &615 minutes&f."));
            }
        }

        if ((borderShrinkInterval * 60 - counter) / 60 == 30) {
            if (!this.minRadiusHit) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe border will shrink to &6" + (currentRadius - borderShrinkBlocks) + " &fin &630 minutes&f."));
            }
        }

        if (borderShrinkInterval * 60 - counter == 30) {
            if (!this.minRadiusHit) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe border will shrink to &6" + (currentRadius - borderShrinkBlocks) + " &fin &630 seconds&f.").replace("-400", "50").replace("-450", "25").replace(ChatColor.GOLD + "0 ", ChatColor.GOLD + "100 "));
            }
        }

        if (counter >= borderShrinkInterval * 60) {
            counter = 0;
            this.shrinkWorld();
        } else if ((!this.minRadiusHit && counter >= (borderShrinkInterval * 60 - 10)) || (this.minRadiusHit && this.extraShrinkCounter >= 1 && counter >= (borderShrinkInterval * 60 - 10))) {
            // Don't delay this, use Bukkit.broadcastMessage() (want to give them time to run)
            Bukkit.broadcastMessage((ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe border will shrink to &6" + (currentRadius - borderShrinkBlocks) + " &fin &6" + (borderShrinkInterval * 60 - counter) + " seconds&f.").replace("-900", "50").replace("-400", "50").replace("-450", "25")).replace(ChatColor.GOLD + "0", ChatColor.GOLD + "100"));

            // At 10 seconds start adding the initial bedrock border
            if (borderShrinkInterval * 60 - counter == 10) {
                // Use min radius if final shrink to fix extra 0,0 bedrock 4 block bug
                if (BorderShrinkTask.useBedRockBorder) {
                    WorldGenerationListener.addBedrockBorder(this.currentRadius - this.borderShrinkBlocks > this.borderMinimunRadius ? this.currentRadius - this.borderShrinkBlocks : this.borderMinimunRadius);
                }
            }
        }
    }

    public void shrinkWorld() {
         if (currentRadius == 2500) {
            setCurrentRadius(2000);
        } else if (currentRadius == 2000) {
            setCurrentRadius(1500);
        } else if (currentRadius == 1500) {
            setCurrentRadius(1000);
        } else if (currentRadius == 1000) {
            setCurrentRadius(500);
        } else if (currentRadius == 500) {
            setCurrentRadius(100);

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7&m---------------------------------------"));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(" &eThe border size is now &b100&e."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("  &7- &eYou are not allowed to make traps."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("  &7- &eYou are not allowed to camp."));
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7&m---------------------------------------"));
        } else if (currentRadius == 100) {
            setCurrentRadius(50);
        } else if (currentRadius == 50) {
            setCurrentRadius(25);

            ranBefore = false;

            new DeathmatchTask().runTaskTimer(UHC.getInstance(), 0L, 20L);
            new BlocksTask().runTaskTimer(UHC.getInstance(), 0L, 20L);

            cancel();
        }
    }

    public void setCurrentRadius(int radius) {
        UHC.getInstance().getServer().getPluginManager().callEvent(new BorderShrinkEvent(this.previousRadius, radius));
        UHC.getInstance().getGameManager().setCurrentRadius2(radius);

        UHC.getInstance().getServer().dispatchCommand(UHC.getInstance().getServer().getConsoleSender(), "wb world setcorners -" + radius + " -" + radius + " " + radius + " " + radius);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes("&7[&6&lSilexUHC&7] &fThe border has shrunk to &6" + radius + "&f."));
        UHC.getInstance().getBorderManager().setBorder(radius);
        currentRadius = radius;
        WorldGenerationListener.addBedrockBorder(radius, BorderShrinkTask.EXTRA_BEDROCK_BORDER_HEIGHT);
    }

}
