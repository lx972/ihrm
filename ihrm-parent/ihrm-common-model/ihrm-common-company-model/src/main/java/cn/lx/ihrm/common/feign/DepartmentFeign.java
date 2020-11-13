package cn.lx.ihrm.common.feign;

import cn.lx.ihrm.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Administrator
 */
@FeignClient("company")
@RequestMapping("/company/department")
public interface DepartmentFeign {

    @GetMapping(value = "/companyId/{companyId}/code/{code}")
    Result findByCompanyIdAndCode(@PathVariable("companyId")String companyId,
                                         @PathVariable("code")String code );

    @GetMapping(value = "/{id}")
    Result findById(@PathVariable("id")String id);
}
