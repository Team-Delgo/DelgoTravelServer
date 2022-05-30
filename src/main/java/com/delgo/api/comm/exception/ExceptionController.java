package com.delgo.api.comm.exception;

import com.delgo.api.dto.common.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    // 알 수 없는 에러 체크
    @ExceptionHandler
    public ResponseEntity exception(Exception e) {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity noSuchElementException(NoSuchElementException e) {
        e.printStackTrace();
        log.info("Param Error");
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg("Param Error").build());
    }

    @ExceptionHandler({TypeMismatchException.class})
    public ResponseEntity typeMismatchException(TypeMismatchException e) {
        log.info("Param Type Error" + e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg("Param Type Error").build());
    }
//
//    @ExceptionHandler({SQLException.class})
//    public ResponseEntity sQLException(SQLException e) {
//        log.info("Param Error"+ e.getMessage());
//        return ResponseEntity.ok().body(
//                ResponseDTO.builder().code(303).codeMsg("DB ERROR").build());
//    }
}
