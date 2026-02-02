package one.armelin.bpjlm;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import one.armelin.bpjlm.systems.PlayerAddedSystem;
import one.armelin.bpjlm.systems.PlayerRemovedSystem;
import one.armelin.bpjlm.utils.MessageFormatter;
import one.armelin.bpjlm.utils.TinyMsg;

import java.awt.*;
import java.nio.file.Files;

public class BPJLM extends JavaPlugin {
    private static BPJLM instance;

    public static final String NAME = "BPJLM";
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static Configuration config;

    public BPJLM(JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("%s v%s initializing...", NAME, this.getManifest().getVersion());

        LOGGER.atInfo().log(String.valueOf(getDataDirectory()));

        if(!Files.exists(getDataDirectory())){
            try {
                Files.createDirectories(getDataDirectory());
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log("Failed to create plugin data directory");
            }
        }
        config = Configuration.getConfig(getDataDirectory());

        LOGGER.atInfo().log("%s initialized successfully!", NAME);
    }

    @Override
    protected void setup() {
        super.setup();
        getEntityStoreRegistry().registerSystem(new PlayerAddedSystem());
        EntityStore.REGISTRY.unregisterSystem(PlayerSystems.PlayerRemovedSystem.class);
        getEntityStoreRegistry().registerSystem(new PlayerRemovedSystem());
        getEventRegistry().registerGlobal(PlayerConnectEvent.class,(event -> {
            String playerName = event.getPlayerRef().getUsername();
            String worldName;
            if (event.getWorld() != null) {
                worldName = event.getWorld().getWorldConfig().getDisplayName() != null ? event.getWorld().getWorldConfig().getDisplayName() : WorldConfig.formatDisplayName(event.getWorld().getName());
            } else {
                worldName = "unknown";
            }
            LOGGER.atInfo().log("Player %s connected to world %s", playerName, worldName);
            if(config.texts.joinServer.isEmpty()) return;
            Message message = TinyMsg.parse(MessageFormatter.format(config.texts.joinServer, "playername", playerName, "world", worldName));
            Universe.get().sendMessage(message);
        }));
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class,(event -> {
            String playerName = event.getPlayerRef().getUsername();
            LOGGER.atInfo().log("Player %s disconnected", playerName);
            if (config.texts.leftServer.isEmpty()) return;
            Message message = TinyMsg.parse(MessageFormatter.format(config.texts.leftServer, "playername", playerName));
            Universe.get().sendMessage(message);
        }));
        getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class,(event -> {
            event.setBroadcastJoinMessage(false);
        }));
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

    public static BPJLM getInstance() {
        return instance;
    }
}
