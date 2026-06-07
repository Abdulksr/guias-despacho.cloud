package com.duoc.guias_despacho_cloud.service;

import com.duoc.guias_despacho_cloud.exception.RecursoNoEncontradoException;
import com.duoc.guias_despacho_cloud.modelo.Producto;
import com.duoc.guias_despacho_cloud.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(p -> {
                    p.setNombre(productoActualizado.getNombre());
                    p.setDescripcion(productoActualizado.getDescripcion());
                    p.setPrecio(productoActualizado.getPrecio());
                    p.setStock(productoActualizado.getStock());
                    return productoRepository.save(p);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
