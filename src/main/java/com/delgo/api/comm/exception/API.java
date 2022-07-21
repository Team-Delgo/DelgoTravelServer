package com.delgo.api.comm.exception;

public class API {
    public static class CODE {

        // -------------------------COMMON-------------------------
        public static final int SUCCESS = 200;

        public static final int PARAM_ERROR = 301;
        public static final int PARAM_DATE_ERROR = 302;

        // TOKEN ERROR
        public static final int TOKEN_ERROR = 303;

        // LOGIN ERROR
        public static final int LOGIN_ERROR = 304;

        // DB ERROR
        public static final int DB_ERROR = 305;
        public static final int DB_DELETE_ERROR = 306;
        public static final int NOT_FOUND_DATA = 307;

        // NCP ERROR
        public static final int PHOTO_UPLOAD_ERROR = 308;
        public static final int SMS_ERROR = 309;

        // ------------------------FUNCTION----------------------------
        // COUPON ERROR
        public static final int COUPON_SELECT_ERROR = 311;
        public static final int COUPON_DUPLICATE_ERROR = 312;

        //REVIEW ERROR
        public static final int REVIEW_DUPLICATE_ERROR = 313;
        public static final int REVIEW_NOT_EXIST = 314;
        public static final int REVIEW_PHOTO_COUNT_ERROR = 315;

        //BOOKING ERROR
        public static final int BOOKING_NOT_EXIST = 316;
        public static final int ALREADY_BOOKING_PLACE = 317;

        //EMAIL ERROR
        public static final int EMAIL_NOT_EXIST = 315;
        public static final int EMAIL_DUPLICATE_ERROR = 316;

        //SMS AUTH ERROR
        public static final int AUTH_NO_NOT_MATCHING = 317;

        //PHONE NO ERROR
        public static final int PHONE_NO_NOT_EXIST = 318;
        public static final int PHONE_NO_DUPLICATE_ERROR = 319;

        //NAME ERROR
        public static final int NAME_DUPLICATE_ERROR = 320;


        public static final int UNKNOWN_ERROR = 1000;
        public static final int SERVER_TIMEOUT_ERROR = 1001;
        public static final int SERVER_ERROR = 1003;
    }
}