package me.javaee.uhc.tasks;

import com.google.gson.JsonObject;
import me.javaee.uhc.UHC;
import me.javaee.uhc.command.commands.WorldLoaderCommand;
import me.javaee.uhc.handlers.Scenario;
import me.javaee.uhc.utils.StringCommon;
import net.badlion.worldborder.WorldFillTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RedisDataTask extends BukkitRunnable {
    @Override
    public void run() {
        JsonObject object = new JsonObject();

        object.addProperty("server", Bukkit.getServerName());
        object.addProperty("gamestate", UHC.getInstance().getGameManager().getGameState().name());
        object.addProperty("online", Bukkit.getOnlinePlayers().size());
        object.addProperty("teams", UHC.getInstance().getConfigurator().getIntegerOption("TEAMSIZE").getValue());
        object.addProperty("gameTime", StringCommon.niceTime(GameTimeTask.getNumOfSeconds(), false));
        object.addProperty("scenarios", getScenarios());
        object.addProperty("border", UHC.getInstance().getBorderShrinkTask().getCurrentRadius());
        object.addProperty("alive", UHC.getInstance().getGameManager().getAlivePlayers().size());
        object.addProperty("maxAlive", UHC.getInstance().getGameManager().getJoinedPlayers());
        object.addProperty("whitelist", Bukkit.hasWhitelist());
        object.addProperty("generating", WorldLoaderCommand.generating);
        object.addProperty("minutes", UHC.getInstance().getMinutes());

        if (WorldFillTask.percetage == null) {
            object.addProperty("percentage", "0%");
            object.addProperty("loadedChunks", 0);
        } else {
            object.addProperty("percentage", WorldFillTask.percetage);
            object.addProperty("loadedChunks", WorldFillTask.loadedChunks);
        }

        UHC.getInstance().getRedisMessagingHandler().sendMessage("uhc:information", object.toString());
    }

    public String getScenarios() {
        List<String> toReturn = new ArrayList<>();

        for (Scenario scenario : UHC.getInstance().getScenarios()) {
            if (scenario.isEnabled()) toReturn.add(scenario.getName());
        }

        return toReturn.isEmpty() ? "None" : org.apache.commons.lang.StringUtils.join(toReturn, ", ");
    }
}
