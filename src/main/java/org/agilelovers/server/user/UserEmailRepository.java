package org.agilelovers.server.user;

import io.swagger.annotations.ApiParam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserEmailRepository extends MongoRepository<UserEmailDocument, String> {

    Optional<UserEmailDocument> findByUserID(String userID);
}
