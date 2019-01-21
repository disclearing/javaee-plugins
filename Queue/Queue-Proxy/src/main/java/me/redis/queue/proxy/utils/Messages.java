package me.redis.queue.proxy.utils;

import me.redis.queue.proxy.QueueProxy;
import net.md_5.bungee.api.ChatColor;

public enum Messages {
    ALREADY_QUEUED {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.already_queued"));
        }
    },
    NOT_QUEUED {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.not_queued"));
        }
    },
    SERVER_NOT_VALID {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.server_not_valid"));
        }
    },
    SERVER_NOT_ONLINE {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.server_not_online"));
        }
    },
    POSITION {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.position"));
        }
    },
    SENT_TO_SERVER {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.sent_to_server"));
        }
    },
    COULD_NOT_LOCATE {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.could_not_locate"));
        }
    },
    LEFT_QUEUE {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.left_queue"));
        }
    },
    HAS_RANK {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.buy.has_rank"));
        }
    },
    NO_RANK {
        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', QueueProxy.getPlugin().getMessages().getString("messages.buy.no_rank"));
        }
    };
}
