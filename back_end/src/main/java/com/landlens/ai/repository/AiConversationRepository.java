package com.landlens.ai.repository;

import com.landlens.ai.model.AiConversation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiConversationRepository {
    Optional<AiConversation> findById(UUID id);
    List<AiConversation> findAll();
    AiConversation save(AiConversation aiConversation);
    void delete(AiConversation aiConversation);
    void deleteById(UUID id);
    long count();

    List<AiConversation> findByUserIdAndIsActiveTrue(UUID userId);
}
