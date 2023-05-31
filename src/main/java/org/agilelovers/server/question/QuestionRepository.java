package org.agilelovers.server.question;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends MongoRepository<QuestionDocument,
        String> {

    public Optional<QuestionDocument> findByUserId(String userId);

    public Optional<List<QuestionDocument>> findAllByUserId(String userId);
}
