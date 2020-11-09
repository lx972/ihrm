package cn.lx.ihrm.common.Feign;

import cn.lx.ihrm.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "system")
@RequestMapping("/system/user")
public interface UserFeign {

    @GetMapping(value = "/getRoleNames")
    public Result getRoleNames();

    @GetMapping(value = "/getPermissions")
    public Result getPermissions();
}
