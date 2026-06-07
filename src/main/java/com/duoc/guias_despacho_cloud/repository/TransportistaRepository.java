package com.duoc.guias_despacho_cloud.repository;

import com.duoc.guias_despacho_cloud.modelo.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {

    Optional<Transportista> findByRut(String rut);
}
