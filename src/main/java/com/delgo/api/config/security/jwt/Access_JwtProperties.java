package com.delgo.api.config.security.jwt;

public interface Access_JwtProperties {
	String SECRET = "Access_DelgoSecretKey"; // Key 값
	int EXPIRATION_TIME = 60000; // 10분
	String TOKEN_PREFIX = "Access ";
	String HEADER_STRING = "Authorization_Access";
}
