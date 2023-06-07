package org.agilelovers.server.user;

import org.agilelovers.server.user.models.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface UserRepository extends MongoRepository<UserDocument, String>, UserRepositoryInterface {

    Optional<UserDocument> findByUsernameAndPassword(String username, String password);

    UserDocument saveUsernameAndPassword(String username, String password);
}
