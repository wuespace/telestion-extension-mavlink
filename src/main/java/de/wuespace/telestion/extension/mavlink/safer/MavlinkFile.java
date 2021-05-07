package de.wuespace.telestion.extension.mavlink.safer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CSV-inspired file format for raw unparsed MAVLink messages.
 *
 * @author Cedric Boes
 * @version 07-05-2021
 */
public final class MavlinkFile {

	public static final char IGNORE_CHAR	= '\\';
	public static final char BATCH_SEP  	= '\n';
	public static final char DATA_SEP   	= ';';

	public static final String PATH_PREFIX		= "raw_data_";
	public static final String FILE_TYPE		= ".mavlink";
	public static final String FILE_DATE_FORMAT	= "yyyy-MM-dd_HH-mm";
	public static final Charset ENCODING		= StandardCharsets.ISO_8859_1;

	public static final String ENTRY_TIME_FORMAT	= "dd_HH-mm-ss-SSS";		// The rest you get out of the file info

	public MavlinkFile(String fullPath) {
		this.file = new File(fullPath);
	}

	public MavlinkFileRepresentation read() throws IOException {
		var contents = Files.readAllBytes(Path.of(file.getAbsolutePath()));
		var builder = new ArrayList<MavlinkFileEntry>();

		var ignoreNext = false;
		var lastStartIndex = 0;
		var timeSepIndex = -1;
		var successSepIndex = -1;

		for (int i = 0; i < contents.length; i++) {
			var c = (char) contents[i];

			if (ignoreNext) {
				ignoreNext = false;
				continue;
			}

			switch (c) {
				case IGNORE_CHAR -> ignoreNext = true;
				case DATA_SEP -> {
					if (timeSepIndex == -1) {
						timeSepIndex = i;
					} else if (successSepIndex == -1) {
						successSepIndex = i;
					} else {
						throw new IOException("MAVLink file is broken!");
					}
				}
				case BATCH_SEP -> {
					var data = Arrays.copyOfRange(contents, timeSepIndex + 1, i);
					var raw = new String(data, ENCODING);
					raw = raw.replace(ign + ign, ign);
					raw = raw.replace(ign + ds, ds);
					raw = raw.replace(ign + bs, bs);

					builder.add(new MavlinkFileEntry(
							new String(Arrays.copyOfRange(contents, lastStartIndex, timeSepIndex), ENCODING),
							raw.getBytes(ENCODING),
							data[successSepIndex + 1] == 1));
					lastStartIndex = i + 1;
					timeSepIndex = -1;
					successSepIndex = -1;
				}
			}
		}

		return new MavlinkFileRepresentation(builder.toArray(MavlinkFileEntry[]::new));
	}

	public FileInputStream getStream() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public void write(MavlinkFileEntry entry) throws IOException {
		var fw = new FileWriter(file, ENCODING);

		var raw = new String(entry.rawMessage(), ENCODING);
		raw = raw.replace(ign, ign + ign);
		raw = raw.replace(ds, ign + ds);
		raw = raw.replace(bs, ign + bs);

		fw.write(entry.timeInfo() + DATA_SEP + raw + DATA_SEP + (entry.success() ? 1 : 0) + BATCH_SEP);
	}

	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	public static MavlinkFileEntry createEntry(byte[] rawBytes, boolean success) {
		return new MavlinkFileEntry(entryDateFormatter.format(LocalTime.now()), rawBytes, success);
	}

	public static MavlinkFile getTimeBasedFile(String path) {
		return getTimeBasedFile(path, fileDateFormatter.format(LocalTime.now()));
	}

	public static MavlinkFile getTimeBasedFile(String path, String time) {
		return new MavlinkFile(path + PATH_PREFIX + time + FILE_TYPE);
	}

	private final File file;
	private static final SimpleDateFormat fileDateFormatter = new SimpleDateFormat(FILE_DATE_FORMAT);
	private static final SimpleDateFormat entryDateFormatter = new SimpleDateFormat(ENTRY_TIME_FORMAT);
	private static final String ign = String.valueOf(IGNORE_CHAR);
	private static final String ds = String.valueOf(DATA_SEP);
	private static final String bs = String.valueOf(BATCH_SEP);
}
