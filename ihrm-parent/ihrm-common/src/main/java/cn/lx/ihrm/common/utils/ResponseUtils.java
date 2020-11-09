package cn.lx.ihrm.common.utils;

import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * cn.lx.ihrm.common.utils
 *
 * @Author Administrator
 * @date 16:02
 */
@Slf4j
public class ResponseUtils {


    /**
     * 响应json到客户端
     *
     * @param response
     * @param resultCode
     */
    public static void responseJson(ServletResponse response, ResultCode resultCode) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(200);
        Result result = new Result(resultCode);
        try {
            httpServletResponse.getWriter().write(JSON.toJSONString(result));
            httpServletResponse.getWriter().flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error("Authentication exception", ex);
        }
    }

}
