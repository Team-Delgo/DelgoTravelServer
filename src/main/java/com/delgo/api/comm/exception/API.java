package com.delgo.api.comm.exception;

import com.delgo.api.dto.common.ResponseDTO;
import org.springframework.http.ResponseEntity;

public class API {
    static public ResponseEntity SuccessReturn(Object data) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg(ApiCode.SUCCESS).data(data).build());
    }

    static public ResponseEntity SuccessReturn() {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg(ApiCode.SUCCESS).build());
    }

    static public ResponseEntity ErrorReturn(int code, String codeMsg) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(code).codeMsg(codeMsg).build());
    }
}
