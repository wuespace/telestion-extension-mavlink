package de.wuespace.telestion.extension.mavlink;

import de.wuespace.telestion.extension.mavlink.annotation.NativeType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefaultParsers {
	public static final HashMap<NativeType, TypeParser<?>> LITTLE_ENDIAN;

	static {
		LITTLE_ENDIAN = new HashMap<>();
		initLittleEndian();
	}

	private static void initLittleEndian() {
		LITTLE_ENDIAN.put(NativeType.INT_8, (payload, arraySize, offset) ->  {
			if (arraySize == 0) {
				return payload[offset];
			} else {
				return Arrays.copyOfRange(payload, offset, offset + arraySize);
			}
		});

		LITTLE_ENDIAN.put(NativeType.UINT_8, (payload, arraySize, offset) -> {
			var data = new int[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = payload[i + offset];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		LITTLE_ENDIAN.put(NativeType.INT_16, (payload, arraySize, offset) -> {
			var data = new int[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (payload[2 * i + 1 + offset] << 8) + payload[2 * i + offset];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		LITTLE_ENDIAN.put(NativeType.UINT_16, (payload, arraySize, offset) -> {
			var data = new int[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (payload[2 * i + 1 + offset] << 8) + payload[2 * i + offset];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		LITTLE_ENDIAN.put(NativeType.INT_32, (payload, arraySize, offset) -> {
			var data = new int[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (payload[4*i + 3 + offset] << 24) + (payload[4*i + 2 + offset] << 16)
						+ (payload[4*i + 1 + offset] << 8) + payload[4*i + offset];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		BiFunction<byte[], Integer, Long> foo = (payload, offset) -> {
			System.out.println((int) (payload[offset + 1] << 8) + Byte.toUnsignedInt(payload[offset]));
			return ((long) payload[offset + 7] << 56)
					+ ((long) payload[offset + 6] << 48) + ((long) payload[offset + 5] << 40)
					+ ((long) payload[offset + 4] << 32) + ((int) payload[offset + 3] << 24) + ((int) payload[offset + 2] << 16)
					+ ((int) payload[offset + 1] << 8) + payload[offset];
		};

		LITTLE_ENDIAN.put(NativeType.UINT_32, (payload, arraySize, offset) -> {
			var data = new long[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (payload[4*i + 3 + offset] << 24) + (payload[4*i + 2 + offset] << 16)
						+ (payload[4*i + 1 + offset] << 8) + payload[4*i + offset];
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		TypeParser<Object> fu = (payload, arraySize, offset) -> {
			var data = new long[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = foo.apply(payload, 8*i + offset);
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		};
		LITTLE_ENDIAN.put(NativeType.INT_64, fu);
		LITTLE_ENDIAN.put(NativeType.UINT_64, fu);

		LITTLE_ENDIAN.put(NativeType.FLOAT, (payload, arraySize, offset) -> {
			var data = new double[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = Float.intBitsToFloat((payload[4*i + 3 + offset] << 24) + (payload[4*i + 2 + offset] << 16)
						+ (payload[4*i + 1 + offset] << 8) + payload[4*i + offset]);
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		LITTLE_ENDIAN.put(NativeType.DOUBLE, (payload, arraySize, offset) -> {
			var data = new double[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = Double.longBitsToDouble(foo.apply(payload, 8*i + offset));
			}
			if (arraySize == 0) {
				return data[0];
			} else {
				return data;
			}
		});

		LITTLE_ENDIAN.put(NativeType.CHAR, (payload, arraySize, offset) -> {
			var data = new char[arraySize > 0 ? arraySize : 1];
			for (int i = 0; i < data.length; i++) {
				data[i] = (char) payload[i + offset];
			}

			if (arraySize == 0) {
				return data[0];
			} else {
				return String.valueOf(data);
			}
		});
	}
}
