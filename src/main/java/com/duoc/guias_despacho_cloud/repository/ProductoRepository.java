package com.duoc.guias_despacho_cloud.repository;

import com.duoc.guias_despacho_cloud.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
