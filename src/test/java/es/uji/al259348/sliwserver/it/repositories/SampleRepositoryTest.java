package es.uji.al259348.sliwserver.it.repositories;

import es.uji.al259348.sliwserver.Main;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.repositories.SampleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Main.class)
public class SampleRepositoryTest {

    @Autowired
    SampleRepository sampleRepository;

    @Test
    public void save() {

        Sample sample = new Sample();
        sample = sampleRepository.save(sample);

        assertNotNull(sample);
        assertNotNull(sample.getId());

    }

}