package org.agilelovers.server.command;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface CommandRepository extends MongoRepository<CommandDocument,
        String> {

    Optional<List<CommandDocument>> findAllByUserId(String userId);
}
