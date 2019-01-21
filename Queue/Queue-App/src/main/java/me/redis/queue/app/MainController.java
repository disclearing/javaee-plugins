package me.redis.queue.app;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import me.redis.queue.app.api.Queue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class MainController {
    private JedisPubSub subscriber;

    @FXML ChoiceBox list;
    @FXML Text players;
    @FXML Text whitelist;
    @FXML Text paused;
    @FXML Text inqueue;

    @FXML
    public void initialize() {
        try (Jedis jedis = QueueApplication.redisWrapper.getJedis()) {
            new Thread(() -> {
                jedis.subscribe(subscriber = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        JsonObject data = new JsonParser().parse(message).getAsJsonObject();

                        switch (channel) {
                            case "queue:information": {
                                String server = data.get("server").getAsString();
                                int maxPlayers = data.get("maxPlayers").getAsInt();
                                int onlinePlayers = data.get("onlinePlayers").getAsInt();
                                boolean whitelisted = data.get("whitelisted").getAsBoolean();

                                if (QueueApplication.queueManager.getByServer(server) == null) {
                                    Queue queue = new Queue(server);
                                    QueueApplication.queueManager.getQueues().add(queue);

                                    list.getItems().add(queue.getServer());
                                }

                                if (QueueApplication.queueManager.getByServer(server) != null) {
                                    Queue queue = QueueApplication.queueManager.getByServer(server);

                                    queue.setMaxPlayers(maxPlayers);
                                    queue.setOnlinePlayers(onlinePlayers);
                                    queue.setWhitelisted(whitelisted);

                                    if (list.getValue() != null && list.getValue().equals(queue.getServer())) {
                                        players.setText(queue.getOnlinePlayers() + "/" + queue.getMaxPlayers());
                                        whitelist.setText(String.valueOf(whitelisted));
                                    }
                                }

                                break;
                            }

                            case "queue:end": {
                                String server = data.get("server").getAsString();
                                boolean online = data.get("online").getAsBoolean();

                                if (QueueApplication.queueManager.getByServer(server) != null) {
                                    Queue queue = QueueApplication.queueManager.getByServer(server);

                                    queue.setOnline(online);
                                }

                                break;
                            }
                        }
                    }
                }, "queue:information", "queue:end");
            }).start();
        }
    }
    public void unsubscribe() {
        subscriber.unsubscribe();
    }

    public void sendMessage(String channel, String message) {
        try (Jedis jedis = QueueApplication.redisWrapper.getJedis()) {
            jedis.publish(channel, message);
        }
    }
}
