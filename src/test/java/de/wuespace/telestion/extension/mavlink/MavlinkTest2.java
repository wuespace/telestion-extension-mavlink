package de.wuespace.telestion.extension.mavlink;

import de.wuespace.telestion.api.message.JsonMessage;
import de.wuespace.telestion.extension.mavlink.annotation.MavInfo;
import de.wuespace.telestion.extension.mavlink.message.MavlinkMessage;
import de.wuespace.telestion.extension.mavlink.messages.SeedHeartbeat;
import de.wuespace.telestion.extension.mavlink.messages.SeedLog;
import de.wuespace.telestion.extension.mavlink.messages.SeedSystemT;
import de.wuespace.telestion.services.connection.rework.ConnectionData;
import de.wuespace.telestion.services.connection.rework.Receiver;
import de.wuespace.telestion.services.connection.rework.Sender;
import de.wuespace.telestion.services.connection.rework.tcp.*;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class MavlinkTest2 {
	@Test
	public void testRoundTrip(Vertx vertx, VertxTestContext testContext) throws Throwable {
		logger.info("Registering all Mavlink-Messages");
		Stream.of(SeedHeartbeat.class, SeedLog.class, SeedSystemT.class).forEach(c -> {
			if (!c.isAnnotationPresent(MavInfo.class)) {
				testContext.failNow("Message " + c.getName() + " does not have mandatory MavInfo!");
			}
			MavInfo info = c.getAnnotation(MavInfo.class);
			MessageIndex.put(info.id(), c);
		});
		logger.info("All messages registered");

		var server = new TcpServer(new TcpServer.Configuration("serverIn", "serverOut", "localhost", 44104));
		var client = new TcpClient(new TcpClient.Configuration("clientIn", "clientOut", TcpTimeouts.DEFAULT_TIMEOUT));
		var dispatcher = new TcpDispatcher(new TcpDispatcher.Configuration("dispatchIn", "clientIn"), server);

		var sender = new Sender(new Sender.Configuration("senderIn", "dispatchIn"));
		var receiver = new Receiver(new Receiver.Configuration("receiverOut", "serverOut", "clientOut"));

		var validatorV2 = new ValidatorMavlink2(new ValidatorMavlink2.Configuration("receiverOut", "foo", "parserIn", new byte[] {'D', 'A', 'E', 'D', 'A', 'L', 'U', 'S'}));
		var parser = new PayloadParser(new PayloadParser.Configuration("parserIn", "parserOut"));

		logger.info("Deploying verticles...");
		Stream.of(server, client, dispatcher, sender, receiver, validatorV2, parser)
				.forEach(v -> vertx.deployVerticle(v, handler -> {
					if (handler.failed()) {
						testContext.failNow(handler.cause());
					}
				}));
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
		logger.info("All necessary verticles for this test have been deployed");

		vertx.eventBus().consumer("parserOut", raw -> {
			JsonMessage.on(MavlinkMessage.class, raw, msg -> {
				if (msg instanceof SeedLog log) {
					System.out.println("Log: " + log.json());
				} else if (msg instanceof SeedHeartbeat beat) {
					System.out.println("Heartbeat: " + beat.json());
				} else if (msg instanceof SeedSystemT systemT) {
					System.out.println("SystemT: " + systemT.json());
				} else {
					testContext.failNow("Unknown Mavlink-Message received!");
					return;
				}
				testContext.completeNow();
			});
		});
		logger.info("Reading input file");
		System.out.println(new File("messages_raw.txt").getAbsolutePath());
		var bytes = Files.readAllBytes(Path.of("src/test/message.txt"));
		logger.info("Finished loading prerequisites");

		Thread.sleep(Duration.ofSeconds(1).toMillis()); // To ensure everything works

		vertx.eventBus().publish("senderIn", new ConnectionData(bytes, new TcpDetails("localhost", 44104)).json());

		assertThat(testContext.awaitCompletion(5, TimeUnit.SECONDS), is(true));
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(MavlinkTest2.class);
}
