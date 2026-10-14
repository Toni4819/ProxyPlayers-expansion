package fr.Toni4819;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Taskable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyPlayersExpansion extends PlaceholderExpansion implements Taskable, PluginMessageListener {

    private static final String CHANNEL = "BungeeCord";

    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private final Set<String> watched = ConcurrentHashMap.newKeySet();
    private final AtomicInteger total = new AtomicInteger();
    private BukkitTask task;

    @Override public @NotNull String getIdentifier() { return "proxyplayers"; }
    @Override public @NotNull String getAuthor() { return "toi"; }
    @Override public @NotNull String getVersion() { return "1.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public void start() {
        PlaceholderAPIPlugin papi = PlaceholderAPIPlugin.getInstance();
        Bukkit.getMessenger().registerOutgoingPluginChannel(papi, CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(papi, CHANNEL, this);
        task = Bukkit.getScheduler().runTaskTimer(papi, this::refresh, 40L, 40L);
    }

    @Override
    public void stop() {
        PlaceholderAPIPlugin papi = PlaceholderAPIPlugin.getInstance();
        if (task != null) task.cancel();
        Bukkit.getMessenger().unregisterIncomingPluginChannel(papi, CHANNEL, this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(papi, CHANNEL);
        counts.clear();
        watched.clear();
    }

    private void refresh() {
        Player carrier = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (carrier == null) return;
        send(carrier, "ALL");
        for (String server : watched) send(carrier, server);
    }

    private void send(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server);
        p.sendPluginMessage(PlaceholderAPIPlugin.getInstance(), CHANNEL, out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] msg) {
        if (!channel.equals(CHANNEL)) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(msg);
        if (!in.readUTF().equals("PlayerCount")) return;
        String server = in.readUTF();
        int count = in.readInt();
        if (server.equals("ALL")) total.set(count);
        else counts.put(server.toLowerCase(), count);
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("total")) return String.valueOf(total.get());

        if (params.regionMatches(true, 0, "server:", 0, 8)) {
            int sum = 0;
            for (String s : params.substring(8).split(",")) {
                String name = s.trim();
                if (name.isEmpty()) continue;
                watched.add(name);
                sum += counts.getOrDefault(name.toLowerCase(), 0);
            }
            return String.valueOf(sum);
        }
        return null;
    }
}