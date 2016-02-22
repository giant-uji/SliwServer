package es.uji.al259348.sliwserver.repositories;

import es.uji.al259348.sliwserver.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ElasticsearchCrudRepository<User, String> {
}
