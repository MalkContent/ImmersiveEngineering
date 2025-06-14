/*
 * BluSunrize
 * Copyright (c) 2025
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.api.tool;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public class NoisyToolItemStack implements INoisyTool
{
	NoisyToolItemStack

	@Override
	public Holder<SoundEvent> getIdleSound(ItemStack stack)
	{
		return null;
	}

	@Override
	public Holder<SoundEvent> getBusySound(ItemStack stack)
	{
		return null;
	}

	@Override
	public Holder<SoundEvent> getFadingSound(ItemStack stack)
	{
		return null;
	}

	@Override
	public Holder<SoundEvent> getAttackSound(ItemStack stack)
	{
		return null;
	}

	@Override
	public Holder<SoundEvent> getHarvestSound(ItemStack stack)
	{
		return null;
	}

	@Override
	public boolean ableToMakeNoise(ItemStack stack)
	{
		return false;
	}
}
