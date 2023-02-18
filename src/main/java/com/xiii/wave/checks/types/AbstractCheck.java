package com.xiii.wave.checks.types;

import com.xiii.wave.Wave;
import com.xiii.wave.api.events.WaveViolationEvent;
import com.xiii.wave.checks.annotations.Development;
import com.xiii.wave.checks.annotations.Experimental;
import com.xiii.wave.checks.annotations.Testing;
import com.xiii.wave.checks.enums.CheckCategory;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.files.Config;
import com.xiii.wave.files.commentedfiles.CommentedFileConfiguration;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.utils.BetterStream;
import com.xiii.wave.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractCheck {

    protected final Profile profile;

    private final boolean enabled;

    private final Set<String> commands = new LinkedHashSet<>();

    private final boolean enabledSetback;

    private final String checkName, checkType, fullCheckName, description;
    private final boolean experimental;
    private final boolean testing;
    private final CheckCategory checkCategory;
    private final boolean development;
    private int vl;
    private final int maxVl;
    private float buffer;
    private String verbose; //TODO: USE STRINGBUILDER

    public AbstractCheck(Profile profile, CheckType check, String type, String description) {

        this.profile = profile;
        this.checkName = check.getCheckName();
        this.checkType = type;
        this.description = description;

        final CommentedFileConfiguration config = Wave.getInstance().getChecks();
        final String checkName = this.checkName.toLowerCase();
        final String checkType = type.toLowerCase().replace(" ", "_");

        this.testing = this.getClass().isAnnotationPresent(Testing.class);

        this.enabledSetback = !Config.Setting.SILENT_MODE.getBoolean()
                && (check == CheckType.SPEED || check == CheckType.FLY || check == CheckType.MOTION);

        this.enabled = type.isEmpty()
                ? config.getBoolean(checkName + ".enabled")
                : config.getBoolean(checkName + "." + checkType + ".enabled", config.getBoolean(checkName + "." + checkType));

        this.maxVl = config.getInt(checkName + ".max_vl");

        /*
        This is null inside GUI's
         */
        if (profile != null && !this.testing) {
            this.commands.addAll(
                    BetterStream.applyAndGet(config.getStringList(checkName + ".commands"),
                            command -> command.replace("%player%", profile.getPlayer().getName())
                    )
            );

        }

        Class<? extends AbstractCheck> clazz = this.getClass();

        this.experimental = clazz.isAnnotationPresent(Experimental.class);

        this.development = clazz.isAnnotationPresent(Development.class);

        this.checkCategory = check.getCheckCategory();

        this.fullCheckName = this.checkName + (type.isEmpty() ? "" : (" (" + type + ")"));
    }

    public String getVerbose() {
        return verbose;
    }

    public String getFullCheckName() {
        return fullCheckName;
    }

    protected void debug(Object info) {
        Bukkit.broadcastMessage(String.valueOf(info));
    }

    public void fail(String verbose) {

        this.verbose = verbose;

        fail();
    }

    public void fail() {

        //Development
        if (this.development) return;

        //Just to make sure
        if (this.vl < 0) this.vl = 0;

        final Player p = profile.getPlayer();

        if (p == null) return;

        WaveViolationEvent violationEvent = new WaveViolationEvent(
                p,
                this.checkName,
                this.description,
                this.checkType,
                verbose,
                //Increase the violations here
                this.vl++,
                this.maxVl,
                this.experimental);

        Bukkit.getPluginManager().callEvent(violationEvent);

        if (violationEvent.isCancelled() || this.experimental) {

            this.vl--;

            return;
        }

        if (this.enabledSetback) profile.getMovementData().getSetbackProcessor().setback(true);

        if (this.vl >= this.maxVl) {

            MiscUtils.consoleCommand(this.commands);

            this.vl = 0;
            this.buffer = 0;
        }
    }

    public CheckCategory getCategory() {
        return checkCategory;
    }

    public void resetVl() {
        this.vl = 0;
    }

    public int getVl() {
        return this.vl;
    }

    public void setVl(int vl) {
        this.vl = vl;
    }

    protected float increaseBuffer() {
        return this.buffer++;
    }

    protected float increaseBufferBy(double amount) {
        return this.buffer += amount;
    }

    protected float decreaseBuffer() {
        return this.buffer == 0 ? 0 : (this.buffer = Math.max(0, this.buffer - 1));
    }

    protected float decreaseBufferBy(double amount) {
        return this.buffer == 0 ? 0 : (this.buffer = (float) Math.max(0, this.buffer - amount));
    }

    public void resetBuffer() {
        this.buffer = 0;
    }

    protected float getBuffer() {
        return this.buffer;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getCheckName() {
        return this.checkName;
    }

    public String getCheckType() {
        return this.checkType;
    }

    public String getDescription() {
        return this.description;
    }
}
