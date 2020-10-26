package cn.lx.ihrm.system.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.system.Role;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.entity.PageResult;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.system.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author Administrator
 * @date 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private IRoleService iRoleService;


    @PutMapping(value = "/permission/{roleId}")
    public Result assignPrem(@PathVariable("roleId") String roleId,
                             @RequestBody Role role) {
        if (StringUtils.isEmpty(roleId)||role.getPermIds().size()==0){
            throw new CommonException(ResultCode.E30001);
        }
        iRoleService.assignPrem(roleId, role.getPermIds());
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping(value = "")
    public Result findAll(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "30") int size) {
        String companyId = getCompanyId();
        //查询所有角色
        Page<Role> rolePage = iRoleService.findAll(companyId, page, size);
        PageResult<Role> rolePageResult = new PageResult<>(rolePage.getTotalElements(),
                rolePage.getContent());
        return new Result(ResultCode.SUCCESS, rolePageResult);
    }

    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") String id) {
        Role role = iRoleService.findById(id);
        return new Result(ResultCode.SUCCESS, role);
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable("id") String id) {
        iRoleService.deleteById(id);
        return Result.SUCCESS();
    }

    @PutMapping(value = "/{id}")
    public Result updateById(@PathVariable("id") String id,
                             @RequestBody Role role) {
        Role update = iRoleService.updateById(id, role);
        return new Result(ResultCode.SUCCESS, update);
    }

    @PostMapping(value = "")
    public Result insert(@RequestBody Role role) {
        String companyId = getCompanyId();
        Role insert = iRoleService.insert(role, companyId);
        return new Result(ResultCode.SUCCESS, insert);
    }
}
