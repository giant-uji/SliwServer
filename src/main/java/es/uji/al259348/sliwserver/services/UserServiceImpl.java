package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Device;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.DeviceRepository;
import es.uji.al259348.sliwserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public User getUserLinkedTo(String deviceId) {
        Device device = deviceRepository.findOne(deviceId);
        return (device != null) ? device.getUser() : null;
    }

}
