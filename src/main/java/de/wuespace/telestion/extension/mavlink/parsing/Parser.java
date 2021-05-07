package de.wuespace.telestion.extension.mavlink.parsing;

public interface Parser {
	byte parseByte(byte[] raw);

	int parseUnsignedByte(byte[] raw);

	int parseShort(byte[] raw);

	int parseUnsignedShort(byte[] raw);

	int parseInt(byte[] raw);

	long parseUnsignedInt(byte[] raw);

	long parseLong(byte[] raw);

	long parseUnsignedLong(byte[] raw);

	float parseFloat(byte[] raw);

	double parseDouble(byte[] raw);

	char parseChar(byte[] raw);
}
