package com.duoc.guias_despacho_cloud.controller;

import com.duoc.guias_despacho_cloud.modelo.GuiaDespacho;
import com.duoc.guias_despacho_cloud.service.GuiaDespachoService;
import com.itextpdf.text.DocumentException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/guias-despacho")
public class GuiaDespachoController {

    @Autowired
    private GuiaDespachoService guiaDespachoService;

    @GetMapping
    public ResponseEntity<List<GuiaDespacho>> obtenerTodas() {
        return ResponseEntity.ok(guiaDespachoService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiaDespacho> obtenerPorId(@PathVariable Long id) {
        return guiaDespachoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroGuia}")
    public ResponseEntity<GuiaDespacho> obtenerPorNumeroGuia(@PathVariable String numeroGuia) {
        return guiaDespachoService.obtenerPorNumeroGuia(numeroGuia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<GuiaDespacho> obtenerPorPedidoId(@PathVariable Long pedidoId) {
        return guiaDespachoService.obtenerPorPedidoId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transportista/{transportistaId}/fecha/{fecha}")
    public ResponseEntity<List<GuiaDespacho>> obtenerPorTransportistaYFecha(@PathVariable Long transportistaId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(guiaDespachoService.obtenerPorTransportistaYFecha(transportistaId, fecha));
    }

    @GetMapping("/{id}/guias")
    public ResponseEntity<ByteArrayResource> descargarGuia(@PathVariable Long id) {
        try {
            byte[] data = guiaDespachoService.descargarGuia(id);
            return ResponseEntity.ok(new ByteArrayResource(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generar/{pedidoId}")
    public ResponseEntity<GuiaDespacho> generarGuia(@PathVariable Long pedidoId, @RequestParam Long transportistaId)
            throws FileNotFoundException, DocumentException {
        GuiaDespacho guia = guiaDespachoService.generarGuia(pedidoId, transportistaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(guia);

    }

    @PutMapping("/{id}/guia")
    public ResponseEntity<GuiaDespacho> actualizarGuia(@PathVariable Long id, @RequestBody String nuevoContenido) {
        try {
            GuiaDespacho actualizada = guiaDespachoService.actualizarGuia(id, nuevoContenido);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGuia(@PathVariable Long id) {
        guiaDespachoService.eliminarGuia(id);
        return ResponseEntity.noContent().build();
    }
}
