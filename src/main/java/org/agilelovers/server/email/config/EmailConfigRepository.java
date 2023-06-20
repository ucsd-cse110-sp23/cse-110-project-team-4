package org.agilelovers.server.email.config;

import org.agilelovers.common.documents.EmailConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface EmailConfigRepository extends MongoRepository<EmailConfigDocument, String> {
    Optional<EmailConfigDocument> findByUserID(String userID);
}
