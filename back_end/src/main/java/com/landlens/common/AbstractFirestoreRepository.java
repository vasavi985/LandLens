package com.landlens.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractFirestoreRepository<T extends BaseAuditEntity> {

    @Autowired
    protected Firestore firestore;

    @Autowired
    protected ObjectMapper objectMapper;

    protected final String collectionName;
    protected final Class<T> entityClass;

    protected AbstractFirestoreRepository(String collectionName, Class<T> entityClass) {
        this.collectionName = collectionName;
        this.entityClass = entityClass;
    }

    protected CollectionReference getCollection() {
        return firestore.collection(collectionName);
    }

    public Optional<T> findById(UUID id) {
        if (id == null) return Optional.empty();
        return findById(id.toString());
    }

    public Optional<T> findById(String id) {
        if (id == null) return Optional.empty();
        try {
            DocumentSnapshot doc = getCollection().document(id).get().get();
            if (doc.exists()) {
                return Optional.ofNullable(mapToObject(doc.getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading document " + id + " from Firestore collection " + collectionName, e);
        }
        return Optional.empty();
    }

    public List<T> findAll() {
        try {
            QuerySnapshot snapshot = getCollection().get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error reading all documents from Firestore collection " + collectionName, e);
        }
    }

    public T save(T entity) {
        if (entity == null) return null;
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        try {
            Map<String, Object> map = objectToMap(entity);
            getCollection().document(entity.getId().toString()).set(map).get();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving document to Firestore collection " + collectionName, e);
        }
    }

    public void delete(T entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    public void deleteById(UUID id) {
        if (id == null) return;
        try {
            getCollection().document(id.toString()).delete().get();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting document " + id + " from Firestore collection " + collectionName, e);
        }
    }

    public void deleteAll() {
        try {
            QuerySnapshot snapshot = getCollection().get().get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                doc.getReference().delete().get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting all documents from Firestore collection " + collectionName, e);
        }
    }

    public long count() {
        try {
            return getCollection().get().get().size();
        } catch (Exception e) {
            throw new RuntimeException("Error counting documents in Firestore collection " + collectionName, e);
        }
    }

    protected T mapToObject(Map<String, Object> map) {
        if (map == null) return null;
        try {
            return objectMapper.convertValue(map, entityClass);
        } catch (Exception e) {
            return null;
        }
    }

    protected Map<String, Object> objectToMap(T entity) {
        if (entity == null) return null;
        try {
            return objectMapper.convertValue(entity, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to map", e);
        }
    }
}
