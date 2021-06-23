package de.wuespace.telestion.extension.mavlink.parsing;

import de.wuespace.telestion.extension.mavlink.annotation.NativeType;
import de.wuespace.telestion.extension.mavlink.TypeParser;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.IntStream;

public class DefaultParsers {
	public static final HashMap<NativeType, TypeParser<?>> LITTLE_ENDIAN;

	static {
		LITTLE_ENDIAN = new HashMap<>();
		initLittleEndian();
	}

	private static void initLittleEndian() {
		var typeParser = new LittleEndian();
		registerDefaultParser(LITTLE_ENDIAN, typeParser);
	}

	private static Object parse(Function<byte[], ?> parser, byte[] payload, int arraySize, int offset,
								NativeType nativeType) {
		var objects = IntStream.range(0, arraySize == 0 ? 1 : arraySize)
				.mapToObj(i -> parser.apply(Arrays.copyOfRange(payload, offset + i, offset + nativeType.size + i))).toArray();

		var type = objects[0].getClass();
		var array = Array.newInstance(type, objects.length);
		System.arraycopy(objects, 0, array, 0, objects.length);

		var parsed = ArrayUtils.toPrimitive(array);

		if (arraySize == 0) {
			return Array.get(parsed, 0);
		}
		return parsed;
	}

	private static void registerDefaultParser(HashMap<NativeType, TypeParser<?>> defaultParser, Parser typeParser) {
		defaultParser.put(NativeType.INT_8, (payload, arraySize, offset) ->
				parse(typeParser::parseByte, payload, arraySize, offset, NativeType.INT_8));

		defaultParser.put(NativeType.UINT_8, (payload, arraySize, offset) ->
				parse(typeParser::parseUnsignedByte, payload, arraySize, offset, NativeType.UINT_8));

		defaultParser.put(NativeType.INT_16, (payload, arraySize, offset) ->
				parse(typeParser::parseShort, payload, arraySize, offset, NativeType.INT_16));

		defaultParser.put(NativeType.UINT_16, (payload, arraySize, offset) ->
				parse(typeParser::parseUnsignedShort, payload, arraySize, offset, NativeType.UINT_16));

		defaultParser.put(NativeType.INT_32, (payload, arraySize, offset) ->
				parse(typeParser::parseInt, payload, arraySize, offset, NativeType.INT_32));

		defaultParser.put(NativeType.UINT_32, (payload, arraySize, offset) ->
				parse(typeParser::parseUnsignedInt, payload, arraySize, offset, NativeType.UINT_32));

		defaultParser.put(NativeType.INT_64, (payload, arraySize, offset) ->
				parse(typeParser::parseLong, payload, arraySize, offset, NativeType.INT_64));

		defaultParser.put(NativeType.UINT_64, (payload, arraySize, offset) ->
				parse(typeParser::parseUnsignedLong, payload, arraySize, offset, NativeType.UINT_64));

		defaultParser.put(NativeType.FLOAT, (payload, arraySize, offset) ->
				parse(typeParser::parseFloat, payload, arraySize, offset, NativeType.FLOAT));

		defaultParser.put(NativeType.DOUBLE, (payload, arraySize, offset) ->
				parse(typeParser::parseDouble, payload, arraySize, offset, NativeType.DOUBLE));

		defaultParser.put(NativeType.CHAR, (payload, arraySize, offset) ->
				parse(typeParser::parseChar, payload, arraySize, offset, NativeType.CHAR));
	}
}
