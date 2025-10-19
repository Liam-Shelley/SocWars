package com.soc.game.manager;

import com.soc.SocWars;
import com.soc.game.map.SpreadRules;
import com.soc.lib.SocWarsLib;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GamesManager {
    private static final GamesManager INSTANCE = new GamesManager();

    public static final float QUEUE_PROGRESS_THRESHOLD = 1f;
    public static final int QUEUE_CHECK_INTERVAL = 20;

    private ServerWorld world;

    private final ArrayList<AbstractGameManager> games = new ArrayList<>();
    private final ConcurrentHashMap<ServerPlayerEntity, Integer> playerGameLookup = new ConcurrentHashMap<>();

    private final MatchmakingQueue<GameType> queue = new MatchmakingQueue<>();
    private final HashMap<GameType, Float> queueProgress = new HashMap<>();

    private GamesManager() {
        Arrays.stream(GameType.values()).forEach(queue -> this.queueProgress.put(queue, 0f));

        this.initialiseEvents();
    }

    public static void initialise() {}

    public static GamesManager getInstance() {
        return INSTANCE;
    }

    public void initialiseEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.world = server.getOverworld());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.games.forEach(AbstractGameManager::endGame));
        ServerTickEvents.START_SERVER_TICK.register(this::tick);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            return this.getGame(entity).map(game -> game.onPlayerDeath((ServerPlayerEntity) entity, source, amount)).orElse(true);
        });
    }

    public boolean startGame(AbstractGameManager game) {
        if (game == null) return false;

        if (this.games.size() > game.getGameId()) {
            this.games.set(game.getGameId(), game);
        } else {
            this.games.add(game);
        }

        this.queue.unqueuePlayers(game.getPlayers());

        game.startGame();
        game.getPlayers().forEach(player -> this.playerGameLookup.put(player, game.getGameId())); //Bit of gross bookkeeping

        return true;
    }

    public void endGame(int gameId) {
        this.playerGameLookup.forEach((player, id) -> {
            if (id == gameId) this.playerGameLookup.remove(player);
        }); //Tail end of the gross bookkeeping
        this.games.set(gameId, null);
    }

    public Optional<AbstractGameManager> getGame(LivingEntity entity) {
        final Integer id = this.playerGameLookup.get(entity); //Hilarious abuse of Map#Get
        return Optional.ofNullable(id).map(this.games::get);
    }

    private int getNewGameId() {
        final Iterator<? extends AbstractGameManager> games = this.games.iterator();

        int i = 0;
        while (games.hasNext() && games.next() != null) {
            i++;
        }

        return games.hasNext() ? --i : i;
    }

    public void tick(MinecraftServer server) {
        this.games.forEach(game -> {
            if (game != null) game.tick();
        });

        if (this.world.getTime() % QUEUE_CHECK_INTERVAL == 0) { //Only update queues once per second
            this.checkQueues();
        }
    }

    private void checkQueues() {
        this.queueProgress.keySet().forEach(queueType -> {
            final float currentProgress = this.queueProgress.get(queueType);
            final float queueProgressDelta = this.queue.getQueueProgress(queueType) * QUEUE_CHECK_INTERVAL;

            final Set<ServerPlayerEntity> players = this.queue.getPlayersInQueue(queueType).stream().limit(queueType.maxPlayers()).collect(Collectors.toSet()); //Cap the number of players to send into a game to the queueType's max player count

            this.queueProgress.put(queueType, queueProgressDelta < Float.MIN_NORMAL || players.size() < queueType.minPlayers() ? 0f : currentProgress + queueProgressDelta); //Update the queueType progress of every queueType

            final int remainingTime = (int)((QUEUE_PROGRESS_THRESHOLD - currentProgress) / queueProgressDelta);
            players.forEach(player -> player.sendMessage(remainingTime > 1000f ? Text.translatable("hud.queue_not_starting", players.size(), queueType.minPlayers()) : Text.translatable("hud.queue_time_remaining", SocWarsLib.getTimeFromTicks(remainingTime, false)), true));

            if (currentProgress >= QUEUE_PROGRESS_THRESHOLD) {
                this.finishQueue(queueType, players);
            }
        });
    }

    private void finishQueue(GameType queue, Set<ServerPlayerEntity> players) {
        this.queueProgress.put(queue, 0f); //Reset the queueType progress

        final int gameId = this.getNewGameId();

        final AbstractGameManager game = switch (queue) {
            case SKYWARS -> new SkywarsGameManager(this.world, players, null, gameId, 5);
            case BEDWARS -> new BedwarsGameManager(this.world, players, new SpreadRules(4), gameId);
            case PROP_HUNT -> null; //Maybe get around to writing some of the game logic for prop hunt
        };

        final boolean startedGame = startGame(game);
        if (!startedGame) SocWars.LOGGER.warn("Failed to start game {}", game.getGameId());
    }

    public void queuePlayer(ServerPlayerEntity player, GameType queue) {
        this.queue.queuePlayer(player, queue);
    }

    public void unqueuePlayer(ServerPlayerEntity player, GameType queue) {
        if (!this.queue.isPlayerInQueue(player, queue)) this.queue.unqueuePlayer(player);
    }
}
