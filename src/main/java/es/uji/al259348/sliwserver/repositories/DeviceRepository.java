package es.uji.al259348.sliwserver.repositories;

import es.uji.al259348.sliwserver.model.Device;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends ElasticsearchCrudRepository<Device, String> {

    @Query("{ \"filtered\" : { \"filter\" : { \"term\" : { \"mac\" : \"?0\" } } } }")
    Device findOneByMac(String mac);

}
