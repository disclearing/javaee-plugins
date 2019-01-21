package me.javaee.meetup.timer;

/**
 * Represents a {@link Timer}, used to manage cooldowns.
 */
public abstract class Timer {

    protected final String name;
    protected final long defaultCooldown;

    /**
     * Constructs a new {@link Timer} with a given name.
     *
     * @param name
     *            the name
     * @param defaultCooldown
     *            the default cooldown in milliseconds
     */
    public Timer(String name, long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }

    /**
     * Gets the prefix this {@link Timer} will display on a scoreboard.
     *
     * @return the scoreboard prefix
     */
    public abstract String getScoreboardPrefix();

    /**
     * Gets the name of this {@link Timer}.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the display name of this {@link Timer}.
     *
     * @return the display name
     */
    public final String getDisplayName() {
        return getScoreboardPrefix() + name;
    }
}