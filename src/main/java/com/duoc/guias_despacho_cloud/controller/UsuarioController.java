package com.duoc.guias_despacho_cloud.controller;

import com.duoc.guias_despacho_cloud.dto.UsuarioDTO;
import com.duoc.guias_despacho_cloud.modelo.Usuario;
import com.duoc.guias_despacho_cloud.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> obtenerPorEmail(@PathVariable String email) {
        return usuarioService.obtenerPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setDireccion(usuarioDTO.getDireccion());
        usuario.setTelefono(usuarioDTO.getTelefono());

        Usuario guardado = usuarioService.guardar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setRol(usuarioDTO.getRol());
            usuario.setDireccion(usuarioDTO.getDireccion());
            usuario.setTelefono(usuarioDTO.getTelefono());

            Usuario actualizado = usuarioService.actualizar(id, usuario);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
