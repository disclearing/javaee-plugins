package me.javaee.uhc.managers;

import me.javaee.uhc.UHC;
import me.javaee.uhc.enums.GameState;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

/*
 * Copyright (c) 2017, Álvaro Mariano. All rights reserved.
 *
 * Do not redistribute without permission from the author.
 */
public class ServerManager {

    public void setGameState() {
        try (Jedis jedis = UHC.getInstance().getPool().getResource()) {
            jedis.set("uhc1;gamestate", UHC.getInstance().getGameManager().getGameState().name().toUpperCase());
        }
    }

    public void setOnlinePlayers() {
        try (Jedis jedis = UHC.getInstance().getPool().getResource()) {
            jedis.set("uhc1;onlinePlayers", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
    }

    public void setMaxPlayers() {
        try (Jedis jedis = UHC.getInstance().getPool().getResource()) {
            jedis.set("uhc1;maxPlayers", String.valueOf(Bukkit.getMaxPlayers()));
        }
    }

    public void setAlivePlayers() {
        try (Jedis jedis = UHC.getInstance().getPool().getResource()) {
            jedis.set("uhc1;alivePlayers", String.valueOf(UHC.getInstance().getGameManager().getAlivePlayers().size()));
        }
    }

    public void setJoinedPlayers() {
        try (Jedis jedis = UHC.getInstance().getPool().getResource()) {
            jedis.set("uhc1;joinedPlayers", String.valueOf(UHC.getInstance().getGameManager().getJoinedPlayers()));
        }
    }

    public void setSpectators() {
        try (Jedis jedis = UHC.getInstance().getPool().getResource()) {
            jedis.set("uhc1;spectators", String.valueOf(UHC.getInstance().getSpectatorManager().getSpectators().size()));
        }
    }
}
