package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Device;
import es.uji.al259348.sliwserver.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public Device findOne(String deviceId) {
        return deviceRepository.findOne(deviceId);
    }

}
