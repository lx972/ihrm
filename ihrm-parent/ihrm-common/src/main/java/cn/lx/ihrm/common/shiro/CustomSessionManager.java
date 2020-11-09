package cn.lx.ihrm.common.shiro;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * cn.lx.ihrm.common.shiro
 *
 * @Author Administrator
 * @date 15:06
 */
public class CustomSessionManager extends DefaultWebSessionManager {

    /**
     * 重写获取sessionID的方法
     * 在系统自带的三种方法外，额外添加了一种从请求头中获取
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String sessionId = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isEmpty(sessionId)){
            return super.getSessionId(request,response);
        }
        //这些属性都是参照父类方法进行设置
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "header");
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
        //automatically mark it valid here.  If it is invalid, the
        //onUnknownSession method below will be invoked and we'll remove the attribute at that time.
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        // always set rewrite flag - SHIRO-361
        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());

        return sessionId;
    }
}
