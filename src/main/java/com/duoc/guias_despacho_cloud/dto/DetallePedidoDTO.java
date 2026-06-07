package com.duoc.guias_despacho_cloud.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {

    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private Double precioUnitario;

    private Double subtotal;
}
