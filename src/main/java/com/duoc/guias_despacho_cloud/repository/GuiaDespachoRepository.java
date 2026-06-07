package com.duoc.guias_despacho_cloud.repository;

import com.duoc.guias_despacho_cloud.modelo.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    Optional<GuiaDespacho> findByNumeroGuia(String numeroGuia);

    Optional<GuiaDespacho> findByPedidoId(Long pedidoId);

    List<GuiaDespacho> findByTransportistaIdAndFechaEmision(Long transportistaId, LocalDate fechaEmision);
}
