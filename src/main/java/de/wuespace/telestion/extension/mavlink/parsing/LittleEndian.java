package de.wuespace.telestion.extension.mavlink.parsing;

import java.util.Arrays;

public class LittleEndian implements Parser {
	@Override
	public byte parseByte(byte[] raw) {
		return raw[0];
	}

	@Override
	public int parseUnsignedByte(byte[] raw) {
		return Byte.toUnsignedInt(raw[0]);
	}

	@Override
	public int parseShort(byte[] raw) {
		var unsigned = toUnsignedBytes(raw, 2);
		return (unsigned[1] << 8) + unsigned[0];
	}

	@Override
	public int parseUnsignedShort(byte[] raw) {
		var unsigned = toUnsignedBytes(raw);
		return (unsigned[1] << 8) + unsigned[0];
	}

	@Override
	public int parseInt(byte[] raw) {
		var unsigned = toUnsignedBytes(raw, 4);
		return (unsigned[3] << 24) + (unsigned[2] << 16) + (unsigned[1] << 8) + unsigned[0];
	}

	@Override
	public long parseUnsignedInt(byte[] raw) {
		var unsigned = toUnsignedBytes(raw);
		return ((long) unsigned[3] << 24) + ((long) unsigned[2] << 16) + ((long) unsigned[1] << 8) + unsigned[0];
	}

	@Override
	public long parseLong(byte[] raw) {
		return ((long) parseInt(Arrays.copyOfRange(raw, 4, 8)) << 32)
				+ parseUnsignedInt(Arrays.copyOfRange(raw, 0, 4));
	}

	@Override
	public long parseUnsignedLong(byte[] raw) {
		return (parseUnsignedInt(Arrays.copyOfRange(raw, 4, 8)) << 32)
				+ parseUnsignedInt(Arrays.copyOfRange(raw, 0, 4));
	}

	@Override
	public float parseFloat(byte[] raw) {
		return Float.intBitsToFloat(parseInt(raw));
	}

	@Override
	public double parseDouble(byte[] raw) {
		return Double.longBitsToDouble(parseLong(raw));
	}

	@Override
	public char parseChar(byte[] raw) {
		return (char) raw[0];
	}

	private static int[] toUnsignedBytes(byte[] raw) {
		return toUnsignedBytes(raw, -1);
	}

	private static int[] toUnsignedBytes(byte[] raw, int keepSign) {
		var unsigned = new int[raw.length];

		for (int i = 0; i < raw.length; i++) {
			if (i % keepSign != 0 || keepSign == -1) {
				unsigned[i] = Byte.toUnsignedInt(raw[i]);
			} else {
				unsigned[i] = raw[i];
			}
		}
		return unsigned;
	}
}
