/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.api.tool;

import blusunrize.immersiveengineering.api.Lib.NoisyToolCapabilities;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for Items (not ItemStacks, that would be stupid), no guarantees if it is used for non-Items.
 * It is expected that implementing classes hold an ItemStack, but not required.
 */
public interface INoisyTool
{
	Holder<SoundEvent> getIdleSound();

	Holder<SoundEvent> getBusySound();

	/**
	 * Due to lacking information on sound duration, the duration is hard coded. Any Fading sounds need to be <b>more</b> than <b>1.0s</b> in duration.
	 * The sound cuts off after <b>1.0s</b>, but a little bit of excess duration (>~0.01s) is required for the noisy tool sound stage machine to work correctly
	 *
	 * @return fading sound
	 */
	Holder<SoundEvent> getFadingSound();

	/**
	 * Due to lacking information on sound duration, the duration is hard coded. Any Attack sounds need to be <b>more</b> than <b>0.3s</b> in duration.
	 * The sound cuts off after <b>0.3s</b>, but a little bit of excess duration (>~0.01s) is required for the noisy tool sound stage machine to work correctly
	 * Having a too small excess duration leads to notable gaps in the audio when transitioning, which is why the default attack sounds have ~0.06s extra.
	 * Cause they used to be 0.35s and then it caused issues.. Take heed ^^
	 *
	 * @return attack sound
	 */
	Holder<SoundEvent> getAttackSound();

	Holder<SoundEvent> getHarvestSound();

	boolean isActive();

	/**
	 * Checks if the stack item is a NoisyTool and is active (and thus able) to make noise.
	 *
	 * @param stack the ItemStack to check. May be any ItemStack.
	 * @return true if the stack item is a NoisyTool and is able to make noise.
	 */
	static boolean isActiveNoisyTool(ItemStack stack)
	{
		INoisyTool noisyTool = stack.getCapability(NoisyToolCapabilities.ITEM);
		return noisyTool!=null&&noisyTool.isActive();
	}

	/**
	 * When an ItemStack gets modified server side (i.e. takes damage, changes tags (i.e. uses fuel), etc.), it creates a new ItemStack on the client side.
	 * There is no unreasonably involved way to check if the new ItemStack is actually just the old ItemStack, but modified.
	 * So this for these cases, this default implementation checks the next best thing: Item equality and sound equality.
	 * <p>
	 * It is encouraged to override this with a simpler check.
	 * <p>
	 * This check also assumes, that it has already been checked and confirmed, that the stacks are not identical
	 *
	 * @param otherStack the stack mainStack is compared against
	 * @return true if stacks are considered the same stack. By default: if stacks  produce the same sounds.
	 */
	default boolean noisySameStack(ItemStack otherStack)
	{
		INoisyTool otherNoisyTool = otherStack.getCapability(NoisyToolCapabilities.ITEM);

		return this.equals(otherNoisyTool)
				&&this.getIdleSound().equals(otherNoisyTool.getIdleSound())
				&&this.getBusySound().equals(otherNoisyTool.getBusySound())
				&&this.getFadingSound().equals(otherNoisyTool.getFadingSound())
				&&this.getAttackSound().equals(otherNoisyTool.getAttackSound())
				&&this.getHarvestSound().equals(otherNoisyTool.getHarvestSound());
	}

	@Nullable
	ItemStack getStack();
}
