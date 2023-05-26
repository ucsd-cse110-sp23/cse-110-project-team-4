package org.agilelovers.server.command;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommandRepository extends MongoRepository<CommandDocument,
        String> {

    public Optional<CommandDocument> findByUserId(String userId);

    public Optional<List<CommandDocument>> findAllByUserId(String userId);
}
