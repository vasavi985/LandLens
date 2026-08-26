package com.landlens.ai.repository;

import com.landlens.ai.model.AiMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiMessageRepository {
    Optional<AiMessage> findById(UUID id);
    List<AiMessage> findAll();
    AiMessage save(AiMessage aiMessage);
    void delete(AiMessage aiMessage);
    void deleteById(UUID id);
    long count();

    List<AiMessage> findByConversationIdAndIsActiveTrueOrderByTimestampAsc(UUID conversationId);
}
