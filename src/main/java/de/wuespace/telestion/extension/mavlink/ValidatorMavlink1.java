package de.wuespace.telestion.extension.mavlink;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.extension.mavlink.annotation.MavInfo;
import de.wuespace.telestion.extension.mavlink.message.Mavlink1Information;
import de.wuespace.telestion.extension.mavlink.message.RawMavlinkPacket;
import de.wuespace.telestion.extension.mavlink.message.internal.ValidatedMavlinkPacket;
import de.wuespace.telestion.extension.mavlink.security.X25Checksum;
import de.wuespace.telestion.services.connection.rework.ConnectionData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author Cedric Boes
 * @version 1.0
 */
public final class ValidatorMavlink1 extends AbstractVerticle {

	@Override
	public final void start(Promise<Void> startPromise) {
		config = Config.get(config, config(), Configuration.class);

		vertx.eventBus().consumer(config.inAddress(), this::handleMessage);

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	/**
	 * Config-Class which can be used to create a new {@link ValidatorMavlink1}.
	 *
	 * @param inAddress        {@link #inAddress}
	 * @param packetOutAddress {@link #packetOutAddress}
	 * @param parserInAddress  {@link #parserInAddress}
	 */
	public final record Configuration(@JsonProperty String inAddress,
									  @JsonProperty String packetOutAddress,
									  @JsonProperty String parserInAddress) implements JsonMessage {
		/**
		 * Used for reflection!
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null, null);
		}
	}

	public ValidatorMavlink1() {
		this(null);
	}

	/**
	 * Creates a new {@link ValidatorMavlink1} with the given {@link Configuration}.
	 *
	 * @param config {@link Configuration}
	 */
	public ValidatorMavlink1(Configuration config) {
		this.config = config;
	}

	/**
	 * @param inAddress
	 * @param packetOutAddress
	 * @param parserInAddress
	 */
	public ValidatorMavlink1(String inAddress, String packetOutAddress, String parserInAddress) {
		this(new Configuration(inAddress, packetOutAddress, parserInAddress));
	}

	private <T> void handleMessage(Message<T> msg) {
		JsonMessage.on(ConnectionData.class, msg, packet -> {
			var raw = packet.rawData();

			// Checking raw packet constraints and if the packet is a MAVLinkV1 packet
			if (!(raw != null && raw.length > 7 && raw[0] == (byte) 0xFE)) {
				return;
			}

			logger.debug("MavlinkV1-packet received");

			var length = raw[1];

			// It can be greater if e.g. the packet length is smaller than the raw stream input
			if (raw.length - 8 - length < 0) {
				logger.info("Broken MavlinkV1-packet received!");
				vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false));
				return;
			}

			var seq = raw[2];
			var sysId = raw[3];
			var compId = raw[4];
			var msgId = raw[5];
			var payload = Arrays.copyOfRange(raw, 6, 6 + length);
			var checksum = Arrays.copyOfRange(raw, 6 + length, 6 + length + 2);

			var clazz = MessageIndex.get(msgId); // todo: null check
			if (!clazz.isAnnotationPresent(MavInfo.class)) {
				logger.warn("Annotation missing for {} (MavlinkV1)!", clazz.getName());
				vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false));
				return;
			}
			var annotation = clazz.getAnnotation(MavInfo.class);

			int messageChecksum = ((Byte.toUnsignedInt(checksum[1]) << 8) ^ Byte.toUnsignedInt(checksum[0])) & 0xffff;

			if (X25Checksum.calculate(Arrays.copyOfRange(raw, 1, length + 6), annotation.crc()) != messageChecksum) {
				logger.info("Checksum of received MavlinkV2-packet invalid");
				vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, false).json());
				return;
			}

			vertx.eventBus().publish(config.packetOutAddress(), new RawMavlinkPacket(raw, true));

			var mavInfo = new Mavlink1Information(seq, sysId, compId);
			vertx.eventBus().publish(config.parserInAddress(), new ValidatedMavlinkPacket(payload, clazz, mavInfo));
		});
	}

	private Configuration config;

	/**
	 * Handles all logs for {@link ValidatorMavlink1 this} verticle.
	 */
	private final Logger logger = LoggerFactory.getLogger(ValidatorMavlink2.class);

}
