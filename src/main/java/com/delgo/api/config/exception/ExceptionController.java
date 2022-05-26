package com.delgo.api.config.exception;

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

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity noSuchElementException(NoSuchElementException e) {
        log.info("Param Error"+ e.getMessage());
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg("Param Error").build());
    }

    @ExceptionHandler({TypeMismatchException.class})
    public ResponseEntity typeMismatchException(TypeMismatchException e) {
        log.info("Param Type Error"+ e.getMessage());
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
