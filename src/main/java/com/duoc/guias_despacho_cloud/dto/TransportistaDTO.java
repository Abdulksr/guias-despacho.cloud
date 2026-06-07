package com.duoc.guias_despacho_cloud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportistaDTO {

    private Long id;

    @NotBlank(message = "El nombre del transportista es obligatorio")
    private String nombre;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    private String telefono;

    @NotBlank(message = "El vehículo es obligatorio")
    private String vehiculo;

    @NotBlank(message = "La patente del vehículo es obligatoria")
    private String patenteVehiculo;
}
