package de.wuespace.telestion.extension.mavlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.extension.mavlink.annotation.MavInfo;
import de.wuespace.telestion.extension.mavlink.message.Mavlink2Information;
import de.wuespace.telestion.extension.mavlink.message.RawMavlinkPacket;
import de.wuespace.telestion.extension.mavlink.message.internal.ValidatedMavlinkPacket;
import de.wuespace.telestion.extension.mavlink.security.MavV2Signator;
import de.wuespace.telestion.extension.mavlink.security.SecretKeySafe;
import de.wuespace.telestion.extension.mavlink.security.X25Checksum;
import de.wuespace.telestion.services.connection.rework.ConnectionData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author Cedric Boes
 * @version 1.0
 */
public final class ValidatorMavlink2 extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) {
		var config = Config.get(this.config, config(), Configuration.class);

		if (config != this.config) {
			safe.deleteKey();
			safe = new SecretKeySafe(config.password());
		}

		this.config = config;

		vertx.eventBus().consumer(config.inAddress(), this::handleMessage);

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		deleteKey();
		stopPromise.complete();
	}


	public void deleteKey() {
		safe.deleteKey();
	}

	/**
	 * Config-Class which can be used to create a new {@link ValidatorMavlink2}.
	 *
	 * @param inAddress        {@link #inAddress}
	 * @param packetOutAddress {@link #packetOutAddress}
	 * @param parserInAddress  {@link #parserInAddress}
	 */
	public final record Configuration(@JsonProperty String inAddress,
									  @JsonProperty String packetOutAddress,
									  @JsonProperty String parserInAddress,
									  @JsonProperty byte[] password) implements JsonMessage {
		/**
		 * Used for reflection!
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null, null, null);
		}
	}

	public ValidatorMavlink2() {
		this(null);
	}

	/**
	 * Creates a new {@link ValidatorMavlink2} with the given {@link Configuration}.
	 *
	 * @param config {@link Configuration}
	 */
	public ValidatorMavlink2(Configuration config) {
		this.config = config;
		this.safe = new SecretKeySafe(this.config.password());
	}

	/**
	 * @param inAddress
	 * @param packetOutAddress
	 * @param parserInAddress
	 */
	public ValidatorMavlink2(String inAddress, String packetOutAddress, String parserInAddress, byte[] password) {
		this(new Configuration(inAddress, packetOutAddress, parserInAddress, password));
	}

	private void handleMessage(Message<?> msg) {
		JsonMessage.on(ConnectionData.class, msg, packet -> {
			var raw = packet.rawData();

			// Checking raw packet constraints and if the packet is a MAVLinkV2 packet
			if (!(raw != null && raw.length > 11 && raw[0] == (byte) 0xFD)) {
				return;
			}

			logger.debug("MavlinkV2-packet received");

			var length = raw[1];

			// It can be greater if e.g. the packet length is smaller than the raw stream input
			if (raw.length - 12 - length < 0) {
				logger.info("Broken MavlinkV2-packet received!");
				vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false));
				return;
			}

			var incompatFlags = raw[2];
			var compatFlags = raw[3];
			var seq = raw[4];
			var sysId = raw[5];
			var compId = raw[6];
			var msgId = (((int) raw[9]) << 16) + (((int) raw[8]) << 8) + (int) raw[7]; // todo: null check
			var payload = Arrays.copyOfRange(raw, 10, 10 + length);
			var checksum = Arrays.copyOfRange(raw, 10 + length, 10 + length + 2);

			var clazz = MessageIndex.get(msgId);
			if (!clazz.isAnnotationPresent(MavInfo.class)) {
				logger.warn("Annotation missing for {} (MavlinkV2)!", clazz.getName());
				vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false).json());
				return;
			}
			var annotation = clazz.getAnnotation(MavInfo.class);

			// Currently supported incompatibility flags
			if (incompatFlags == 0x01) {
				if (raw.length >= 10 + length + 2 + 13) {
					logger.info("Broken MavlinkV2-packet received!");
					vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false).json());
					return;
				}

				var rawSign = Arrays.copyOfRange(raw, 10 + length + 2, 10 + length + 2 + 13);
				var linkId = rawSign[0];
				var timeStamp = Arrays.copyOfRange(rawSign, 1, 7);
				var sign = Arrays.copyOfRange(rawSign, 7, 13);

				var state = false;

				try {
					state = Arrays.equals(MavV2Signator.rawSignature(safe.getSecretKey(),
							Arrays.copyOfRange(raw, 0, 10), payload, annotation.crc(), linkId, timeStamp),
							sign);
				} catch (NoSuchAlgorithmException e) {
					logger.error("Specified Encryption Algorithm not found! This means that all received packets " +
							"with signatures will be rejected!", e);
				}
				if (!state) {
					vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false).json());
					return;
				}
			}

			if (X25Checksum.calculate(Arrays.copyOfRange(raw, 0, length + 10),
					annotation.crc()) != (checksum[0] >> 8) + checksum[1]) {
				logger.info("Checksum of received MavlinkV2-packet invalid!");
				//vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false).json());
				//return;
			}

			vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, true).json());

			var mavInfo = new Mavlink2Information(incompatFlags, compatFlags, seq, sysId, compId);
			vertx.eventBus().publish(config.parserInAddress(),
					new ValidatedMavlinkPacket(payload, clazz, mavInfo).json());
		});
	}

	/**
	 * Handles all logs for {@link ValidatorMavlink2 this} verticle.
	 */
	private final Logger logger = LoggerFactory.getLogger(ValidatorMavlink2.class);

	/**
	 * Stores the secret key as a byte[]-array for the Mavlink-signature.
	 */
	private SecretKeySafe safe;

	private Configuration config;
}
