package com.delgo.api.comm.exception;

public enum ApiCode {
    SUCCESS(API.CODE.SUCCESS, "SUCCESS"), // 200

    // COMMON ERROR
    PARAM_ERROR(API.CODE.PARAM_ERROR, "PARAM_ERROR"), // 301
    PARAM_DATE_ERROR(API.CODE.PARAM_DATE_ERROR, "PARAM_DATE_ERROR"),


    // LOGIN ERROR
    LOGIN_ERROR(API.CODE.PARAM_DATE_ERROR, "LOGIN_ERROR"),

    // TOKEN ERROR
    TOKEN_ERROR(API.CODE.TOKEN_ERROR, "TOKEN_ERROR"),

    // DB ERROR
    DB_FAIL(API.CODE.DB_ERROR, "DB ERROR"), // 302
    DB_DELETE_ERROR(API.CODE.DB_DELETE_ERROR, "DB DELETE ERROR"),

    // COUPON ERROR
    COUPON_SELECT_ERROR(API.CODE.COUPON_SELECT_ERROR, "존재하지 않는 쿠폰 번호입니다."),
    COUPON_DUPLICATE_ERROR(API.CODE.COUPON_DUPLICATE_ERROR, "이미 존재하는 쿠폰입니다."),

    // NCP ERROR
    PHOTO_UPLOAD_ERROR(API.CODE.PHOTO_UPLOAD_ERROR, "NCP PHOTO ERROR"),
    SMS_ERROR(API.CODE.SMS_ERROR, "NCP SMS ERROR"),

    //REVIEW ERROR
    REVIEW_NOT_EXIST(API.CODE.REVIEW_NOT_EXIST, "REVIEW_NOT_EXIST"),

    //BOOKING ERROR
    BOOKING_NOT_EXIST(API.CODE.BOOKING_NOT_EXIST, "BOOKING_NOT_EXIST"),

    UNKNOWN_ERROR(API.CODE.UNKNOWN_ERROR, "알수 없는 오류");


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