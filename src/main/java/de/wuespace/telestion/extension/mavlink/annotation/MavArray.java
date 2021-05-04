package de.wuespace.telestion.extension.mavlink.annotation;

import de.wuespace.telestion.extension.mavlink.message.MavlinkMessage;

import java.lang.annotation.*;

/**
 * An {@link Annotation} specifying that a MAVLink-Message-Field is an array.<br>
 * Can be used to obtain more information about this array, too.
 *
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavArray {
	/**
	 * Returns the length of the array.
	 *
	 * @return length of the array
	 */
	int length();
}
