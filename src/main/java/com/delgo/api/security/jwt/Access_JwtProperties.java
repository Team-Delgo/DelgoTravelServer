package com.delgo.api.security.jwt;

public interface Access_JwtProperties {
	String SECRET = "delgoSecretKey"; // Key 값
	int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
	String TOKEN_PREFIX = "Access ";
	String HEADER_STRING = "Authorization";
}
