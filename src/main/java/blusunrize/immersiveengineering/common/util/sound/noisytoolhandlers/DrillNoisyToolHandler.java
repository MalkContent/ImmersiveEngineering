/*
 * BluSunrize
 * Copyright (c) 2025
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.util.sound.noisytoolhandlers;

import blusunrize.immersiveengineering.api.tool.NoisyToolItemStack;
import blusunrize.immersiveengineering.common.items.DrillItem;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.IESounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public class DrillNoisyToolHandler extends NoisyToolItemStack
{
	public DrillNoisyToolHandler(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public Holder<SoundEvent> getIdleSound()
	{
		return IESounds.drill_idle;
	}

	@Override
	public Holder<SoundEvent> getBusySound()
	{
		return IESounds.drill_busy;
	}

	@Override
	public Holder<SoundEvent> getFadingSound()
	{
		return IESounds.drill_fade;
	}


	@Override
	public Holder<SoundEvent> getAttackSound()
	{
		return IESounds.drill_attack;
	}

	@Override
	public Holder<SoundEvent> getHarvestSound()
	{
		return IESounds.drill_harvest;
	}

	@Override
	public boolean ableToMakeNoise()
	{
		return Tools.DRILL.get().canToolBeUsed(stack);
	}

	@Override
	public boolean noisySameStack(ItemStack otherStack)
	{
		return otherStack.getItem() instanceof DrillItem;
	}
}
