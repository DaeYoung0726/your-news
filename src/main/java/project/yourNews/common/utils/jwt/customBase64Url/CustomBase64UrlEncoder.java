package project.yourNews.common.utils.jwt.customBase64Url;

import io.jsonwebtoken.io.Encoder;

import java.io.OutputStream;
import java.util.Base64;

public class CustomBase64UrlEncoder implements Encoder<OutputStream, OutputStream> {

    @Override
    public OutputStream encode(OutputStream outputStream) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.wrap(outputStream);
    }
}