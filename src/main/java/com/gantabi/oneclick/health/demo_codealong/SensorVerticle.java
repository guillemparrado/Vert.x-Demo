package com.gantabi.oneclick.health.demo_codealong;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class SensorVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private static final int httpPort = Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "8080"));

  private final String uuid = UUID.randomUUID().toString();
  private double temperature = 21.0;
  private final Random random = new Random();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    logger.info("SensorVerticle started");
    vertx.setPeriodic(2000, this::updateTemperature);

    Router router = Router.router(vertx);
    router.get("/data").handler(this::getData);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(httpPort)
      .onSuccess(ok -> {
        logger.info("http server running: http://localhost:{}", httpPort);
        startPromise.complete();
      })
      .onFailure(startPromise::fail);
  }

  private void getData(RoutingContext context) {
    logger.info("Processing HTTP request from {}", context.request().remoteAddress());
    JsonObject payload = createPayload();
    context.response()
      .putHeader("Content-Type", "application/json")
      .setStatusCode(200)
      .end(payload.encode());
  }

  private JsonObject createPayload() {
    return new JsonObject()
      .put("uuid", uuid)
      .put("temperature", temperature)
      .put("timestamp", System.currentTimeMillis());
  }

  private void updateTemperature(Long id) {
    temperature += random.nextGaussian() / 2.0d;
    logger.info("Temperature updated: {}", temperature);

    vertx.eventBus().publish("temperature.updates", createPayload());
  }
}
