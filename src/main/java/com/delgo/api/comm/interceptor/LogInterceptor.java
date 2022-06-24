package com.delgo.api.comm.interceptor;

import com.delgo.api.comm.log.APILog;
import com.delgo.api.comm.log.ERRLog;
import com.delgo.api.dto.common.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) throws Exception {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        if (responseWrapper != null) {
            ResponseDTO responseDTO = getResponseBody(responseWrapper);

            if (responseDTO.getCode() == 200)
                APILog.info(request, responseDTO.getCode(), responseDTO.getCodeMsg());
            else // Controller 단 Error Log 처리
                ERRLog.info(request, responseDTO.getCode(), responseDTO.getCodeMsg());
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        if (responseWrapper != null) {
            ResponseDTO responseDTO = getResponseBody(responseWrapper);

            // Exception이 발생하면 postHandle을 타지 않는다.
            if (responseDTO.getCode() != 200) // Service 단 Error Log 처리
                ERRLog.info(request, responseDTO.getCode(), responseDTO.getCodeMsg());
        }
    }


    private ResponseDTO getResponseBody(ContentCachingResponseWrapper responseWrapper) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(responseWrapper.getContentAsByteArray(), ResponseDTO.class);
    }

    private ContentCachingResponseWrapper getResponseWrapper(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        }
        return new ContentCachingResponseWrapper(response);
    }
}