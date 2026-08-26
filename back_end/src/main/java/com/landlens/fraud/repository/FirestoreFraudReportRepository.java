package com.landlens.fraud.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.fraud.model.FraudReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreFraudReportRepository extends AbstractFirestoreRepository<FraudReport> implements FraudReportRepository {

    public FirestoreFraudReportRepository() {
        super("fraud_reports", FraudReport.class);
    }

    @Override
    public List<FraudReport> findByPropertyId(UUID propertyId) {
        if (propertyId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("property.id", propertyId.toString())
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching fraud reports by property: " + propertyId, e);
        }
    }

    @Override
    public List<FraudReport> findByStatus(String status) {
        if (status == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("status", status.toUpperCase())
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching fraud reports by status: " + status, e);
        }
    }

    @Override
    public List<FraudReport> findByOfficerId(UUID officerId) {
        if (officerId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("officer.id", officerId.toString())
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching fraud reports by officer: " + officerId, e);
        }
    }
}
