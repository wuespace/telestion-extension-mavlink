package de.wuespace.telestion.extension.mavlink.annotation;

import de.wuespace.telestion.extension.mavlink.message.MavlinkMessage;

import java.lang.annotation.*;

/**
 * An {@link Annotation} providing more information about a MAVLink-Message.
 *
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavInfo {
	/**
	 * Equivalent to the message id of the MAVLink-Specifications.
	 *
	 * @return message id
	 */
	int id();

	/**
	 * The calculated CRC_EXTRA byte for this message.
	 *
	 * @return CRC_EXTRA byte for this message
	 */
	int crc();
}
