package com.duoc.guias_despacho_cloud.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transportistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String rut;

    private String telefono;

    @Column(nullable = false)
    private String vehiculo;

    @Column(name = "patente_vehiculo", nullable = false)
    private String patenteVehiculo;
}
