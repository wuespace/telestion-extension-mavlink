package de.wuespace.telestion.extension.mavlink.safer;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record MavlinkFileEntry(
		@JsonProperty
		String timeInfo,
		@JsonProperty
		byte[] rawMessage,
		@JsonProperty
		boolean success) implements JsonMessage {
}
