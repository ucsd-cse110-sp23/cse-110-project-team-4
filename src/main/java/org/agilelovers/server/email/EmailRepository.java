package org.agilelovers.server.email;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface EmailRepository extends MongoRepository<EmailDocument, String> {
    Optional<List<EmailDocument>> findAllByUserId(String userID);

    Optional<EmailDocument> findByid(String id);

}
