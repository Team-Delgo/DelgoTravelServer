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

    private final String POST = "POST";
    private final String TYPE_JSON = "application/json";

    // Success Return은 postHandle에서 Log 처리
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) throws Exception {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        ResponseDTO responseDTO = getResponseBody(responseWrapper);
        if (responseDTO.getCode() == 200)
            if (request.getMethod().equals(POST) && request.getContentType().equals(TYPE_JSON))
                log.info("{} || Result : code = {} msg = {} \n Parameter : {} ", request.getRequestURI(), responseDTO.getCode(), responseDTO.getCodeMsg(),request.getAttribute("requestBody"));
            else
                APILog.info(request, responseDTO.getCode(), responseDTO.getCodeMsg());
    }

    // Error Return은 afterCompletion Log 처리
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        ResponseDTO responseDTO = getResponseBody(responseWrapper);

        // Exception이 발생하면 postHandle을 타지 않는다.
        if (responseDTO.getCode() != 200) // Service 단 Error Log 처리
            if (request.getMethod().equals(POST) && request.getContentType().equals(TYPE_JSON))
                log.info("{} || Result : code = {} msg = {} \n Parameter : {} ", request.getRequestURI(), responseDTO.getCode(), responseDTO.getCodeMsg(),request.getAttribute("requestBody"));
            else
                ERRLog.info(request, responseDTO.getCode(), responseDTO.getCodeMsg());
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