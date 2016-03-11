package es.uji.al259348.sliwserver.exceptions;

public class NoSuchDeviceException extends Exception {

    public NoSuchDeviceException(String deviceId) {
        super(String.format("El dispositivo %s no existe.", deviceId));
    }

}
