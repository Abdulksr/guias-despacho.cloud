package com.duoc.guias_despacho_cloud.service;

import com.duoc.guias_despacho_cloud.exception.RecursoNoEncontradoException;
import com.duoc.guias_despacho_cloud.modelo.Transportista;
import com.duoc.guias_despacho_cloud.repository.TransportistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportistaService {

    @Autowired
    private TransportistaRepository transportistaRepository;

    public List<Transportista> obtenerTodos() {
        return transportistaRepository.findAll();
    }

    public Optional<Transportista> obtenerPorId(Long id) {
        return transportistaRepository.findById(id);
    }

    public Optional<Transportista> obtenerPorRut(String rut) {
        return transportistaRepository.findByRut(rut);
    }

    public Transportista guardar(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    public Transportista actualizar(Long id, Transportista transportistaActualizado) {
        return transportistaRepository.findById(id)
                .map(t -> {
                    t.setNombre(transportistaActualizado.getNombre());
                    t.setRut(transportistaActualizado.getRut());
                    t.setTelefono(transportistaActualizado.getTelefono());
                    t.setVehiculo(transportistaActualizado.getVehiculo());
                    t.setPatenteVehiculo(transportistaActualizado.getPatenteVehiculo());
                    return transportistaRepository.save(t);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Transportista no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        transportistaRepository.deleteById(id);
    }
}
