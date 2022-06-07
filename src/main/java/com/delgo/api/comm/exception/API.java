package com.delgo.api.comm.exception;

public class API {
    public static class CODE {
        public static final int SUCCESS = 200;

        // COMMON ERROR
        public static final int PARAM_ERROR = 301;
        public static final int PARAM_DATE_ERROR = 302;

        // DB ERROR
        public static final int DB_ERROR = 302;
        public static final int DB_DELETE_ERROR = 304;

        // COUPON ERROR
        public static final int COUPON_SELECT_ERROR = 304;
        public static final int COUPON_DUPLICATE_ERROR = 304;

        // NCP ERROR
        public static final int PHOTO_UPLOAD_ERROR = 304;
        public static final int SMS_ERROR = 304;


        //REVIEW ERROR
        public static final int REVIEW_NOT_EXIST = 304;

        //BOOKING ERROR
        public static final int BOOKING_NOT_EXIST = 304;




        public static final int SESSION_END = 303;

        public static final int NOT_FOUND_SEARCH = 308;
        public static final int USER_ID_NOT_FOUND = 310;
        public static final int PHONE_ID_NOT_FOUND = 311;
        public static final int INIT_PASSWORD_FAIL = 312;
        public static final int TERMS_NOT_ALLOWED = 313;

        public static final int UNKNOWN_ERROR = 1000;
        public static final int SERVER_TIMEOUT_ERROR = 1001;
        public static final int SERVER_ERROR = 1003;
    }
}