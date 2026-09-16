package net.industrialgrid.breaker;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.patryk3211.powergrid.utility.Unit;

/** Discrete-step sibling of PowerGrid's HvBreakerScrollBehaviour: instead of a free 0-100 A
 * scroll range, the value is an index into IndustrialBreakerBlockEntity.SETTINGS (0 = Off). */
public class IndustrialBreakerScrollBehaviour extends ScrollValueBehaviour {
    public IndustrialBreakerScrollBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
        super(label, be, slot);
        withFormatter(i -> i == 0 ? "Off" : IndustrialBreakerBlockEntity.formatSetting(i));
        between(0, IndustrialBreakerBlockEntity.SETTINGS.length);
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(label, max, 1, ImmutableList.of(Unit.CURRENT.get().component()),
                new ValueSettingsFormatter(ValueSettingsBehaviour.ValueSettings::format));
    }
}
