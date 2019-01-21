package me.javaee.uhc.redis;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.javaee.uhc.UHC;
import me.javaee.uhc.handlers.Scenario;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;

public final class RedisMessagingHandler {
    private JedisPubSub subscriber;

    public RedisMessagingHandler() {
        try (Jedis jedis = UHC.getPool().getResource()) {
            Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> jedis.subscribe(subscriber = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    JsonObject data = new JsonParser().parse(message).getAsJsonObject();

                    switch (channel) {
                        case "uhc:whitelist": {
                            if (data.get("server").getAsString().equalsIgnoreCase(Bukkit.getServerName())) {
                                int minutes = data.get("minutes").getAsInt();

                                if (minutes == 0) {
                                    if (Scenario.getByName("Event Game").isEnabled()) {
                                        UHC.getInstance().getConfigurator().getBooleanOption("CANJOIN").setValue(true);
                                        return;
                                    }

                                    Bukkit.setWhitelist(false);
                                } else if (minutes == 5) {
                                    UHC.getInstance().getConfigurator().getBooleanOption("VIPJOIN").setValue(true);
                                }
                            }
                            break;
                        }

                        case "uhc:getconfig": {
                            JsonObject object = new JsonObject();

                            object.addProperty("player", data.get("player").getAsString());

                            boolean speed = UHC.getInstance().getConfigurator().getBooleanOption("SPEED").getValue();
                            boolean strength = UHC.getInstance().getConfigurator().getBooleanOption("STRENGTH").getValue();
                            boolean friendly = UHC.getInstance().getConfigurator().getBooleanOption("FRIENDLYFIRE").getValue();
                            int teamsize = UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue();

                            object.addProperty("1", ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);
                            object.addProperty("2", ChatColor.GOLD.toString() + ChatColor.BOLD + "Silex " + ChatColor.WHITE + ChatColor.BOLD + "Configuration");
                            object.addProperty("3", ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);
                            object.addProperty("4", ChatColor.translateAlternateColorCodes("  &l" + (teamsize <= 1 ? "FFA" : "To" + teamsize + " &7(" + (friendly ? "&aFriendly Fire" : "&c&mFriendly Fire") + "&7)") + " &f- " + ChatColor.GREEN + getScenarios()));
                            object.addProperty("5", ChatColor.translateAlternateColorCodes("    &fNether: " + (Scenario.getByName("Nether").isEnabled() ? "&aEnabled" : "&cDisabled")));
                            object.addProperty("6", ChatColor.translateAlternateColorCodes("    &fEnd: " + (Scenario.getByName("End").isEnabled() ? "&aEnabled" : "&cDisabled")));
                            object.addProperty("7", ChatColor.translateAlternateColorCodes("    &fInitial Border: &e" + (UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue() != null ? (int) UHC.getInstance().getConfigurator().getOption(UHC.CONFIG_OPTIONS.RADIUS.name()).getValue() : "None")));
                            object.addProperty("8", ChatColor.translateAlternateColorCodes("    &fFirst Shrink: &e" + (UHC.getInstance().getBorderShrinkTask().startTime != 0 ? UHC.getInstance().getBorderShrinkTask().startTime : "None") + " minutes"));
                            object.addProperty("9", ChatColor.translateAlternateColorCodes("    &fBorder Shrink Interval: &e" + (UHC.getInstance().getBorderShrinkTask().borderShrinkInterval != 0 ? UHC.getInstance().getBorderShrinkTask().borderShrinkInterval : "None") + " minutes"));
                            object.addProperty("10", ChatColor.translateAlternateColorCodes("    &fFinal Border: &e" + (UHC.getInstance().getBorderShrinkTask().borderMinimunRadius != 0 ? UHC.getInstance().getBorderShrinkTask().borderMinimunRadius : "None") + " blocks"));
                            object.addProperty("11", ChatColor.translateAlternateColorCodes("    &fApple Rate: &e" + UHC.getInstance().getConfigurator().getIntegerOption("APPLERATE").getValue() + "%"));
                            object.addProperty("12", ChatColor.translateAlternateColorCodes("    &fShears: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("SHEARS").getValue()))));
                            object.addProperty("13", ChatColor.translateAlternateColorCodes("    &fPvP Time: &e" + UHC.getInstance().getConfigurator().getIntegerOption("PVPTIME").getValue() + " minutes"));
                            object.addProperty("14", ChatColor.translateAlternateColorCodes("    &fHeal Time: &e" + UHC.getInstance().getConfigurator().getIntegerOption("HEALTIME").getValue() + " minutes"));

                            int f = UHC.getInstance().getConfigurator().getIntegerOption("STRENGTHLVL").getValue();
                            int v = UHC.getInstance().getConfigurator().getIntegerOption("SPEEDLVL").getValue();
                            boolean poision = UHC.getInstance().getConfigurator().getBooleanOption("DEBUFFS").getValue();

                            object.addProperty("15", ChatColor.translateAlternateColorCodes("    &fPotions: &e" + (speed ? "&aSpeed " + v : "&c&mSpeed " + v) + "&e, " + (strength ? "&aStrength " + f : "&c&mStrength " + f) + "&e, " + (poision ? "&aDebuffs" : "&c&mDebuffs")));
                            object.addProperty("16", ChatColor.translateAlternateColorCodes("    &fStatLess: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("STATLESS").getValue()))));
                            object.addProperty("17", ChatColor.translateAlternateColorCodes("    &fBed Bombs: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("BED").getValue()))));
                            object.addProperty("18", ChatColor.translateAlternateColorCodes("    &fDeath Kick: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("DEATHKICK").getValue()))));
                            object.addProperty("19", ChatColor.translateAlternateColorCodes("    &fEnderpearl Damage: &e" + WordUtils.capitalize(String.valueOf(UHC.getInstance().getConfigurator().getBooleanOption("ENDERPEARLDAMAGE").getValue()))));
                            object.addProperty("20", ChatColor.GRAY + STRAIGHT_LINE_DEFAULT);

                            publish("uhc:config", object.toString());

                            break;
                        }
                    }
                }
            }, "uhc:whitelist", "uhc:getconfig"));
        }
    }

    public void unsubscribe() {
        subscriber.unsubscribe();
    }

    public void sendMessage(String channel, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(UHC.getInstance(), () -> {
            try (Jedis jedis = UHC.getPool().getResource()) {
                jedis.publish(channel, message);
            }
        });
    }

    public void publish(String channel, String message) {
        try (Jedis jedis = UHC.getPool().getResource()) {
            jedis.publish(channel, message);
        }
    }

    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256);
    public static String STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);

    public static String getScenarios() {
        List<String> lines = new ArrayList<>();
        String scenario = "";

        for (Scenario scenarios : UHC.getInstance().getScenarios()) {
            if (scenarios.isEnabled()) {
                lines.add(scenarios.getName());
                if (lines.contains("End")) {
                    lines.remove("End");
                }
                if (lines.contains("Nether")) {
                    lines.remove("Nether");
                }
                scenario = StringUtils.join(lines, ", ");
            }
        }

        if (scenario.equalsIgnoreCase("")) {
            return "None";
        } else {
            return scenario;
        }
    }
}