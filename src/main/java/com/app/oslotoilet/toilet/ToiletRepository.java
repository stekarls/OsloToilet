package com.app.oslotoilet.toilet;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    //Loads every toilet together with its opening hours in one query, instead of one extra query per toilet
    @Query("SELECT DISTINCT t FROM Toilet t LEFT JOIN FETCH t.openingHours")
    List<Toilet> findAllWithOpeningHours();
}
