package no.digdir.efm.eureka.persistent;

import com.netflix.discovery.converters.wrappers.DecoderWrapper;
import com.netflix.discovery.converters.wrappers.EncoderWrapper;
import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class EfmEurekaHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {

    private final ServerCodecs serverCodecs;

    public EfmEurekaHttpMessageConverter(ServerCodecs serverCodecs) {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.serverCodecs = serverCodecs;
        this.setDefaultCharset(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        return readInternal(contextClass, inputMessage);
    }

    @Override
    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        try (InputStream inputStream = inputMessage.getBody()) {
            return getDecoder().decode(inputStream, clazz);
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e, inputMessage);
        }
    }

    @Override
    protected void writeInternal(Object o, Type type, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        try (OutputStream outputStream = outputMessage.getBody()) {
            getEncoder().encode(o, outputStream);
        } catch (Exception e) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    private EncoderWrapper getEncoder() {
        return serverCodecs.getFullJsonCodec();
    }

    private DecoderWrapper getDecoder() {
        return serverCodecs.getFullJsonCodec();
    }
}
