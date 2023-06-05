package org.agilelovers.server.question;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface QuestionRepository extends MongoRepository<QuestionDocument,
        String> {

    Optional<List<QuestionDocument>> findAllByUserId(String userId);
}
