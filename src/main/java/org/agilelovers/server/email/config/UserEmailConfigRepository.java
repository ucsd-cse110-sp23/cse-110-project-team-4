package org.agilelovers.server.email.config;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserEmailConfigRepository extends MongoRepository<UserEmailConfigDocument, String> {

    Optional<UserEmailConfigDocument> findByUserID(String userID);
}
