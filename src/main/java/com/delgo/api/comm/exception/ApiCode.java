package com.delgo.api.comm.exception;

public enum ApiCode {
    SUCCESS(API.CODE.SUCCESS, "성공"), // 200
    PARAM_ERROR(API.CODE.PARAM_ERROR, "입력값이 올바르지 않습니다."), // 3011
    PARAM_DATE_ERROR(API.CODE.PARAM_DATE_ERROR, "날짜값이 올바르지 않습니다."), // 3011
    DB_FAIL(API.CODE.DB_ERROR, "DB 오류"), // 302
    SESSION_END(API.CODE.SESSION_END, "세션 만료"), // 303
    NOT_FOUND_SEARCH(API.CODE.NOT_FOUND_SEARCH, "조회 결과가 없습니다."), // 308
    USER_ID_NOT_FOUND(API.CODE.USER_ID_NOT_FOUND, "USER ID 불일치"), // 310
    PHONE_ID_NOT_FOUND(API.CODE.PHONE_ID_NOT_FOUND, "Phone ID 불일치"), // 311
    INIT_PASSWORD_FAIL(API.CODE.INIT_PASSWORD_FAIL, "비밀번호 초기화에 실패하였습니다."), // 312
    TERMS_NOT_ALLOWED(API.CODE.TERMS_NOT_ALLOWED, "이용약관 미동의"), // 313
    UNKNOWN_ERROR(API.CODE.UNKNOWN_ERROR, "알수 없는 오류"), // 1001
    SERVER_TIMEOUT_ERROR(API.CODE.SERVER_TIMEOUT_ERROR, "서버 타임아웃 오류"), // 1002
    SERVER_ERROR(API.CODE.SERVER_ERROR, "서버 오류");    //1003


    private int code;
    private String msg;

    ApiCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public static ApiCode CODE(int intCode) {
        ApiCode apiCode = null;
        for (ApiCode code : ApiCode.values()) {
            if (intCode == code.getCode()) {
                apiCode = code;
                break;
            }
        }

        if (apiCode == null) {
            apiCode = ApiCode.SUCCESS;
        }

        return apiCode;
    }
}