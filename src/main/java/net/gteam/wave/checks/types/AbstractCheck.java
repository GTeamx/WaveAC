package net.gteam.wave.checks.types;

import net.gteam.wave.Wave;
import net.gteam.wave.api.events.WaveViolationEvent;
import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.annotations.Disabled;
import net.gteam.wave.checks.annotations.Experimental;
import net.gteam.wave.checks.enums.CheckCategory;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.files.Config;
import net.gteam.wave.files.commentedfiles.CommentedFileConfiguration;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.utils.BetterStream;
import net.gteam.wave.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractCheck {

    protected final Profile profile;

    private final boolean enabled;

    private final Set<String> commands = new LinkedHashSet<>();

    private final boolean enabledSetback;

    private final String checkCodeName;
    private final String checkType;
    private final String fullCheckName;
    private final String description;
    private final boolean development, disabled, experimental;
    private final CheckCategory checkCategory;
    private final CheckType check;
    private int vl = 1;
    private final int maxVl;
    private float buffer;
    private String verbose = "";

    public AbstractCheck(final Profile profile, final CheckType check, final String type, final String description) {

        this.profile = profile;
        this.check = check;
        this.checkCodeName = check.getCheckCodeName();
        this.checkType = type;
        this.description = description;

        final CommentedFileConfiguration config = Wave.getInstance().getChecks();
        final String codeName = this.checkCodeName.toLowerCase();
        final String checkType = type.toLowerCase().replace(" ", "_");

        this.enabledSetback = !Config.Setting.GHOST_MODE.getBoolean()
                && (check == CheckType.SPEED || check == CheckType.FLY || check == CheckType.MOTION);

        this.enabled = type.isEmpty()
                ? config.getBoolean(codeName + ".enabled")
                : config.getBoolean(codeName + "." + checkType + ".enabled", config.getBoolean(codeName + "." + checkType + ".enabled"));

        this.maxVl = config.getInt(codeName + "." + checkType + ".max_vl");

        if (profile != null) {
            this.commands.addAll(
                    BetterStream.applyAndGet(config.getStringList(codeName + "." + checkType + ".commands"),
                            command -> command.replace("%player%", profile.getPlayer().getName())
                    )
            );
        }

        final Class<? extends AbstractCheck> clazz = this.getClass();

        this.development = clazz.isAnnotationPresent(Development.class);

        this.disabled = clazz.isAnnotationPresent(Disabled.class);

        this.experimental = clazz.isAnnotationPresent(Experimental.class);

        this.checkCategory = check.getCheckCategory();

        this.fullCheckName = check.getCheckDisplayName() + (type.isEmpty() ? "" : (" (" + type + ")"));
    }

    public String getVerbose() {
        return verbose;
    }

    public String getFullCheckName() {
        return fullCheckName;
    }

    protected void debug(final Object info) {
        Bukkit.broadcastMessage(String.valueOf(info));
    }

    public void fail(final String verbose) {

        this.verbose = verbose;

        fail();
    }

    public void fail() {

        //Development
        if (this.disabled) return;

        //Just to make sure
        if (this.vl < 0) this.vl = 1;

        final Player player = profile.getPlayer();

        if (player == null) return;

        final WaveViolationEvent violationEvent = new WaveViolationEvent(
                player,
                this.check,
                this.description,
                this.checkType,
                verbose,
                //Increase the violations here
                this.vl++,
                this.maxVl,
                this.experimental);

        Bukkit.getPluginManager().callEvent(violationEvent);

        if (violationEvent.isCancelled()) {

            this.vl--;

            return;
        }

        if (this.enabledSetback) profile.getMovementData().getSetbackProcessor().setback(true);

        if (this.vl > this.maxVl && !development) {

            MiscUtils.consoleCommand(this.commands);

            this.vl = 0;
            this.buffer = 0;
        }
    }

    public CheckCategory getCategory() {
        return checkCategory;
    }

    public void resetVl() {
        this.vl = 1;
    }

    public int getVl() {
        return this.vl;
    }

    public void setVl(final int vl) {
        this.vl = vl;
    }

    protected float increaseBuffer(int maxBuffer) {
        return this.buffer = Math.min(maxBuffer, this.buffer + 1);
    }

    protected float increaseBufferBy(final double amount) {
        return this.buffer += (float) amount;
    }

    protected float decreaseBuffer() {
        return this.buffer == 0 ? 0 : (this.buffer = Math.max(0, this.buffer - 1));
    }

    protected float decreaseBufferBy(final double amount) {
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

    public String getCheckCodeName() {
        return this.checkCodeName;
    }

    public String getCheckType() {
        return this.checkType;
    }

    public String getDescription() {
        return this.description;
    }
}