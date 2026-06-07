package com.duoc.guias_despacho_cloud.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "guias_despacho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "numero_guia", nullable = false, unique = true)
    private String numeroGuia;

    @Column(name = "direccion_destino", nullable = false)
    private String direccionDestino;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "transportista_id", nullable = false)
    private Transportista transportista;

    @Column(name = "s3_key")
    private String s3Key;
}
