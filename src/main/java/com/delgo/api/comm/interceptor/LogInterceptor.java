package com.delgo.api.comm.interceptor;

import com.delgo.api.comm.log.APILog;
import com.delgo.api.comm.log.ERRLog;
import com.delgo.api.dto.common.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogInterceptor implements HandlerInterceptor {

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) throws Exception {
        ContentCachingResponseWrapper responseWrapper = getResponseWrapper(response);
        ResponseDTO responseDTO = getResponseBody(responseWrapper);

        if (responseDTO.getCode() == 200)
            APILog.info(request, responseDTO.getCode(), responseDTO.getCodeMsg());
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