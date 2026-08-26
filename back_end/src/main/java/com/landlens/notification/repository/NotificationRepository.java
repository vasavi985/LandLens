package com.landlens.notification.repository;

import com.landlens.notification.model.Notification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Optional<Notification> findById(UUID id);
    List<Notification> findAll();
    Notification save(Notification notification);
    void delete(Notification notification);
    void deleteById(UUID id);
    long count();

    List<Notification> findByReceiverIdAndIsActiveTrueOrderByCreatedTimeDesc(UUID receiverId);
}
