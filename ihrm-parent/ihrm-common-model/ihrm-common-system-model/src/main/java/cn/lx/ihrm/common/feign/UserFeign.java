package cn.lx.ihrm.common.feign;

import cn.lx.ihrm.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "system")
@RequestMapping("/system/user")
public interface UserFeign {

    @GetMapping(value = "/getRoleNames")
    Result getRoleNames();

    @GetMapping(value = "/getPermissions")
    Result getPermissions();
}
