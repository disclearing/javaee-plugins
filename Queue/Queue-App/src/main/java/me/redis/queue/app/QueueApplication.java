package me.redis.queue.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.redis.queue.app.api.manager.QueueManager;
import me.redis.queue.app.redis.RedisWrapper;

public class QueueApplication extends Application {
    public static RedisWrapper redisWrapper;
    public static QueueManager queueManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("QueueStatus.fxml"));

        primaryStage.setTitle("rQueue System");
        primaryStage.setScene(new Scene(root, 192, 137));
        primaryStage.show();
    }

    public static void main(String[] args) {
        redisWrapper = new RedisWrapper();
        queueManager = new QueueManager();

        launch(args);
    }
}
