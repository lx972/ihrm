package cn.lx.ihrm.common.shiro.filter;

import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.utils.ResponseUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * cn.lx.ihrm.common.shiro.filter
 *
 * @Author Administrator
 * @date 16:07
 */
public class MyPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {
    /**
     * Handles the response when access has been denied.  It behaves as follows:
     * <ul>
     * <li>If the {@code Subject} is unknown<sup><a href="#known">[1]</a></sup>:
     * <ol><li>The incoming request will be saved and they will be redirected to the login page for authentication
     * (via the {@link #saveRequestAndRedirectToLogin(ServletRequest, ServletResponse)}
     * method).</li>
     * <li>Once successfully authenticated, they will be redirected back to the originally attempted page.</li></ol>
     * </li>
     * <li>If the Subject is known:</li>
     * <ol>
     * <li>The HTTP {@link HttpServletResponse#SC_UNAUTHORIZED} header will be set (401 Unauthorized)</li>
     * <li>If the {@link #getUnauthorizedUrl() unauthorizedUrl} has been configured, a redirect will be issued to that
     * URL.  Otherwise the 401 response is rendered normally</li>
     * </ul>
     * <code><a name="known">[1]</a></code>: A {@code Subject} is 'known' when
     * <code>subject.{@link Subject#getPrincipal() getPrincipal()}</code> is not {@code null},
     * which implicitly means that the subject is either currently authenticated or they have been remembered via
     * 'remember me' services.
     *
     * @param request  the incoming <code>ServletRequest</code>
     * @param response the outgoing <code>ServletResponse</code>
     * @return {@code false} always for this implementation.
     * @throws IOException if there is any servlet error.
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        Subject subject = getSubject(request, response);
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            //saveRequestAndRedirectToLogin(request, response);
            //未登录
            ResponseUtils.responseJson(response, ResultCode.UNAUTHENTICATED);
        } else {
            // If subject is known but not authorized, redirect to the unauthorized URL if there is one
            // If no unauthorized URL is specified, just return an unauthorized HTTP status code
            //String unauthorizedUrl = getUnauthorizedUrl();
            //SHIRO-142 - ensure that redirect _or_ error code occurs - both cannot happen due to response commit:
            /*if (StringUtils.hasText(unauthorizedUrl)) {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
            } else {
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }*/

            //权限不足
            ResponseUtils.responseJson(response, ResultCode.UNAUTHORISE);

        }
        return false;
    }
}
