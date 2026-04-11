package com.soc.game.manager.bedwars.traps;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.game.manager.AbstractGameManager;
import com.soc.items.BaseWeapon;
import com.soc.items.EatFunctionFood;
import com.soc.lib.Events;
import com.soc.networking.s2c.JumpscarePayload;
import com.soc.networking.s2c.SilencePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.resetScale;
import static com.soc.lib.SocWarsLib.scaleEntity;
import static com.soc.game.manager.bedwars.traps.Traps.register;

public class SimpleTriggerTrap extends AbstractTrap {
    public static void initialise() {}

    public static final AbstractTrap MINING_FATIGUE = register(new SimpleTriggerTrap("mining_fatigue", Items.TRIPWIRE_HOOK.getDefaultStack(), 12 * 20, StatusEffects.MINING_FATIGUE, 1));
    public static final AbstractTrap CLEAR_EFFECTS = register(new SimpleTriggerTrap("clear_effects", Items.MILK_BUCKET.getDefaultStack(), 5 * 20, ServerPlayerEntity::clearStatusEffects));
    public static final AbstractTrap LAUNCH = register(new SimpleTriggerTrap("launch", Items.PISTON.getDefaultStack(), 5 * 20, enemy -> enemy.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(enemy.getId(), new Vec3d(0d, 2d, 0d)))));
    public static final AbstractTrap STEVIE_WONDER = register(new SimpleTriggerTrap("steve_harvey", Items.SCULK_VEIN.getDefaultStack(), 8 * 20, (enemy, context) -> {
        enemy.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, context.getCooldownTime(), 1, false, true, true));
        enemy.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, context.getCooldownTime(), 1, false, true, true));
    }));
    public static final AbstractTrap SHUFFLE = register(new SimpleTriggerTrap("shuffle", Items.STRING.getDefaultStack(), 5 * 20, player -> {
        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> items = new ArrayList<>(inventory.getMainStacks());
        Collections.shuffle(items);
        for (int i = 0; !items.isEmpty(); i++) {
            inventory.setStack(i, items.removeFirst());
        }
    }));
    public static final AbstractTrap PERPLEXITY = register(new SimpleTriggerTrap("perplexity", Items.DIAMOND_HOE.getDefaultStack(), 6 * 20, ModEffects.PERPLEXITY, 0));
    public static final AbstractTrap ENLARGEMENT = register(new SimpleTriggerTrap("enlargement", EatFunctionFood.BIGGENING_PILLS.getDefaultStack(), 10 * 20, (enemy, context) -> {
        scaleEntity(enemy, 2f);
        Events.getInstance().scheduleEvent(() -> resetScale(enemy), context.getCooldownTime());
    }));
    public static final AbstractTrap GLOWING = register(new SimpleTriggerTrap("glowing", Items.TORCH.getDefaultStack(), 8 * 20, StatusEffects.GLOWING, 0));
    public static final AbstractTrap POSTURA = register(new SimpleTriggerTrap("postura", BaseWeapon.BAT.getDefaultStack(), 10 * 20, ModEffects.ARTHRODESIS, 0));
    public static final AbstractTrap SPEED = register(new SimpleTriggerTrap("speed", Items.BLUE_ICE.getDefaultStack(), 2 * 20, StatusEffects.SPEED, 9));
    public static final AbstractTrap JUMPSCARE = register(new SimpleTriggerTrap("jumpscare", Items.CREEPER_HEAD.getDefaultStack(), 5 * 20, enemy -> ServerPlayNetworking.send(enemy, new JumpscarePayload(SoundEvents.ENTITY_ENDERMAN_DEATH, Identifier.of(SocWars.MOD_ID, ""))))); //TODO: Choose an Id or maybe make the field optional or something
    public static final AbstractTrap SILENCE = register(new SimpleTriggerTrap("silence", Items.WHITE_WOOL.getDefaultStack(), 10 * 20, enemy -> ServerPlayNetworking.send(enemy, new SilencePayload(10 * 20))));
    public static final AbstractTrap LIGHTWEIGHT = register(new SimpleTriggerTrap("lightweight", Items.FEATHER.getDefaultStack(), 8 * 20, ModEffects.LIGHTWEIGHT, 4));

    private final BiConsumer<ServerPlayerEntity, AbstractTrap> enemyTriggerFunction;

    public SimpleTriggerTrap(String id, ItemStack icon, int time, Consumer<ServerPlayerEntity> enemyTriggerFunction) {
        super(id, icon, time);
        this.enemyTriggerFunction = (enemy, context) -> enemyTriggerFunction.accept(enemy);
    }

    public SimpleTriggerTrap(String id, ItemStack icon, int time, BiConsumer<ServerPlayerEntity, AbstractTrap> enemyTriggerFunction) {
        super(id, icon, time);
        this.enemyTriggerFunction = enemyTriggerFunction;
    }

    public SimpleTriggerTrap(String id, ItemStack icon, int duration, RegistryEntry<StatusEffect> effect, int amplifier) {
        super(id, icon, duration);
        this.enemyTriggerFunction = (enemy, context) -> enemy.addStatusEffect(new StatusEffectInstance(effect, this.getCooldownTime(), amplifier, false, false));
    }

    @Override
    public void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemies, DyeColor team) {
        enemies.forEach(enemy -> this.enemyTriggerFunction.accept(enemy, this));
    }
}
