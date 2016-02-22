package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.SampleRepository;
import es.uji.al259348.sliwserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SampleServiceImpl implements SampleService {

    @Autowired
    SampleRepository sampleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MLService mlService;

    @Override
    public Sample save(Sample sample) {
        return sampleRepository.save(sample);
    }

    @Override
    public void classify(Sample sample) {
        User user = userRepository.findOne(sample.getUserId());

        String location = mlService.classify(user, sample);
        System.out.println("Classified as: " + location);
        sample.setLocation(location);
    }

}
