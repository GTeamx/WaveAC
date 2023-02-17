package com.xiii.wave.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.xiii.wave.Wave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public final class ChatUtils {

    private final Wave plugin;
    private static MiniMessage mm;

    public ChatUtils(Wave plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
    }

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");

    public static String format(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String stripColorCodes(final String input) {
        return ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(input).replaceAll(""));
    }

    public static Component deserialize(final String message) {
        MiniMessage minimessage = MiniMessage.builder()
                .strict(true)
                .tags(TagResolver.builder()
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .resolver(TagResolver.standard())
                        .build()
                )
                .build();
        return minimessage.deserialize(message);
    }

    public static void sendMessage(final ChatMessage message, Collection<? extends UUID> players) {

        WrapperPlayServerChatMessage chat = new WrapperPlayServerChatMessage(message);

        for (UUID uuid : players) {

            Player p = Bukkit.getPlayer(uuid);

            if (p == null) continue;

            PacketEvents.getAPI().getProtocolManager().sendPackets(p, chat);
        }
    }

    public static void log(final Level level, String message) {
        Wave.getInstance().getLogger().log(level, message);
    }
}
