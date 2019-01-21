/*
 * Copyright (c) 2015 Nate Mortensen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.javaee.uhc.utils;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonElement;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MojangNameFetcher implements Callable<Map<UUID, MojangNameFetcher.Lookup<MojangNameFetcher.NameHistory>>> {

    private static final String PROFILE_URL = "https://api.mojang.com/user/profiles/%s/names";

    private final JsonParser jsonParser = new JsonParser();
    private final List<UUID> uuids;

    public MojangNameFetcher(List<UUID> uuids) {
        this.uuids = ImmutableList.copyOf(uuids);
    }

    @Override
    public Map<UUID, Lookup<NameHistory>> call() throws Exception {
        Map<UUID, Lookup<NameHistory>> uuidNameHistory = new HashMap<>();
        for (UUID uuid : uuids) {
            try {
                URLConnection connection = new URL(String.format(PROFILE_URL, uuid.toString().replace("-", ""))).openConnection();
                JsonArray response = (JsonArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                NameHistory history = new NameHistory();

                Iterator<JsonElement> elements = response.iterator();

                while (elements.hasNext()) {
                    JsonObject object = elements.next().getAsJsonObject();
                    String name = object.get("name").getAsString();
                    Date changedToAt = object.has("changedToAt") ? new Date((long) object.get("changedToAt").getAsLong()) : null;
                    history.getHistory().add(new NameHistoryEntry(name, changedToAt));
                }
                history.sort();
                uuidNameHistory.put(uuid, new Lookup<>(history, null));
            } catch (Exception e) {
                uuidNameHistory.put(uuid, new Lookup<NameHistory>(null, e));
            }
        }
        return uuidNameHistory;
    }

    public static Callable<Lookup<NameHistory>> getNameHistory(final UUID uuid) {
        return new Callable<Lookup<NameHistory>>() {
            @Override
            public Lookup<NameHistory> call() throws Exception {
                return new MojangNameFetcher(Collections.singletonList(uuid)).call().get(uuid);
            }
        };
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
    @ToString
    public static class NameHistory {

        private final List<NameHistoryEntry> history = new ArrayList<>();

        public String getOriginalName() {
            return history.get(0).getName();
        }

        public String getCurrentName() {
            return history.get(history.size() - 1).getName();
        }

        private void sort() {
            Collections.sort(history, new Comparator<NameHistoryEntry>() {
                @Override
                public int compare(NameHistoryEntry o1, NameHistoryEntry o2) {
                    if (o1.getChangedAt() == null) {
                        return -1;
                    } else if (o2.getChangedAt() == null) {
                        return 1;
                    } else {
                        return o1.getChangedAt().compareTo(o2.getChangedAt());
                    }
                }
            });
        }
    }

    @Getter
    @ToString
    public static class NameHistoryEntry {

        private final String name;
        private final Date changedAt;

        public NameHistoryEntry(String name, Date changedAt) {
            this.name = name;
            this.changedAt = changedAt;
        }

        @Override
        public String toString() {
            return "NameHistoryEntry{" +
                    "name='" + name + '\'' +
                    ", changedAt=" + changedAt +
                    '}';
        }
    }
}