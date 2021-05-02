package de.wuespace.telestion.extension.mavlink.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.message.JsonMessage;

/**
 * This will be published on the bus, if the {@link de.wuespace.telestion.extension.mavlink.Validator Validator} detects
 * that a packet was a MAVLink-packet.<br>
 * The record also contains information about the success of the parsing-process of the validator.
 *
 * @param raw bytes of the {@link de.wuespace.telestion.services.connection.rework.ConnectionData ConnectionData}
 * @param success if the validation process was successful
 *
 * @author Cedric Boes
 * @version 1.0
 * @see de.wuespace.telestion.extension.mavlink.Validator
 */
public record RawMavlinkPacket(@JsonProperty byte[] raw,
							   @JsonProperty boolean success) implements JsonMessage {
	@SuppressWarnings("unused")
	private RawMavlinkPacket() {
		this(null, false);
	}
}
