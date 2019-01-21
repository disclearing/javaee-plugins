package me.redis.queue.proxy;

import lombok.Getter;
import me.redis.queue.proxy.commands.JoinQueueCommand;
import me.redis.queue.proxy.commands.LeaveQueueCommand;
import me.redis.queue.proxy.commands.PauseQueueCommand;
import me.redis.queue.proxy.commands.QueueStatusCommand;
import me.redis.queue.proxy.listeners.PlayerListeners;
import me.redis.queue.proxy.profile.rank.RankManager;
import me.redis.queue.proxy.queue.Queue;
import me.redis.queue.proxy.queue.manager.QueueManager;
import me.redis.queue.proxy.redis.RedisMessagingHandler;
import me.redis.queue.proxy.redis.RedisWrapper;
import me.redis.queue.proxy.tasks.QueueAPITask;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Getter
public class QueueProxy extends Plugin {
    @Getter private static QueueProxy plugin;

    private Configuration config;
    private Configuration messages;

    private RedisWrapper redisWrapper;
    private RedisMessagingHandler redisMessagingHandler;

    private QueueManager queueManager;
    private RankManager rankManager;

    @Override
    public void onEnable() {
        plugin = this;

        loadConfig("config.yml");
        loadConfig("messages.yml");

        redisWrapper = new RedisWrapper(getConfig().getSection("redis"));
        redisMessagingHandler = new RedisMessagingHandler(this);

        rankManager = new RankManager();

        queueManager = new QueueManager();
        createQueues();

        getProxy().getScheduler().schedule(this, new QueueAPITask(), 1L, 1L, TimeUnit.SECONDS);

        getProxy().getPluginManager().registerCommand(this, new JoinQueueCommand());
        getProxy().getPluginManager().registerCommand(this, new LeaveQueueCommand());
        getProxy().getPluginManager().registerCommand(this, new PauseQueueCommand());
        getProxy().getPluginManager().registerCommand(this, new QueueStatusCommand());

        getProxy().getPluginManager().registerListener(this, new PlayerListeners());
    }

    private void createQueues() {
        for (ServerInfo serverInfo : getProxy().getServers().values()) {
            Queue queue = new Queue(serverInfo.getName());
            queueManager.getQueues().add(queue);

            System.out.println("[Queue-System] Created and added Queue(server=" + queue.getServer() + ")");
        }
    }

    private void loadConfig(String fileName) {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), fileName);

        if (!(file.exists())) {
            try (InputStream in = getResourceAsStream(fileName)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (fileName.equalsIgnoreCase("config.yml")) {
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), fileName));
            } else {
                messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), fileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
