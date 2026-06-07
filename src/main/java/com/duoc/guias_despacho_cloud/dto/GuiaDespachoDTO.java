package com.duoc.guias_despacho_cloud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespachoDTO {

    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    private String numeroGuia;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    private LocalDate fechaEmision;

    private LocalDate fechaEntrega;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotNull(message = "El ID del transportista es obligatorio")
    private Long transportistaId;

    @NotNull(message = "La clave S3 es obligatoria")
    private String s3Key;

}
