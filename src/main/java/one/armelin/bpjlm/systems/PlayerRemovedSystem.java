package one.armelin.bpjlm.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import one.armelin.bpjlm.BPJLM;
import one.armelin.bpjlm.utils.MessageFormatter;
import one.armelin.bpjlm.utils.TinyMsg;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.logging.Level;

public class PlayerRemovedSystem extends HolderSystem<EntityStore> {
    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> var1, @NonNullDecl AddReason var2, @NonNullDecl Store<EntityStore> var3) {

    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason reason, @NonNullDecl Store<EntityStore> store)  {
        World world = store.getExternalData().getWorld();
        PlayerRef playerRefComponent = holder.getComponent(PlayerRef.getComponentType());

        assert playerRefComponent != null;

        Player playerComponent = holder.getComponent(Player.getComponentType());

        assert playerComponent != null;

        TransformComponent transformComponent = holder.getComponent(TransformComponent.getComponentType());

        assert transformComponent != null;

        HeadRotation headRotationComponent = holder.getComponent(HeadRotation.getComponentType());

        assert headRotationComponent != null;

        DisplayNameComponent displayNameComponent = holder.getComponent(DisplayNameComponent.getComponentType());

        assert displayNameComponent != null;

        Message displayName = displayNameComponent.getDisplayName();
        PlayerSystems.LOGGER
                .at(Level.INFO)
                .log(
                        "Removing player '%s%s' from world '%s' (%s)",
                        playerRefComponent.getUsername(),
                        displayName != null ? " (" + displayName.getAnsiMessage() + ")" : "",
                        world.getName(),
                        playerRefComponent.getUuid()
                );
        playerComponent.getPlayerConfigData()
                .getPerWorldData(world.getName())
                .setLastPosition(new Transform(transformComponent.getPosition().clone(), headRotationComponent.getRotation().clone()));
        playerRefComponent.getPacketHandler().setQueuePackets(false);
        playerRefComponent.getPacketHandler().tryFlush();

        String playerName = playerRefComponent.getUsername();
        String worldName = world.getWorldConfig().getDisplayName() != null ? world.getWorldConfig().getDisplayName() : WorldConfig.formatDisplayName(world.getName());

        BPJLM.LOGGER.atInfo().log("Player %s left the world %s", playerName, worldName);

        if(BPJLM.config.texts.leftWorld.isEmpty()) return;
        Message message = TinyMsg.parse(MessageFormatter.format(BPJLM.config.texts.leftWorld, "playername", playerName, "world", worldName));
        PlayerUtil.broadcastMessageToPlayers(playerRefComponent.getUuid(), message, store);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                PlayerRef.getComponentType(),
                Player.getComponentType(),
                TransformComponent.getComponentType(),
                HeadRotation.getComponentType(),
                DisplayNameComponent.getComponentType()
        );
    }
}
