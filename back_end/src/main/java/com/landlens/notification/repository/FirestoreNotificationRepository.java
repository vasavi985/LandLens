package com.landlens.notification.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.notification.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreNotificationRepository extends AbstractFirestoreRepository<Notification> implements NotificationRepository {

    public FirestoreNotificationRepository() {
        super("notifications", Notification.class);
    }

    @Override
    public List<Notification> findByReceiverIdAndIsActiveTrueOrderByCreatedTimeDesc(UUID receiverId) {
        if (receiverId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("receiver.id", receiverId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .filter(java.util.Objects::nonNull)
                    .sorted((n1, n2) -> {
                        if (n1.getCreatedTime() == null && n2.getCreatedTime() == null) return 0;
                        if (n1.getCreatedTime() == null) return 1;
                        if (n2.getCreatedTime() == null) return -1;
                        return n2.getCreatedTime().compareTo(n1.getCreatedTime());
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching notifications for receiver: " + receiverId, e);
        }
    }
}
