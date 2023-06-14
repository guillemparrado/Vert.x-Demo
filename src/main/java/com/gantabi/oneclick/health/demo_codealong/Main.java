package com.gantabi.oneclick.health.demo_codealong;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SensorVerticle());

    // Doesn't work
    // vertx.deployVerticle("SensorVerticle", new DeploymentOptions().setInstances(4));

    vertx.eventBus()
      .<JsonObject>consumer("temperature.updates", message -> {
        logger.info(">>> {}", message.body().encodePrettily());
      });

    /*
    // Clustered vertx: External engine (Hazelcast, others).
    Vertx.clusteredVertx(new VertxOptions())
      .onSuccess(clusterdVertx -> clusterdVertx.deployVerticle(new SensorVerticle()))
      .onFailure(f -> logger.error("Woops", f));
     */
  }
}
