package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Device;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.DeviceRepository;
import es.uji.al259348.sliwserver.repositories.SampleRepository;
import es.uji.al259348.sliwserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    SampleRepository sampleRepository;

    @Autowired
    MLService mlService;

    @Override
    public User getUser(String id) {
        return userRepository.findOne(id);
    }

    @Override
    public User getUserLinkedTo(String deviceId) {
        Device device = deviceRepository.findOne(deviceId);
        return (device != null) ? device.getUser() : null;
    }

    @Override
    public void configure(User user, List<Sample> samples) {
        samples.forEach(sampleRepository::save);

        List<String> bssids = getBssids(samples);
        user.setBssids(bssids);

        List<Classifier> classifiers = mlService.buildClassifiers(user, samples);
        user.setClassifiers(MLServiceImpl.toBase64(classifiers));

        user.setConfigured(true);

        userRepository.save(user);
    }

    private static List<String> getBssids(List<Sample> samples) {
        return samples.stream()
                .flatMap(sample -> sample.getScanResults().stream())
                .map(wifiScanResult -> wifiScanResult.BSSID)
                .distinct()
                .collect(Collectors.toList());
    }

}
