package com.duoc.guias_despacho_cloud.controller;

import com.duoc.guias_despacho_cloud.dto.TransportistaDTO;
import com.duoc.guias_despacho_cloud.modelo.Transportista;
import com.duoc.guias_despacho_cloud.service.TransportistaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
public class TransportistaController {

    @Autowired
    private TransportistaService transportistaService;

    @GetMapping
    public ResponseEntity<List<Transportista>> obtenerTodos() {
        return ResponseEntity.ok(transportistaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transportista> obtenerPorId(@PathVariable Long id) {
        return transportistaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Transportista> obtenerPorRut(@PathVariable String rut) {
        return transportistaService.obtenerPorRut(rut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transportista> crear(@Valid @RequestBody TransportistaDTO dto) {
        Transportista transportista = new Transportista();
        transportista.setNombre(dto.getNombre());
        transportista.setRut(dto.getRut());
        transportista.setTelefono(dto.getTelefono());
        transportista.setVehiculo(dto.getVehiculo());
        transportista.setPatenteVehiculo(dto.getPatenteVehiculo());

        Transportista guardado = transportistaService.guardar(transportista);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transportista> actualizar(@PathVariable Long id, @Valid @RequestBody TransportistaDTO dto) {
        Transportista transportista = new Transportista();
        transportista.setNombre(dto.getNombre());
        transportista.setRut(dto.getRut());
        transportista.setTelefono(dto.getTelefono());
        transportista.setVehiculo(dto.getVehiculo());
        transportista.setPatenteVehiculo(dto.getPatenteVehiculo());

        Transportista actualizado = transportistaService.actualizar(id, transportista);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transportistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
