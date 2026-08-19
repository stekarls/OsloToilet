package com.app.oslotoilet.openingHours;

import com.app.oslotoilet.toilet.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface OpeningHoursRepository extends JpaRepository<OpeningHours, UUID> {

    List<OpeningHours> findByToiletOrderByDayOfWeekAsc(Toilet toilet);

    boolean existsByToiletAndDayOfWeek(Toilet toilet, DayOfWeek dayOfWeek);
}
