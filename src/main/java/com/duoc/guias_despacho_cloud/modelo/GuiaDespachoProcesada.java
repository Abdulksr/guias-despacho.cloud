package com.duoc.guias_despacho_cloud.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GUIAS_PROCESADAS_MQ")
@Entity
public class GuiaDespachoProcesada implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contenido_guia", length = 4000, nullable = false)
    private String contenidoGuia;

    @Column(name = "fecha_hora_procesamiento", nullable = false)
    private LocalDateTime fechaHoraProcesamiento;

    @Column(name = "estado", nullable = false)
    private String estado;

}
