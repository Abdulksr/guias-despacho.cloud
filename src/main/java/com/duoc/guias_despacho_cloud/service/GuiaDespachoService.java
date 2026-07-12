package com.duoc.guias_despacho_cloud.service;

import com.duoc.guias_despacho_cloud.config.RabbitMQConfig;
import com.duoc.guias_despacho_cloud.exception.RecursoDuplicadoException;
import com.duoc.guias_despacho_cloud.exception.RecursoNoEncontradoException;
import com.duoc.guias_despacho_cloud.modelo.GuiaDespacho;
import com.duoc.guias_despacho_cloud.modelo.Pedido;
import com.duoc.guias_despacho_cloud.modelo.Transportista;
import com.duoc.guias_despacho_cloud.repository.GuiaDespachoRepository;
import com.duoc.guias_despacho_cloud.repository.PedidoRepository;
import com.duoc.guias_despacho_cloud.repository.S3Repository;
import com.duoc.guias_despacho_cloud.repository.TransportistaRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GuiaDespachoService {

    @Autowired
    private S3Repository s3Repository;

    @Value("${efs.path}")
    private String efsPath;

    @Autowired
    private GuiaDespachoRepository guiaDespachoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private TransportistaRepository transportistaRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public List<GuiaDespacho> obtenerTodas() {
        return guiaDespachoRepository.findAll();
    }

    public Optional<GuiaDespacho> obtenerPorId(Long id) {
        return guiaDespachoRepository.findById(id);
    }

    public Optional<GuiaDespacho> obtenerPorNumeroGuia(String numeroGuia) {
        return guiaDespachoRepository.findByNumeroGuia(numeroGuia);
    }

    public Optional<GuiaDespacho> obtenerPorPedidoId(Long pedidoId) {
        return guiaDespachoRepository.findByPedidoId(pedidoId);
    }

    public List<GuiaDespacho> obtenerPorTransportistaYFecha(Long transportistaId, LocalDate fecha) {
        return guiaDespachoRepository.findByTransportistaIdAndFechaEmision(transportistaId, fecha);
    }

    @Transactional
    public GuiaDespacho generarGuia(Long pedidoId, Long transportistaId)
            throws FileNotFoundException, DocumentException {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con id: " + pedidoId));

        Transportista transportista = transportistaRepository.findById(transportistaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Transportista no encontrado con id: " + transportistaId));

        if (guiaDespachoRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe una guía de despacho para el pedido: " + pedidoId);
        }

        GuiaDespacho guia = new GuiaDespacho();
        guia.setPedido(pedido);
        guia.setTransportista(transportista);
        guia.setNumeroGuia("GD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        guia.setDireccionDestino(pedido.getUsuario().getDireccion());
        guia.setFechaEmision(LocalDate.now());
        guia.setEstado("GENERADA");

        pedido.setEstado("DESPACHADO");
        pedidoRepository.save(pedido);

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("      GUÍA DE DESPACHO: ").append(guia.getNumeroGuia()).append("\n");
        sb.append("=========================================\n");
        sb.append("Fecha de Emisión: ").append(guia.getFechaEmision()).append("\n");
        sb.append("Usuario Destino: ").append(pedido.getUsuario().getNombre()).append("\n");
        sb.append("Dirección: ").append(guia.getDireccionDestino()).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append("DATOS DEL TRANSPORTISTA:\n");
        sb.append("Nombre: ").append(transportista.getNombre()).append("\n");
        sb.append("RUT: ").append(transportista.getRut()).append("\n");
        sb.append("Vehículo: ").append(transportista.getVehiculo()).append(" (Patente: ")
                .append(transportista.getPatenteVehiculo()).append(")\n");
        sb.append("-----------------------------------------\n");
        sb.append("DETALLE DE CARGA:\n");

        pedido.getDetalles().forEach(detalle -> sb.append("- ").append(detalle.getProducto().getNombre())
                .append(" | Cantidad: ").append(detalle.getCantidad())
                .append(" | Subtotal: $").append(detalle.getSubtotal()).append("\n"));

        sb.append("-----------------------------------------\n");
        sb.append("VALOR DECLARADO TOTAL: $").append(pedido.getTotal()).append("\n");
        sb.append("=========================================\n");

        String contenido = sb.toString();

        File directorioEfs = new File(efsPath);
        if (!directorioEfs.exists()) {
            directorioEfs.mkdirs();
        }

        String nombreArchivo = guia.getNumeroGuia() + ".pdf";
        File archivoLocal = new File(directorioEfs, nombreArchivo);

        Document pdf = new Document();
        PdfWriter.getInstance(pdf, new FileOutputStream(archivoLocal));
        pdf.open();
        Font fuente = FontFactory.getFont(FontFactory.COURIER, 12);
        pdf.add(new Paragraph(contenido, fuente));
        pdf.close();

        String fecha = guia.getFechaEmision().toString();
        String nombreTranspSinEspacios = transportista.getNombre().replaceAll("\\s+", "");
        String s3Key = fecha + "/" + nombreTranspSinEspacios + "/" + nombreArchivo;

        guia.setS3Key(s3Key);

        s3Repository.uploadFile(s3Key, archivoLocal);

        pedido.setEstado("DESPACHADO");
        pedidoRepository.save(pedido);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_GUIAS, RabbitMQConfig.ROUTING_KEY_GUIAS, contenido);

        return guiaDespachoRepository.save(guia);
    }

    public GuiaDespacho actualizarEstado(Long id, String nuevoEstado) {
        return guiaDespachoRepository.findById(id)
                .map(g -> {
                    g.setEstado(nuevoEstado);
                    if ("ENTREGADA".equals(nuevoEstado)) {
                        g.setFechaEntrega(LocalDate.now());
                    }
                    return guiaDespachoRepository.save(g);
                })
                .orElseThrow(() -> new RecursoNoEncontradoException("Guía no encontrada con id: " + id));
    }

    public void eliminar(Long id) {
        guiaDespachoRepository.deleteById(id);
    }

    public byte[] descargarGuia(Long id) throws IOException {
        GuiaDespacho guia = guiaDespachoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Guía no encontrada con id: " + id));
        String s3Key = guia.getS3Key();
        return s3Repository.downloadFile(s3Key);
    }

    public GuiaDespacho actualizarGuia(Long id, String nuevoContenido) throws IOException, DocumentException {
        GuiaDespacho guia = guiaDespachoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Guía no encontrada con id: " + id));

        File directorioEfs = new File(efsPath);
        if (!directorioEfs.exists()) {
            directorioEfs.mkdirs();
        }

        String nombreArchivo = guia.getNumeroGuia() + ".pdf";
        File archivoLocal = new File(directorioEfs, nombreArchivo);

        Document pdf = new Document();
        PdfWriter.getInstance(pdf, new FileOutputStream(archivoLocal));
        pdf.open();
        Font fuente = FontFactory.getFont(FontFactory.COURIER, 12);
        pdf.add(new Paragraph(nuevoContenido, fuente));
        pdf.close();

        s3Repository.uploadFile(guia.getS3Key(), archivoLocal);

        return guiaDespachoRepository.save(guia);
    }

    public void eliminarGuia(Long id) {
        GuiaDespacho guia = guiaDespachoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Guía no encontrada con id: " + id));
        s3Repository.deleteObject(guia.getS3Key());

        File archivoLocal = new File(efsPath + guia.getS3Key() + ".pdf");
        if (archivoLocal.exists()) {
            archivoLocal.delete();
        }

        guiaDespachoRepository.deleteById(id);
    }

}
