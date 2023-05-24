package org.agilelovers.server;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface QuestionRepository extends MongoRepository<QuestionItem,
        String> {

    @Query('{}')
}
