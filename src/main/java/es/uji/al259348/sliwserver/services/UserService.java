package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.exceptions.NoSuchDeviceException;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;

import java.util.List;

public interface UserService {

    User getUser(String id);
    User getUserLinkedTo(String mac) throws NoSuchDeviceException;

    void configure(User user, List<Sample> samples);
    void buildClassifiers(String userId);
}
