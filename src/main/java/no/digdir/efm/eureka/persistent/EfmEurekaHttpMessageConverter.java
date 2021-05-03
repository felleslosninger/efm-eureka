package no.digdir.efm.eureka.persistent;

import com.netflix.appinfo.EurekaAccept;
import com.netflix.discovery.converters.wrappers.DecoderWrapper;
import com.netflix.discovery.converters.wrappers.EncoderWrapper;
import com.netflix.eureka.registry.Key;
import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class EfmEurekaHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final HttpServletRequest request;
    private final ServerCodecs serverCodecs;

    public EfmEurekaHttpMessageConverter(HttpServletRequest request, ServerCodecs serverCodecs) {
        super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.ALL);
        this.request = request;
        this.serverCodecs = serverCodecs;
        this.setDefaultCharset(StandardCharsets.UTF_8);
    }

    @Override
    protected boolean supports(@Nonnull Class<?> aClass) {
        return true;
    }

    @Nonnull
    @Override
    protected final Object readInternal(@Nonnull Class<?> clazz, @Nonnull HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
        try (InputStream inputStream = inputMessage.getBody()) {
            return getDecoder().decode(inputStream, clazz);
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e, inputMessage);
        }
    }

    @Override
    protected void writeInternal(@Nonnull Object object, @Nonnull HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
        try (OutputStream outputStream = outputMessage.getBody()) {
            getEncoder().encode(object, outputStream);
        } catch (Exception e) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    private EncoderWrapper getEncoder() {
        String value = request.getHeader(EurekaAccept.HTTP_X_EUREKA_ACCEPT);
        EurekaAccept eurekaAccept = EurekaAccept.fromString(value);
        return serverCodecs.getEncoder(getKeyType(), eurekaAccept);
    }

    private DecoderWrapper getDecoder() {
        return getKeyType() == Key.KeyType.JSON ? serverCodecs.getFullJsonCodec() : serverCodecs.getFullXmlCodec();
    }

    private Key.KeyType getKeyType() {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        if (acceptHeader == null || !acceptHeader.contains("json")) {
            return Key.KeyType.XML;
        }

        return Key.KeyType.JSON;
    }
}
