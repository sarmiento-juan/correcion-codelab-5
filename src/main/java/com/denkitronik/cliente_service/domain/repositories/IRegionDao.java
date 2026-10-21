package com.denkitronik.cliente_service.domain.repositories;

import com.denkitronik.cliente_service.domain.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRegionDao extends JpaRepository<Region, Long> {
}
