package com.duoc.guias_despacho_cloud.service;

import com.duoc.guias_despacho_cloud.exception.RecursoNoEncontradoException;
import com.duoc.guias_despacho_cloud.modelo.Usuario;
import com.duoc.guias_despacho_cloud.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNombre(usuarioActualizado.getNombre());
                    u.setEmail(usuarioActualizado.getEmail());
                    u.setRol(usuarioActualizado.getRol());
                    u.setDireccion(usuarioActualizado.getDireccion());
                    u.setTelefono(usuarioActualizado.getTelefono());
                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
