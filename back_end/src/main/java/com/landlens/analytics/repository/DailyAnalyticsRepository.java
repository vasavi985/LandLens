package com.landlens.analytics.repository;

import com.landlens.analytics.model.DailyAnalytics;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyAnalyticsRepository {
    Optional<DailyAnalytics> findById(UUID id);
    List<DailyAnalytics> findAll();
    DailyAnalytics save(DailyAnalytics dailyAnalytics);
    void delete(DailyAnalytics dailyAnalytics);
    void deleteById(UUID id);
    long count();

    Optional<DailyAnalytics> findByAnalyticsDate(LocalDate date);
}
