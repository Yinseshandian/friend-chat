package com.li.chat.handler;

import com.li.chat.common.enums.WebErrorCodeEnum;
import com.li.chat.common.utils.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/**
 * @author malaka
 */
@RestControllerAdvice
public class CommonExceptionHandler {

    /**
     * 请求类型错误
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultData httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        return ResultData.error(500, e.getMessage());
    }

    /**
     * 未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public ResultData errorHandler(Throwable e) {
        e.printStackTrace();
        return ResultData.error(WebErrorCodeEnum.SERVICE_ERROR);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResultData errorHandler(HttpMessageNotReadableException e) {
        e.printStackTrace();
        return ResultData.error(500, "请求参数异常");
    }

    /**
     * 表单请求校验结果处理
     * @param bindException
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public ResultData errorHandler(BindException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();
        return extractException(bindingResult.getAllErrors());
    }

    /**
     * JSON请求校验结果，也就是请求中对实体标记了@RequestBody
     * @param methodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public  ResultData errorHandler(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        return extractException(bindingResult.getAllErrors());
    }

    private  ResultData extractException(List<ObjectError> errorList) {
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError objectError : errorList) {
            errorMsg.append(objectError.getDefaultMessage()).append(";");
        }
        // 移出最后的分隔符
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
        return ResultData.error(HttpStatus.BAD_REQUEST.value(), errorMsg.toString());
    }


}
