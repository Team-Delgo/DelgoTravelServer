package com.delgo.api.comm.security.jwt.config;

public interface AccessTokenProperties {
	String SECRET = "Access_Delgo_SecretKey"; // Key 값
	int EXPIRATION_TIME = 60000 * 60 * 24; // 1분 * 60 * 24 = 1일
	String TOKEN_PREFIX = "Access ";
	String HEADER_STRING = "Authorization_Access";
}
