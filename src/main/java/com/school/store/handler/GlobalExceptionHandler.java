package com.school.store.handler;

import com.school.store.exception.BaseException;
import com.school.store.vo.ResultVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 *  全局异常处理
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    //拦截权限异常
    @ExceptionHandler(value = BaseException.class)
    public ResultVo handleBaseException(HttpServletRequest request,
                                         BaseException baseException)
    {
        return simpleResult(baseException, null);
    }


    /**
     *  返回 固定的 信息
     * @param baseException
     * @param data
     * @param <T>
     * @return
     */
    public <T> ResultVo simpleResult(BaseException baseException, T data){
        ResultVo vo = new ResultVo();
        vo.setMsg(baseException.getMsg());
        vo.setCode(baseException.getCode());
        vo.setData(data);
        return vo;
    }
}
