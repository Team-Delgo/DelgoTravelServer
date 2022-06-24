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

        //BOOKING ERROR
        public static final int BOOKING_NOT_EXIST = 314;

        //EMAIL ERROR
        public static final int EMAIL_IS_NOT_EXISTING_ERROR = 315;
        public static final int EMAIL_IS_EXISTING_ERROR = 316;

        //SMS AUTH ERROR
        public static final int AUTH_NO_IS_NOT_MATCHING_ERROR = 317;

        //PHONE NO ERROR
        public static final int PHONE_NO_IS_NOT_EXISTING_ERROR = 318;
        public static final int PHONE_NO_IS_EXISTING_ERROR = 319;


        public static final int UNKNOWN_ERROR = 1000;
        public static final int SERVER_TIMEOUT_ERROR = 1001;
        public static final int SERVER_ERROR = 1003;
    }
}