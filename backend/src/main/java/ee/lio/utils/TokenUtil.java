package ee.lio.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class TokenUtil {
    public static String generateToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }

    public static String hashToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}
