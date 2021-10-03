/*
 * BluSunrize
 * Copyright (c) 2021
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.api.tool;

import blusunrize.immersiveengineering.api.Lib;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public class LogicCircuitHandler
{
	public static boolean boolifyRSV(byte redStoneValue)
	{
		return redStoneValue!=0;
	}

	private static byte deboolifyRSV(boolean redStoneValue)
	{
		return redStoneValue?(byte)15: (byte)0;
	}

	private static byte wrapBooleanLogic(byte[] args, BooleanLogicCircuitOperator boolOp)
	{
		boolean[] boolArgs = new boolean[args.length];
		for(int i = 0; i < args.length; i++)
		{
			boolArgs[i] = boolifyRSV(args[i]);
		}
		return deboolifyRSV(boolOp.apply(boolArgs));
	}

	private enum BooleanLogicCircuitOperator
	{
		NOT(args -> !args[0]),
		OR(args -> args[0]|args[1]),
		AND(args -> args[0]&args[1]),
		XOR(args -> args[0]^args[1]),
		NOR(args -> !args[0]|args[1]),
		NAND(args -> !args[0]&args[1]),
		XNOR(args -> args[0]==args[1]);

		private final Function<boolean[], Boolean> operator;

		BooleanLogicCircuitOperator(Function<boolean[], Boolean> operator)
		{
			this.operator = operator;
		}

		public boolean apply(boolean[] args)
		{
			return this.operator.apply(args);
		}
	}

	public enum LogicCircuitOperator
	{
		SET(1, args -> args[0], 0),
		NOT(1, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.NOT), 1),
		OR(2, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.OR), 3),
		AND(2, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.AND), 2),
		XOR(2, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.XOR), 4),
		NOR(2, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.NOR), 4),
		NAND(2, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.NAND), 1),
		XNOR(2, args -> wrapBooleanLogic(args, BooleanLogicCircuitOperator.XNOR), 5);

		private final int argumentCount;
		private final Function<byte[], Byte> operator;
		private final int complexity;

		public static final int TOTAL_MAX_INPUTS = Arrays.stream(values())
				.mapToInt(LogicCircuitOperator::getArgumentCount)
				.max()
				.orElse(1);

		LogicCircuitOperator(int argumentCount, Function<byte[], Byte> operator, int complexity)
		{
			this.argumentCount = argumentCount;
			this.operator = operator;
			this.complexity = complexity;
		}

		public int getArgumentCount()
		{
			return this.argumentCount;
		}

		public int getComplexity()
		{
			return complexity;
		}

		public byte apply(byte[] args)
		{
			return this.operator.apply(args);
		}

		@Nullable
		public static LogicCircuitOperator getByString(String name)
		{
			if(name==null)
				return null;
			return valueOf(name);
		}

		public BaseComponent getDescription()
		{
			return new TranslatableComponent(Lib.DESC_INFO+"operator."+this.name().toLowerCase(Locale.ENGLISH));
		}
	}

	public enum LogicCircuitRegister
	{
		// All 16 dye colors
		WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY,
		LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK,
		// Plus 8 internal storages
		R0, R1, R2, R3, R4, R5, R6, R7;

		public BaseComponent getDescription()
		{
			if(this.ordinal() < 16)
				return new TranslatableComponent("color.minecraft."+DyeColor.byId(this.ordinal()).getName());
			else
				return new TranslatableComponent(Lib.DESC_INFO+"register", this.ordinal()-16);
		}
	}

	public static final class LogicCircuitInstruction
	{
		private final LogicCircuitOperator operator;
		private final LogicCircuitRegister output;
		private final LogicCircuitRegister[] inputs;

		private final BaseComponent formattedString;

		public LogicCircuitInstruction(LogicCircuitOperator operator, LogicCircuitRegister output, LogicCircuitRegister[] inputs)
		{
			Preconditions.checkArgument(operator.getArgumentCount()==inputs.length, "Logic inputs must match the argument count of the operator");
			this.operator = operator;
			this.output = output;
			this.inputs = inputs;

			this.formattedString = this.output.getDescription();
			this.formattedString.append(" = ");
			this.formattedString.append(this.operator.getDescription());
			for(int i = 0; i < inputs.length; i++)
			{
				this.formattedString.append(i!=0?", ": ": ");
				this.formattedString.append(inputs[i].getDescription());
			}
		}

		public LogicCircuitOperator getOperator()
		{
			return operator;
		}

		public LogicCircuitRegister getOutput()
		{
			return output;
		}

		public LogicCircuitRegister[] getInputs()
		{
			return inputs;
		}

		public void apply(ILogicCircuitHandler handler)
		{
			byte[] bInputs = new byte[operator.getArgumentCount()];
			for(int i = 0; i < inputs.length; i++)
				bInputs[i] = handler.getLogicCircuitRegister(inputs[i]);
			handler.setLogicCircuitRegister(output, operator.apply(bInputs));
		}

		public BaseComponent getFormattedString()
		{
			return this.formattedString;
		}

		public CompoundTag serialize()
		{
			CompoundTag nbt = new CompoundTag();
			nbt.putString("operator", this.operator.name());
			nbt.putString("output", this.output.name());
			ListTag inputList = new ListTag();
			for(LogicCircuitRegister input : this.inputs)
				inputList.add(StringTag.valueOf(input.name()));
			nbt.put("inputs", inputList);
			return nbt;
		}

		public static LogicCircuitInstruction deserialize(CompoundTag nbt)
		{
			LogicCircuitOperator operator = LogicCircuitOperator.valueOf(nbt.getString("operator"));
			LogicCircuitRegister output = LogicCircuitRegister.valueOf(nbt.getString("output"));
			ListTag inputList = nbt.getList("inputs", NBT.TAG_STRING);
			LogicCircuitRegister[] inputs = new LogicCircuitRegister[inputList.size()];
			for(int i = 0; i < inputs.length; i++)
				inputs[i] = LogicCircuitRegister.valueOf(inputList.getString(i));
			return new LogicCircuitInstruction(operator, output, inputs);
		}
	}

	public interface ILogicCircuitHandler
	{
		byte getLogicCircuitRegister(LogicCircuitRegister register);

		void setLogicCircuitRegister(LogicCircuitRegister register, byte state);
	}
}
