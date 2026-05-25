package com.app.oslotoilet.toilet;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToiletRepository extends JpaRepository<Toilet, UUID> {
}
