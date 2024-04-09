package com.arextest.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTServiceImpl implements JWTService {

  private long accessExpireTime;
  private long refreshExpireTime;
  private String tokenSecret;

  @Override
  public String makeAccessToken(String username) {
    Date date = new Date(System.currentTimeMillis() + accessExpireTime);
    Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim("username", username)
        .sign(algorithm);
  }

  @Override
  public String makeAccessToken(String username, long expireTime) {
    Date date = new Date(System.currentTimeMillis() + expireTime);
    Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim("username", username)
        .sign(algorithm);
  }

  @Override
  public String makeRefreshToken(String username) {
    Date date = new Date(System.currentTimeMillis() + refreshExpireTime);
    Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
    return JWT.create()
        .withExpiresAt(date)
        .withClaim("username", username)
        .sign(algorithm);

  }

  @Override
  public boolean verifyToken(String field) {
    if (StringUtils.isEmpty(field)) {
      return false;
    }
    return getToken(field) != null;
  }

  @Override
  public String getUserName(String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }
    try {
      Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
      JWTVerifier build = JWT.require(algorithm).build();
      DecodedJWT verify = build.verify(token);
      return verify.getClaim("username").asString();
    } catch (Exception e) {
      return null;
    }
  }

  private DecodedJWT getToken(String token) {
    if (StringUtils.isEmpty(token)) {
      return null;
    }
    try {
      Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
      JWTVerifier build = JWT.require(algorithm).build();
      return build.verify(token);
    } catch (Exception e) {
      return null;
    }
  }

}
