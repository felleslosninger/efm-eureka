package no.digdir.efm.eureka.persistent;

public class EfmEurekaException extends RuntimeException {

    public EfmEurekaException() {
    }

    public EfmEurekaException(String message) {
        super(message);
    }

    public EfmEurekaException(String message, Throwable cause) {
        super(message, cause);
    }

    public EfmEurekaException(Throwable cause) {
        super(cause);
    }

    public EfmEurekaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
