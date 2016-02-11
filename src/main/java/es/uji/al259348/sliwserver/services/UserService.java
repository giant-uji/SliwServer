package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.User;

public interface UserService {

    User getUserLinkedTo(String deviceId);
}
