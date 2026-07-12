package com.duoc.guias_despacho_cloud.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoc.guias_despacho_cloud.config.RabbitMQConfig;
import com.duoc.guias_despacho_cloud.modelo.GuiaDespachoProcesada;
import com.duoc.guias_despacho_cloud.repository.GuiaDespachoProcesadoRepository;

@Service
public class ConsumidorService {

    @Autowired
    private GuiaDespachoProcesadoRepository guiaDespachoProcesadoRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_GUIAS_PRINCIPAL)
    public void procesarGuiaDespacho(String contenido) {
        System.out.println("Mensaje recibido desde RabbitMQ en ConsumidorService:\n" + contenido);

        // Metodo solo para probar la cola de errores
        if (contenido.contains("ERROR")) {
            System.out.println("ERROR en la guía de despacho");
            throw new RuntimeException("Simulando falla");
        }

        GuiaDespachoProcesada guiaProcesada = new GuiaDespachoProcesada();
        guiaProcesada.setContenidoGuia(contenido);
        guiaProcesada.setFechaHoraProcesamiento(LocalDateTime.now());
        guiaProcesada.setEstado("PROCESADA");
        guiaDespachoProcesadoRepository.save(guiaProcesada);
        System.out.println("Guía de despacho procesada correctamente");

    }

}
