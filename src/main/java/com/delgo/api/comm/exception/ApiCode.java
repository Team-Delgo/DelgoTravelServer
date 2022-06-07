package com.delgo.api.comm.exception;

public enum ApiCode {
    SUCCESS(API.CODE.SUCCESS, "SUCCESS"), // 200
    PARAM_ERROR(API.CODE.PARAM_ERROR, "PARAM_ERROR"), // 3011
    PARAM_DATE_ERROR(API.CODE.PARAM_DATE_ERROR, "PARAM_DATE_ERROR"), // 3011

    DB_FAIL(API.CODE.DB_ERROR, "DB ERROR"), // 302
    DB_DELETE_ERROR(API.CODE.DB_DELETE_ERROR, "DB DELETE ERROR"), // 3011

    COUPON_SELECT_ERROR(API.CODE.COUPON_SELECT_ERROR, "존재하지 않는 쿠폰 번호입니다."), // 3011
    COUPON_DUPLICATE_ERROR(API.CODE.COUPON_DUPLICATE_ERROR, "이미 존재하는 쿠폰입니다."), // 3011

    PHOTO_UPLOAD_ERROR(API.CODE.PHOTO_UPLOAD_ERROR, "NCP PHOTO ERROR"), // 3011
    SMS_ERROR(API.CODE.SMS_ERROR, "NCP SMS ERROR"), // 3011

    REVIEW_NOT_EXIST(API.CODE.REVIEW_NOT_EXIST, "REVIEW_NOT_EXIST"), // 3011
    BOOKING_NOT_EXIST(API.CODE.BOOKING_NOT_EXIST, "BOOKING_NOT_EXIST"), // 3011


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