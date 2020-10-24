package cn.lx.ihrm.system.exceptionHandle;

import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.exception.CommonException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * cn.lx.ihrm.system.exceptionHandle
 *
 * @Author Administrator
 * @date 9:19
 */
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        if (e instanceof CommonException) {
            CommonException commonException = (CommonException) e;
            return new Result(commonException.getResultCode());
        } else {
            return Result.ERROR();
        }
    }
}
