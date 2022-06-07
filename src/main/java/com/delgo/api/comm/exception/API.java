package com.delgo.api.comm.exception;

public class API {
    public static class CODE {
        public static final int SUCCESS = 200;
        public static final int PARAM_ERROR = 301;
        public static final int PARAM_DATE_ERROR = 302;

        public static final int DB_ERROR = 302;
        public static final int DB_DELETE_ERROR = 304;


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