package com.duoc.guias_despacho_cloud.service;

import com.duoc.guias_despacho_cloud.exception.RecursoNoEncontradoException;
import com.duoc.guias_despacho_cloud.modelo.Cliente;
import com.duoc.guias_despacho_cloud.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        return clienteRepository.findById(id)
                .map(c -> {
                    c.setNombre(clienteActualizado.getNombre());
                    c.setEmail(clienteActualizado.getEmail());
                    c.setDireccion(clienteActualizado.getDireccion());
                    c.setTelefono(clienteActualizado.getTelefono());
                    return clienteRepository.save(c);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
