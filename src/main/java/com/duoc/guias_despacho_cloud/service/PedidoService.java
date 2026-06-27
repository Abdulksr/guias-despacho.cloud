package com.duoc.guias_despacho_cloud.service;

import com.duoc.guias_despacho_cloud.exception.RecursoNoEncontradoException;
import com.duoc.guias_despacho_cloud.exception.StockInsuficienteException;
import com.duoc.guias_despacho_cloud.modelo.DetallePedido;
import com.duoc.guias_despacho_cloud.modelo.Pedido;
import com.duoc.guias_despacho_cloud.modelo.Producto;
import com.duoc.guias_despacho_cloud.repository.PedidoRepository;
import com.duoc.guias_despacho_cloud.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPorUsuarioId(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> obtenerPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        double total = 0;
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id: " + detalle.getProducto().getId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto: " + producto.getNombre()
                        + ". Disponible: " + producto.getStock() + ", solicitado: " + detalle.getCantidad());
            }

            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * detalle.getCantidad());
            detalle.setPedido(pedido);

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            total += detalle.getSubtotal();
        }
        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");

        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id)
                .map(p -> {
                    p.setEstado(nuevoEstado);
                    return pedidoRepository.save(p);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
