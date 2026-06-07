package com.duoc.guias_despacho_cloud.controller;

import com.duoc.guias_despacho_cloud.dto.DetallePedidoDTO;
import com.duoc.guias_despacho_cloud.dto.PedidoDTO;
import com.duoc.guias_despacho_cloud.modelo.Cliente;
import com.duoc.guias_despacho_cloud.modelo.DetallePedido;
import com.duoc.guias_despacho_cloud.modelo.Pedido;
import com.duoc.guias_despacho_cloud.modelo.Producto;
import com.duoc.guias_despacho_cloud.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> obtenerPorClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.obtenerPorClienteId(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoDTO pedidoDTO) {
        try {
            Pedido pedido = new Pedido();

            Cliente cliente = new Cliente();
            cliente.setId(pedidoDTO.getClienteId());
            pedido.setCliente(cliente);

            pedido.setFechaPedido(LocalDate.now());

            List<DetallePedido> detalles = new ArrayList<>();
            for (DetallePedidoDTO detalleDTO : pedidoDTO.getDetalles()) {
                DetallePedido detalle = new DetallePedido();
                Producto producto = new Producto();
                producto.setId(detalleDTO.getProductoId());
                detalle.setProducto(producto);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalles.add(detalle);
            }
            pedido.setDetalles(detalles);

            Pedido creado = pedidoService.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            Pedido actualizado = pedidoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
