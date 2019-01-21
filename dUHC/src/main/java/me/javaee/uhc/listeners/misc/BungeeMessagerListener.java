package me.javaee.uhc.listeners.misc;

import me.javaee.uhc.UHC;
import me.javaee.uhc.command.commands.ConfigCommand;
import me.javaee.uhc.handlers.ConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.ArrayList;

public class BungeeMessagerListener implements PluginMessageListener, Listener {
    private final UHC plugin;

    public BungeeMessagerListener(UHC plugin) {
        this.plugin = plugin;

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    public void sendBungeeMessage(Player player, String subChannel, String... args) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(subChannel);
            for (String data : args) {
                output.writeUTF(data);
            }

            player.sendPluginMessage(plugin, "BungeeCord", stream.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (channel.equals("BungeeCord")) {
            DataInputStream input = null;

            try {
                input = new DataInputStream(new ByteArrayInputStream(data));

                String subChannel = input.readUTF();

                if (subChannel.equals("UHC")) {
                    String type = input.readUTF();
                    if (type.equals("Chat")) {
                        String asd = input.readUTF();

                        ArrayList<String> message = new ArrayList<>();

                        message.add("&6&lUltrahardcore");
                        message.add(" &7- &6Teams&7: &f" + (UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue() <= 1 ? "FFA" : "To" + UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue()));
                        message.add(" &7- &6Scenarios&7: &f" + ConfigCommand.getScenarios());
                        message.add(" &7- &6Border&7: &f" + UHC.getInstance().getConfigurator().getIntegerOption("RADIUS").getValue());
                        message.add("");
                        if (asd.equalsIgnoreCase("OFF")) {
                            message.add("&6&lWhitelist off!");
                        } else {
                            message.add("&6&lWhitelisted off in &f&l" + asd + "&6&l.");
                        }

                        for (String string : message) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(string));
                        }

                        message.clear();
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }
}