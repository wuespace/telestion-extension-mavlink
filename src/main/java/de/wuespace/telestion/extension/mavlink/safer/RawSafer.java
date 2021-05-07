package de.wuespace.telestion.extension.mavlink.safer;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.wuespace.telestion.api.config.Config;
import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.extension.mavlink.message.RawMavlinkPacket;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawSafer extends AbstractVerticle {
	@Override
	public void start(Promise<Void> startPromise) {
		var config = Config.get(forcedConfig, config(), Configuration.class);

		vertx.eventBus().consumer(config.inAddress(), raw -> JsonMessage.on(raw, RawMavlinkPacket.class, msg -> {

		}));

		startPromise.complete();
	}

	@Override
	public void stop(Promise<Void> stopPromise) {
		stopPromise.complete();
	}

	public record Configuration(@JsonProperty String inAddress,
								@JsonProperty String outAddress,
								@JsonProperty String filePath,
								@JsonProperty boolean timeStamps) {
		/**
		 * For json-loading.
		 */
		@SuppressWarnings("unused")
		private Configuration() {
			this(null, null, null, false);
		}
	}

	public RawSafer(Configuration config) {
		this.forcedConfig = config;
	}

	public RawSafer(String inAddress, String outAddress, String filePath, boolean timeStamps) {
		this(new Configuration(inAddress, outAddress, filePath, timeStamps));
	}

	private final Logger logger = LoggerFactory.getLogger(RawSafer.class);

	private Configuration forcedConfig;
}
