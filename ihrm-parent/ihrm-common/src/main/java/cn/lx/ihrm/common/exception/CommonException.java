package cn.lx.ihrm.common.exception;

import cn.lx.ihrm.common.entity.ResultCode;
import lombok.Data;

/**
 * cn.lx.ihrm.common.exception
 *
 * @Author Administrator
 * @date 18:07
 */
@Data
public class CommonException extends RuntimeException {

    private ResultCode resultCode=ResultCode.SERVER_ERROR;


    public CommonException() { }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public CommonException(ResultCode resultCode) {
        super(resultCode.message());
        this.resultCode = resultCode;
    }
}
