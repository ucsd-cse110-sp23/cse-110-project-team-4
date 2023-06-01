package org.agilelovers.server.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends MongoRepository<UserDocument,
        String> {

    Optional<UserDocument> findByUsernameAndPassword(String username,
                                                         String password);
}
