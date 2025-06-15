/*
 * BluSunrize
 * Copyright (c) 2025
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.util.sound.noisytoolhandlers;

import blusunrize.immersiveengineering.api.tool.NoisyToolItemStack;
import blusunrize.immersiveengineering.common.items.BuzzsawItem;
import blusunrize.immersiveengineering.common.items.GrindingDiskItem;
import blusunrize.immersiveengineering.common.items.RockcutterItem;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.IESounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BuzzsawNoisyToolHandler extends NoisyToolItemStack
{
	public BuzzsawNoisyToolHandler(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public Holder<SoundEvent> getIdleSound()
	{
		return IESounds.buzzsaw_idle;
	}

	@Override
	public Holder<SoundEvent> getBusySound()
	{
		return IESounds.buzzsaw_busy;
	}

	@Override
	public Holder<SoundEvent> getFadingSound()
	{
		return IESounds.buzzsaw_fade;
	}

	@Override
	public Holder<SoundEvent> getAttackSound()
	{
		return IESounds.buzzsaw_attack;
	}

	@Override
	public Holder<SoundEvent> getHarvestSound()
	{
		Item headitem = Tools.BUZZSAW.get().getHead(stack).getItem();
		if(headitem instanceof GrindingDiskItem||headitem instanceof RockcutterItem)
			return IESounds.buzzsaw_harvest_grind;
		return IESounds.buzzsaw_harvest_saw;
	}

	@Override
	public boolean ableToMakeNoise()
	{
		return Tools.BUZZSAW.get().canToolBeUsed(stack);
	}

	@Override
	public boolean noisySameStack(ItemStack otherStack)
	{
		return otherStack.getItem() instanceof BuzzsawItem buzzsawItem&&buzzsawItem.getHead(getStack()).getItem().equals(buzzsawItem.getHead(otherStack).getItem());
	}
}
