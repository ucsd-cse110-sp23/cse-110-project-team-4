package org.agilelovers.server.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument,
        String> {

    public Optional<UserDocument> findByUsernameAndPassword(String username,
                                                         String password);
}
