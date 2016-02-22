package es.uji.al259348.sliwserver.repositories;

import es.uji.al259348.sliwserver.model.Sample;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleRepository extends ElasticsearchCrudRepository<Sample, String> {

    List<Sample> findByUserId(String userId);
    List<Sample> findByUserIdAndValid(String userId, boolean valid);

}
