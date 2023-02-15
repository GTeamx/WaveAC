package com.xiii.wave.files;

import com.xiii.wave.Wave;
import com.xiii.wave.files.commentedfiles.CommentedFileConfiguration;
import com.xiii.wave.managers.Initializer;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config implements Initializer {

    private static final String[] HEADER = new String[]{
            "#This is the configuration file for Wave b0001 (config.yml)",
            "#To reset this file, delete it from the Wave's folder and restart your server."
    };

    private final Wave plugin;
    private CommentedFileConfiguration configuration;
    private static boolean exists;

    public Config(Wave plugin) {
        this.plugin = plugin;
    }

    /**
     * @return the config.yml as a CommentedFileConfiguration
     */
    public CommentedFileConfiguration getConfig() {
        return this.configuration;
    }

    @Override
    public void initialize() {

        File configFile = new File(this.plugin.getDataFolder(), "config.yml");

        exists = configFile.exists();

        boolean setHeaderFooter = !configFile.exists();

        boolean changed = setHeaderFooter;

        this.configuration = CommentedFileConfiguration.loadConfiguration(this.plugin, configFile);

        if (setHeaderFooter) this.configuration.addComments(HEADER);

        for (Setting setting : Setting.values()) {

            setting.reset();

            changed |= setting.setIfNotExists(this.configuration);
        }

        if (changed) this.configuration.save();
    }

    @Override
    public void shutdown() {
        for (Setting setting : Setting.values()) setting.reset();
    }

    public enum Setting {

        CONFIG_VERSION("config-version", "b0001-config", "This won't have any visual impact, this is only for the plugin, changing it will only break things."),

        PREFIX("prefix", "§f[§b§lWave§f]", "This prefix will be used everywhere, including alerts, commands ect..."),

        // Bukkit.spigot().getConfig().getString("messages.unknown-command")
        MESSAGES("messages", "", "Messages used across Wave"),
        CONSOLE_ALERT("messages.console-alert", false, "Send alerts to console"),
        CONSOLE_COMMAND("message.console-command", "Sorry, this Wave's command can only be executed by a player.", "Message sent to console when trying to execute players only commands (of Wave) from console."),
        NO_PERMISSION("messages.no-permission", Bukkit.spigot().getConfig().getString("messages.unknown-command"), "By putting \"unknown-command\" this will use the unknown-command message from the spigot.yml configuration file."),
        ALERT_MESSAGE("message.alert-message", "&b%player% &7flagged &b%check% &7(§fx§b%vl%§7)", "Alert message that you will see whenever an alert is sent"),
        ALERT_HOVER("messages.alert-hover", "", "Hover message displayed when hovering an alert"),
        ALERT_HOVER_1("messages.alert-hover", "- '&7Description:&r'", ""),
        ALERT_HOVER_2("messages.alert-hover", "- '%description%'", ""),
        ALERT_HOVER_3("messages.alert-hover", "", ""),
        ALERT_HOVER_4("messages.alert-hover", "- '&7Information:&r'", ""),
        ALERT_HOVER_5("messages.alert-hover", "- '%information%'", ""),
        ALERT_HOVER_6("messages.alert-hover", "", ""),
        ALERT_HOVER_7("messages.alert-hover", "- '&7TPS: &r%tps%'", ""),
        ALERT_HOVER_8("messages.alert-hover", "", ""),
        ALERT_HOVER_9("messages.alert-hover", "- '&fClick to teleport'", ""),

        VPN_KEY("vpn-checker-key", "XXXXXX-XXXXXX-XXXXXX-XXXXXX", "You can put your proxycheck.io key here, if no key is inserted you will be limited to 100 requests/day.", "If you wish to disable the VPN checker you can simply put 'DISABLED'."),

        PERMISSIONS("permissions", "", "Permissions used across Wave"),
        PERMISSIONS_ALERTS("permissions.wave-alerts-command", "Wave.commands.alerts", ""),
        PERMISSIONS_ALERTS_BRAND("permissions.alerts-brand", "Wave.alerts.brand", ""),
        PERMISSIONS_MAIN("permissions.wave-main-command", "Wave.commands.main", ""),
        PERMISSIONS_VERSION("permissions.wave-version-command", "Wave.commands.version", ""),
        PERMISSIONS_BRAND("permissions.wave-commands-brand", "Wave.commands.brand", ""),
        PERMISSIONS_PLAYERVERSION("permissions.wave-commands-playerversion", "Wave.commands.playerversion", ""),
        PERMISSIONS_VPN("permissions.wave-vpn-command", "Wave.commands.vpn", ""),
        DISABLE_BYPASS_PERMISSION("permissions.disable-bypass-permission", true, "Should we disable the bypass permission?", "This permission allows player with it to be fully exempt from Wave."),
        PERMISSIONS_BYPASS("permissions.wave-bypass", "Wave.bypass", ""),
        PERMISSIONS_AUTO_ALERTS("permissions.wave-auto-alerts", "Wave.alerts.auto", ""),

        CHECKS("checks", "", "Checks configuration used across every checks"),
        CHECKS_VL_CLEAR_RATE("checks.violations-clear-rate", 5, "Every how many minutes should we clear all violations (in minutes)"),
        SILENT_MODE("checks.silent-mode", true, "Should we setback the player when they flag a movement check ?"),
        TOGGLE_ALERTS_ON_JOIN("checks.toggle-alerts-on-join", true, "Should we enable alerts for admins when they join?");

        private final String key;
        private final Object defaultValue;
        private boolean excluded;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        Setting(String key, Object defaultValue, boolean excluded, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
            this.excluded = excluded;
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();
            return (boolean) this.value;
        }

        public String getKey() {
            return this.key;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            this.loadValue();
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            this.loadValue();
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            this.loadValue();
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            this.loadValue();
            return String.valueOf(this.value);
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>) this.value;
        }

        private boolean setIfNotExists(CommentedFileConfiguration fileConfiguration) {
            this.loadValue();

            if (exists && this.excluded) return false;

            if (fileConfiguration.get(this.key) == null) {
                List<String> comments = Stream.of(this.comments).collect(Collectors.toList());
                if (this.defaultValue != null) {
                    fileConfiguration.set(this.key, this.defaultValue, comments.toArray(new String[0]));
                } else {
                    fileConfiguration.addComments(comments.toArray(new String[0]));
                }

                return true;
            }

            return false;
        }

        /**
         * Resets the cached value
         */
        public void reset() {
            this.value = null;
        }

        /**
         * @return true if this setting is only a section and doesn't contain an actual value
         */
        public boolean isSection() {
            return this.defaultValue == null;
        }

        /**
         * Loads the value from the config and caches it if it isn't set yet
         */
        private void loadValue() {
            if (this.value != null) return;
            this.value = Wave.getInstance().getConfiguration().get(this.key);
        }
    }
}