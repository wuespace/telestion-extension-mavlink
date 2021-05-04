package de.wuespace.telestion.extension.mavlink.annotation;

import de.wuespace.telestion.extension.mavlink.NativeType;
import de.wuespace.telestion.extension.mavlink.message.MavlinkMessage;

import java.lang.annotation.*;

/**
 * An {@link Annotation} giving more information about a MAVLink-Message-Field.
 *
 * @author Cedric Boes
 * @version 1.0
 * @see MavlinkMessage
 */
@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavField {
	/**
	 * Returns the {@link NativeType} of the field.
	 *
	 * @return {@link NativeType} of the field
	 */
	NativeType nativeType();

	/**
	 * Returns the position of a MAVLink-Field in the raw array if the raw packet is not arranged according to the
	 * specifications.<br>
	 * The default value of <code>-1</code> means that the default-position is used.
	 *
	 * @return position of the field in the raw array
	 */
	int position() default -1;

	/**
	 * Returns whether a field is an extension or not.
	 *
	 * @return if a field is an extension
	 */
	boolean extension() default false;
}
