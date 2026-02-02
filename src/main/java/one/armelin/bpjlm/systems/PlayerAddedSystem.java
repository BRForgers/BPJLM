package one.armelin.bpjlm.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.Message;
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

import java.util.Set;

public class PlayerAddedSystem extends RefSystem<EntityStore> {
    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason reason, @NonNullDecl Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        World world = commandBuffer.getExternalData().getWorld();
        PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if(playerRefComponent == null) return;
        String playerName = playerRefComponent.getUsername();
        String worldName = world.getWorldConfig().getDisplayName() != null ? world.getWorldConfig().getDisplayName() : WorldConfig.formatDisplayName(world.getName());
        BPJLM.LOGGER.atInfo().log("Player %s joined the world %s", playerName, worldName);

        if(BPJLM.config.texts.joinWorld.isEmpty()) return;
        Message message = TinyMsg.parse(MessageFormatter.format(BPJLM.config.texts.joinWorld, "playername", playerName, "world", worldName));
        PlayerUtil.broadcastMessageToPlayers(playerRefComponent.getUuid(), message, store);
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> var1, @NonNullDecl RemoveReason var2, @NonNullDecl Store<EntityStore> var3, @NonNullDecl CommandBuffer<EntityStore> var4) {
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, PlayerSystems.PlayerAddedSystem.class));
    }
}
