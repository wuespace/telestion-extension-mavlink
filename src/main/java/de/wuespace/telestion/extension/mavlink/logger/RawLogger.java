package de.wuespace.telestion.extension.mavlink.logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.extension.mavlink.message.RawMavlinkPacket;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RawLogger extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startPromise) {
		var config = Config.get(forcedConfig, config(), Configuration.class);

		vertx.eventBus().consumer(config.inAddress(), raw -> JsonMessage.on(RawMavlinkPacket.class, raw, msg -> {
			var file = MavlinkFile.getTimeBasedFile(config.filePath());
			logger.debug("Saving {} MAVLink message to {}", msg.success() ? "" : "broken", file.getAbsolutePath());
			try {
				file.write(MavlinkFile.createEntry(msg.raw(), msg.success()));
				logger.debug("Saving raw MAVLink packet successful");
			} catch(IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
				logger.warn("Saving raw MAVLink packet failed", e);
			}
		}));

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	public record Configuration(@JsonProperty String inAddress,
								@JsonProperty String filePath) {
		/**
		 * For json-loading.
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null);
		}
	}

	public RawLogger(Configuration config) {
		this.forcedConfig = config;
	}

	public RawLogger(String inAddress, String outAddress, String filePath) {
		this(new Configuration(inAddress, filePath));
	}

	private final Logger logger = LoggerFactory.getLogger(RawLogger.class);

	private Configuration forcedConfig;
}
