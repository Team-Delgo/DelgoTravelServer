package com.delgo.api.comm;

import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.dto.common.ResponseDTO;
import org.springframework.http.ResponseEntity;

public class CommController {

    public ResponseEntity SuccessReturn(Object data) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.SUCCESS.getCode()).codeMsg(ApiCode.SUCCESS.getMsg()).data(data).build());
    }

    public ResponseEntity SuccessReturn() {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.SUCCESS.getCode()).codeMsg(ApiCode.SUCCESS.getMsg()).build());
    }

    public ResponseEntity ErrorReturn(ApiCode apiCode) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(apiCode.getCode()).codeMsg(apiCode.getMsg()).build());
    }

    public ResponseEntity ErrorReturn(ApiCode apiCode, Object data) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(apiCode.getCode()).codeMsg(apiCode.getMsg()).data(data).build());
    }
}
