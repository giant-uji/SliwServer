package es.uji.al259348.sliwserver.repositories;

import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import java.util.List;

public interface SampleRepository extends ElasticsearchCrudRepository<Sample, String> {

    List<Sample> findByUser(User user);
    List<Sample> findByUserAndValid(User user, boolean valid);

}
