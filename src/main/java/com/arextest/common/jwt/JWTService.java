package com.arextest.common.jwt;

public interface JWTService {


  String makeAccessToken(String username);

  String makeAccessToken(String username, long expireTime);

  String makeRefreshToken(String username);

  boolean verifyToken(String field);

  String getUserName(String token);

}
