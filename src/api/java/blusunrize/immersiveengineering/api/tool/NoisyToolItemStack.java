/*
 * BluSunrize
 * Copyright (c) 2025
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.api.tool;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class NoisyToolItemStack implements INoisyTool
{
	protected ItemStack stack;

	public NoisyToolItemStack(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public @Nullable ItemStack getStack()
	{
		return stack;
	}
}
