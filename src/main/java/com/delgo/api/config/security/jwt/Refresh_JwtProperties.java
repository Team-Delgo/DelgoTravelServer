package com.delgo.api.config.security.jwt;

public interface Refresh_JwtProperties {
	String SECRET = "Refresh_DelgoSecretKey"; // Key 값
	int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
	String TOKEN_PREFIX = "Refresh ";
	String HEADER_STRING = "Authorization_Refresh";
}
