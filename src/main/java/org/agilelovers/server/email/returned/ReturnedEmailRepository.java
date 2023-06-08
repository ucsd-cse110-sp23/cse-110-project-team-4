package org.agilelovers.server.email.returned;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface ReturnedEmailRepository  extends MongoRepository<ReturnedEmailDocument, String> {

    Optional<List<ReturnedEmailDocument>> findAllByUserId(String userID);

    Optional<ReturnedEmailDocument> findById(String Id);
}
