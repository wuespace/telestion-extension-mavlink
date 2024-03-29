package de.wuespace.telestion.extension.mavlink.logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

public record MavlinkFileRepresentation(@JsonProperty
										MavlinkFileEntry[] entries) implements JsonMessage {
}
