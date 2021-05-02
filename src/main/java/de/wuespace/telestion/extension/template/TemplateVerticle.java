package de.wuespace.telestion.extension.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.services.message.Address;

/**
 * Template-Example-Class
 */
public final class TemplateVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(TemplateVerticle.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.setPeriodic(Duration.ofSeconds(2).toMillis(), timerId -> {
			vertx.eventBus().publish(Address.outgoing(this), new Position(0.3, 7.2, 8.0));
		});
		startPromise.complete();
	}

	public record Position(@JsonProperty double x, @JsonProperty double y,
						   @JsonProperty double z) implements JsonMessage {

		@SuppressWarnings("unused")
		private Position() {
			this(0.0, 0.0, 0.0);
		}
	}
}
