package com.app.oslotoilet.toilet;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ToiletRepository extends JpaRepository<Toilet, UUID> {
    List<Toilet> findAllByOrderByAddedAsc();
    List<Toilet> findAllByOrderByAddedDesc();
    List<Toilet> findAllByOrderByUpdatedAtAsc();
    List<Toilet> findAllByOrderByUpdatedAtDesc();
    List<Toilet> findAllByOrderByNameAsc();
    List<Toilet> findAllByOrderByNameDesc();
}
