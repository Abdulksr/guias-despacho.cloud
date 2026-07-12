package com.duoc.guias_despacho_cloud.config;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_GUIAS_PRINCIPAL = "guias_queue";
    public static final String QUEUE_ERRORES = "guias_errores_queue";
    public static final String EXCHANGE_GUIAS = "guias_exchange";
    public static final String EXCHANGE_DLX = "dlx_exchange_errores";
    public static final String ROUTING_KEY_GUIAS = "routing_guias";
    public static final String ROUTING_KEY_DLX = "routing_dlx_errores";

    @Bean
    public Queue guiasQueue() {
        return new Queue(QUEUE_GUIAS_PRINCIPAL, true, false, false, Map.of(
                "x-dead-letter-exchange", EXCHANGE_DLX,
                "x-dead-letter-routing-key", ROUTING_KEY_DLX));
    }

    @Bean
    public Queue guiasErroresQueue() {
        return new Queue(QUEUE_ERRORES, true);
    }

    @Bean
    public DirectExchange guiasExchange() {
        return new DirectExchange(EXCHANGE_GUIAS);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    @Bean
    public Binding guiasBinding() {
        return BindingBuilder.bind(guiasQueue()).to(guiasExchange()).with(ROUTING_KEY_GUIAS);
    }

    @Bean
    public Binding guiasErroresBinding() {
        return BindingBuilder.bind(guiasErroresQueue()).to(dlxExchange()).with(ROUTING_KEY_DLX);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
