package me.javaee.uhc.utils;

import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.Callable;

public class GATextureFetcher implements Callable<GATextureFetcher.Lookup<GATextureFetcher.SkinTexture>> {

    private static final String PROFILE_URL = "https://use.gameapis.net/mc/player/profile/%s";

    private final UUID uniqueId;

    public GATextureFetcher(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public Lookup<SkinTexture> call() {
        SkinTexture texture;

        try {
            URLConnection connection = new URL(String.format(PROFILE_URL, uniqueId.toString().replace("-", ""))).openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(reader.readLine());

            JSONObject properties = (JSONObject) object.get("properties");

            String value = (String) properties.get("value");
            String signature = (String) properties.get("signature");

            texture = new SkinTexture(value, signature);
        } catch (Exception exception) {
            return new Lookup<>(null, exception);
        }

        return new Lookup<>(texture, null);
    }

    @Getter
    public static class Lookup<T> {

        private final T value;
        private final Exception exception;

        public Lookup(T value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        public boolean wasSuccessful() {
            return exception == null;
        }
    }

    @Getter
    public static class SkinTexture {

        private final String texture;
        private final String signature;

        public SkinTexture(String texture, String signature) {
            this.texture = texture;
            this.signature = signature;
        }
    }
}