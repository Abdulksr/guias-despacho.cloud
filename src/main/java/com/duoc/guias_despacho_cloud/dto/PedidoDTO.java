package com.duoc.guias_despacho_cloud.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String usuarioNombre;

    private LocalDate fechaPedido;

    private String estado;

    private Double total;

    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    @Valid
    private List<DetallePedidoDTO> detalles;
}
