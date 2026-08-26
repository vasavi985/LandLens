package com.landlens.analytics.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.analytics.model.DailyAnalytics;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class FirestoreDailyAnalyticsRepository extends AbstractFirestoreRepository<DailyAnalytics> implements DailyAnalyticsRepository {

    public FirestoreDailyAnalyticsRepository() {
        super("daily_analytics", DailyAnalytics.class);
    }

    @Override
    public Optional<DailyAnalytics> findByAnalyticsDate(LocalDate date) {
        if (date == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("analyticsDate", date.toString())
                    .limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching daily analytics by date: " + date, e);
        }
        return Optional.empty();
    }
}
