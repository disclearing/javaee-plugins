package me.javaee.meetup.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static sun.audio.AudioPlayer.player;

public class GameTimeTask extends BukkitRunnable {
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
            // Figure out logic for dragon bar
            boolean initializing = false;
            if (!GameTimeTask.initialized) {
                --GameTimeTask.seconds; // One time only
                initializing = true;
            }

            // One time only
            if (initializing) {
                ++GameTimeTask.seconds;
            }

            GameTimeTask.initialized = true;
        }
    }

    public static int getNumOfSeconds() {
        return GameTimeTask.seconds + (GameTimeTask.minutes * 60) + (GameTimeTask.hours * 60 * 60);
    }

}
