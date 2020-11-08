package cn.lx.ihrm.common.controller;

import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * cn.lx.ihrm.common.controller
 *
 * @Author Administrator
 * @date 9:29
 */
@RestController
public class ShiroController {

    @RequestMapping(value = "/successUrl")
    @ResponseStatus(value = HttpStatus.OK)
    public Result successUrl(){
        return new Result(ResultCode.SUCCESS);
    }

    @RequestMapping(value = "/unauthorizedUrl")
    @ResponseStatus(value = HttpStatus.OK)
    public Result unauthorizedUrl(String code){
        //未认证时
        if ("1".equals(code)){
            return new Result(ResultCode.UNAUTHORISE);
        }else if("2".equals(code)){
            //跳转到登录页面时
            return new Result(ResultCode.UNAUTHENTICATED);
        }
        return new Result(ResultCode.SERVER_ERROR);
    }
}
