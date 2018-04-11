package top.yeonon.lmmall.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 20:04
 **/
@Component
public class JwtTokenGenerator implements TokenGenerator<String> {

    private static final String SECRET = "LMMall_JWT_TOKEN_SECRET";

    @Override
    public String generate(String authorization) throws Exception {
        Date iatDate = new Date();
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 30);
        Date expireTime = nowTime.getTime();

        Map<String, Object> map = Maps.newHashMap();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        String token = JWT.create()
                          .withHeader(map)
                          .withClaim("authorization", authorization)
                          .withExpiresAt(expireTime)
                          .withIssuedAt(iatDate)
                          .sign(Algorithm.HMAC256(SECRET));
        return token;
    }

    @Override
    public boolean verifyToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }
        return true;
    }
}
