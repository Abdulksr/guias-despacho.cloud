package com.duoc.guias_despacho_cloud.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Lógica de comprobación de estado de RabbitMQ
        return Health.up().build();
    }
}
