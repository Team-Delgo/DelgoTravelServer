package com.delgo.api.comm.exception;

import com.delgo.api.dto.common.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    // 알 수 없는 에러 체크
    @ExceptionHandler
    public ResponseEntity exception(Exception e) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg("알 수 없는 오류입니다.").build());
    }


    // @RequestParam Param Error Check
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.info("Param Type Error" + e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(ApiCode.PARAM_ERROR.getCode()).codeMsg(ApiCode.PARAM_ERROR.getMsg()).build());
    }
//
//    @ExceptionHandler({SQLException.class})
//    public ResponseEntity sQLException(SQLException e) {
//        log.info("Param Error"+ e.getMessage());
//        return ResponseEntity.ok().body(
//                ResponseDTO.builder().code(303).codeMsg("DB ERROR").build());
//    }
}
