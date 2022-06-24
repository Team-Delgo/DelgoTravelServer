package com.delgo.api.comm.exception;

import com.delgo.api.dto.common.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    // 알 수 없는 에러 체크
    @ExceptionHandler
    public ResponseEntity exception(Exception e) {
        e.printStackTrace();
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.UNKNOWN_ERROR.getCode()).codeMsg(ApiCode.UNKNOWN_ERROR.getMsg()).build());
    }

    // @RequestParam Param Error Check
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.info("RequestParam PARAM ERROR : {}", e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.PARAM_ERROR.getCode()).codeMsg(ApiCode.PARAM_ERROR.getMsg()).build());
    }

    // @RequestParam File Error Check
    @ExceptionHandler({MissingServletRequestPartException.class})
    public ResponseEntity missingServletRequestPartException(MissingServletRequestPartException e) {
        log.info("RequestParam File ERROR : {}", e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.PARAM_ERROR.getCode()).codeMsg(ApiCode.PARAM_ERROR.getMsg()).build());
    }

    // @RequestBody DTO Param Error Check
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("RequestBody DTO PARAM ERROR : {}", e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.PARAM_ERROR.getCode()).codeMsg(ApiCode.PARAM_ERROR.getMsg()).build());
    }

    // Optional Select Error Check
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity methodArgumentNotValidException(NullPointerException e) {
        log.info("Optional ERROR : {}", e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.NOT_FOUND_DATA.getCode()).codeMsg(ApiCode.NOT_FOUND_DATA.getMsg()).build());
    }

}
