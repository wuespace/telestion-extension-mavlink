package de.wuespace.telestion.extension.mavlink.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.extension.mavlink.annotation.MavArray;
import de.wuespace.telestion.extension.mavlink.annotation.MavField;
import de.wuespace.telestion.extension.mavlink.annotation.MavInfo;
import de.wuespace.telestion.extension.mavlink.NativeType;
import de.wuespace.telestion.extension.mavlink.message.MavlinkMessage;

/**
 *  Contains the some Seeds system_t information + local time.<br>
 * <br>
 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
 *
 * @author Autogenerated by XML2Record-Tool (by Cedric Boes)
 * @version 1.0 (autogenerated)
 */
@MavInfo(id = 10005, crc = 120)
@SuppressWarnings("preview")
public record Drehtest(
		/**
		 * Seed local time<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.INT_64)
		@JsonProperty long timeLocal,
		/**
		 *  acceleration along x-axis, multiple of g<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float imuAccX,
		/**
		 *  acceleration along y-axis, multiple of g<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float imuAccY,
		/**
		 *  acceleration along z-axis, multiple of g<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float imuAccZ,
		/**
		 * angular velocity around x-axis <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float imuGyroX,
		/**
		 * angular velocity around y-axis <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float imuGyroY,
		/**
		 * angular velocity around z-axis <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float imuGyroZ,
		/**
		 * rotation rate of the rotor in rad/s <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float tachoRotRate,
		/**
		 * vertical velocity of the seed in m/s � negative if seed is falling<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float filterVelVertical,
		/**
		 * height above ground in m<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float filterHeightGround,
		/**
		 * absolute (to air/world frame) rotation rate of the rotor in rad/s" <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float filterRotorRotRate,
		/**
		 * absolute (to air/world frame) rotation rate of the body in rad/s"<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float filterBodyRotRate,
		/**
		 * induced vertical velocity (lift)  in m/s<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float filterVelVerticalInd,
		/**
		 * setpoint for the pitch angle for the servos <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float controllerBladePitch,
		/**
		 * desired fin angle <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.FLOAT)
		@JsonProperty float controllerFinAngle,
		/**
		 * contains swashplate_servo1_amps, swashplate_servo2_amps, swashplate_servo3_amps and fin_servo_amps in this order with actual data size units of 12 bits. <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */

		@MavArray(length = 6)
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int[] servoAmps,
		/**
		 * rxsm_volts (RXSM voltage), bat1_volts (Battery block 1 voltage), bat2_volts (Battery block 2 voltage), rail3v3_volts (3V3 Rail voltage) in this order with actual data size units of 12 bits. <br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */

		@MavArray(length = 6)
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int[] systemVoltageInfo,
		/**
		 * indentifier for controller type/state<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int controllerId,
		/**
		 * imu_acc_avail, imu_gyro_avail, baro_avail, vacuum_baro_avail, tacho_rot_avail, servo_amps_avail, bat_temp_avail, volts_avail in this order with individual size of 1 bit<br>
		 * <br>
		 * <i>Autogenerated by XML2Record-Tool v1.3.11</i>
		 */
		@MavField(nativeType = NativeType.UINT_8)
		@JsonProperty int availableStatus) implements MavlinkMessage {
	/**
	 * There shall be no default-constructor for normal developers.
	 */
	@SuppressWarnings("unused")
	private Drehtest() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, 0, 0);
	}
}
