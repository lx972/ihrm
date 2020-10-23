package cn.lx.ihrm.common.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cn.lx.ihrm.common.controller
 *
 * @Author Administrator
 * @date 16:21
 */
public class BaseController {

    protected HttpServletRequest request;
    protected HttpServletResponse response;


    /**
     * 所有controller方法执行前都会调用
     * @param request
     * @param response
     */
    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response){
        this.request=request;
        this.response=response;
    }

    /**
     * 这里先返回1，以后这里需要解析令牌，获取令牌上的数据
     * @return
     */
    protected String getCompanyId(){
        return "1";
    }

    /**
     * 这里先返回`江苏传智播客教育股份有限公司`，以后这里需要解析令牌，获取令牌上的数据
     * @return
     */
    protected String getCompanyName(){
        return "江苏传智播客教育股份有限公司";
    }


}
