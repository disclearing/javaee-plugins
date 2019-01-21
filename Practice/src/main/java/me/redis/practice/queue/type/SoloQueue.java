package me.redis.practice.queue.type;

import lombok.Getter;
import lombok.Setter;

import me.redis.practice.Practice;
import me.redis.practice.arena.Arena;
import me.redis.practice.enums.ProfileStatus;
import me.redis.practice.ladders.Ladder;
import me.redis.practice.match.type.SoloMatch;
import me.redis.practice.profile.Profile;
import me.redis.practice.queue.IQueue;
import me.redis.practice.queue.QueueData;
import me.redis.practice.utils.HiddenStringUtil;
import me.redis.practice.utils.ItemBuilder;
import me.redis.practice.utils.PracticeUtils;
import me.redis.practice.utils.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

public class SoloQueue implements IQueue {

    @Getter private UUID identifier;
    @Getter private Ladder ladder;

    @Getter private boolean ranked;

    @Getter @Setter private int playingAmount = 0;

    @Getter private LinkedList<QueueData> searchList;
    @Getter private BukkitTask queueTask;
    
    public SoloQueue(Ladder ladder, Boolean isRanked) {
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.ranked = isRanked;
        this.searchList = new LinkedList<>();
        this.startTask();
    }

    @Override
    public String getName() {
        return this.ladder.getName() + " 1v1";
    }

    @Override
    public int getQueueingAmount() {
        return this.searchList.size();
    }

    @Override
    public void addToQueue(Object object) {
        Player player = (Player) object;
        Profile profile = Practice.getPlugin().getProfileManager().getProfile(player.getUniqueId());
        int rating = profile.getElo().get(ladder.getName());
        QueueData participant = new QueueData(player.getUniqueId(), this, rating);

        this.searchList.offer(participant);

        profile.setCurrentQueue(this);
        profile.setQueueData(participant);
        profile.setStatus(ProfileStatus.QUEUE);

        player.sendMessage(ChatColor.YELLOW + "Added to the " + ChatColor.GREEN + ladder.getName() + ChatColor.YELLOW  + " queue, please wait for another player.");

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @Override
    public void removeFromQueue(Object object) {
        this.removeFromQueue(object, true, true, false);
    }

    private void removeFromQueue(Object object, boolean send, boolean clean, boolean teleport) {
        this.searchList.remove(object);

        QueueData participant = (QueueData) object;
        Profile profile = Practice.getPlugin().getProfileManager().getProfile((UUID) participant.getObject());

        profile.setStatus(ProfileStatus.LOBBY);
        profile.setCurrentQueue(null);
        profile.setQueueData(null);

        if (clean) {
            PracticeUtils.resetPlayer(profile.getPlayer());
            profile.getPlayer().getInventory().setContents(PracticeUtils.getLobbyInventory());
            profile.getPlayer().updateInventory();
        }

        if (teleport) {
            profile.getPlayer().teleport(Bukkit.getWorld(Practice.getPlugin().getConfig().getString("WORLD.NAME")).getSpawnLocation());
        }

        if (send) {
            profile.getPlayer().sendMessage(ChatColor.YELLOW + "You have left the queue.");
        }
    }
    
    private boolean inRange(QueueData data1, QueueData data2) {
        return (data1.getRating() >= data2.getMinRange() && data1.getRating() <= data2.getMaxRange()) || (data2.getRating() >= data1.getMinRange() && data2.getRating() <= data1.getMaxRange());
    }
    
    private void incrementRange(QueueData data) {
        data.incrementRange();
        Bukkit.getPlayer((UUID)data.getObject()).sendMessage(ChatColor.YELLOW + "Searching in elo range " + ChatColor.GREEN + "[" + data.getMinRange() + " -> " + data.getMaxRange() + "]");
    }
    
    private void createMatch(QueueData data1, QueueData data2) {
        Arena arena = Practice.getPlugin().getArenaManager().getRandomArena();
        Arena sumo = Practice.getPlugin().getArenaManager().getRandomSumoArena();
        Arena spleef = Practice.getPlugin().getArenaManager().getRandomSpleefArena();

        this.removeFromQueue(data1, false, true, false);
        this.removeFromQueue(data2, false, true, false);

        Player player1 = Bukkit.getPlayer((UUID) data1.getObject());
        Player player2 = Bukkit.getPlayer((UUID) data2.getObject());

        if (arena == null) {
            player1.sendMessage(ChatColor.RED + "There are no available arenas, you have been removed from the queue.");
            player2.sendMessage(ChatColor.RED + "There are no available arenas, you have been removed from the queue.");
            return;
        }

        if (ladder.getName().toLowerCase().equalsIgnoreCase("sumo")) {
            SoloMatch match = new SoloMatch(this, this.ladder, sumo, this.ranked, player1, player2);
            Practice.getPlugin().getMatchManager().getMatches().put(match.getUniqueId(), match);
        } else if (ladder.getName().toLowerCase().equalsIgnoreCase("spleef")) {
            SoloMatch match = new SoloMatch(this, this.ladder, spleef, this.ranked, player1, player2);
            Practice.getPlugin().getMatchManager().getMatches().put(match.getUniqueId(), match);
        } else {
            SoloMatch match = new SoloMatch(this, this.ladder, arena, this.ranked, player1, player2);
            Practice.getPlugin().getMatchManager().getMatches().put(match.getUniqueId(), match);
        }

        this.playingAmount = this.playingAmount + 2;
    }
    
    private void startTask() {
        this.queueTask = new BukkitRunnable() {
            int i = 0;
            
            public void run() {
                if (playingAmount < 0) {
                    playingAmount = 0;
                }

                Iterator<QueueData> iterator = SoloQueue.this.searchList.iterator();

                while (iterator.hasNext()) {
                    QueueData search = iterator.next();

                    if (i == 100 && ranked) {
                        incrementRange(search);
                    }

                    if (!iterator.hasNext()) {
                        continue;
                    }

                    QueueData found = iterator.next();

                    if (ranked) {
                        if (inRange(search, found)) {
                            createMatch(search, found);
                        }
                    }
                    else {
                        createMatch(search, found);
                    }
                }

                if (this.i >= 100) {
                    this.i = 0;
                }
                else {
                    this.i = this.i + 2;
                }
            }
        }.runTaskTimer(Practice.getPlugin(), 0L, 2L);
    }

    public ItemStack getQueueToInventory() {
        return new ItemBuilder(SerializationUtils.itemStackFromString(ladder.getIcon())).setDisplayName("&a" + ladder.getName()).setLore(HiddenStringUtil.encodeString(getIdentifier().toString()), "&7&m------------------", "&e In Queue&7: &f" + getQueueingAmount(), "&e In Match&7: &f" + getPlayingAmount(), "&7&m------------------").create();
    }
}