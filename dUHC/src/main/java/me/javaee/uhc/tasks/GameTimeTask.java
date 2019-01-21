package me.javaee.uhc.tasks;

import lombok.Getter;
import me.javaee.uhc.UHC;
import org.bukkit.scheduler.BukkitRunnable;

@Getter public class GameTimeTask extends BukkitRunnable {
    public static boolean initialized = false;

    public static int healTime;
    public static int pvpTime;

    public static int seconds = 0;
    public static int minutes = 0;
    public static int hours = 0;

    @Override
    public void run() {
        if (++GameTimeTask.seconds == 60) {
            ++GameTimeTask.minutes;
            GameTimeTask.seconds = 0;
        }

        if (GameTimeTask.minutes == 60) {
            ++GameTimeTask.hours;
            GameTimeTask.minutes = 0;
        }

        if (!GameTimeTask.initialized || seconds == 0 || seconds % 5 == 0) {
            boolean initializing = false;
            if (!GameTimeTask.initialized) {
                --GameTimeTask.seconds; // One time only
                GameTimeTask.pvpTime = UHC.getInstance().getConfigurator().getIntegerOption(UHC.CONFIG_OPTIONS.PVPTIME.name()).getValue() * 60;
                GameTimeTask.healTime = UHC.getInstance().getConfigurator().getIntegerOption(UHC.CONFIG_OPTIONS.HEALTIME.name()).getValue() * 60;
                initializing = true;
            }

            if (initializing) {
                ++GameTimeTask.seconds;
            }

            GameTimeTask.initialized = true;
        }
    }

     public static int getNumOfSeconds() {
         return seconds + (minutes * 60) + (hours * 60 * 60);
     }

    public int getSeconds() {
        return seconds + (minutes * 60) + (hours * 60 * 60);
    }

    public int getMinutes() {
        return getSeconds() / 60;
    }
}
