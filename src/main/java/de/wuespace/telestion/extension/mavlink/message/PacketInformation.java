package de.wuespace.telestion.extension.mavlink.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.wuespace.telestion.api.message.JsonMessage;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "className")
public interface PacketInformation extends JsonMessage {
}
