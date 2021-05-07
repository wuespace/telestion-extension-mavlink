package de.wuespace.telestion.extension.mavlink.message.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.extension.mavlink.message.MavlinkMessage;
import de.wuespace.telestion.extension.mavlink.message.PacketInformation;

/**
 * @param payload
 * @param clazz
 * @param info
 * @author Cedric Boes
 * @version 1.0
 */
public record ValidatedMavlinkPacket(@JsonProperty byte[] payload,
									 @JsonProperty Class<? extends MavlinkMessage> clazz,
									 @JsonProperty PacketInformation info) implements JsonMessage {
	/**
	 * Used for reflection!
	 */
	@SuppressWarnings("unused")
	private ValidatedMavlinkPacket() {
		this(null, null, null);
	}
}
