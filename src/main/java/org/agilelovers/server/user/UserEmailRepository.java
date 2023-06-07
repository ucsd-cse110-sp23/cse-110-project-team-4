package org.agilelovers.server.user;

import org.agilelovers.server.user.models.UserEmailConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserEmailRepository extends MongoRepository<UserEmailConfigDocument, String> {

    Optional<UserEmailConfigDocument> findByUserID(String userID);
}
