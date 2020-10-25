package cn.lx.ihrm.system.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.system.Permission;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.system.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author Administrator
 * @date 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/permission")
public class PermissionController extends BaseController {

    @Autowired
    private IPermissionService iPermissionService;


    @GetMapping(value = "")
    public Result findAll(Permission permission) {
        //查询所有权限
        List<Permission> permissions = iPermissionService.findAll(permission);
        return new Result(ResultCode.SUCCESS, permissions);
    }

    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") String id) {
        Permission permission = iPermissionService.findById(id);
        return new Result(ResultCode.SUCCESS, permission);
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable("id") String id) {
        iPermissionService.deleteById(id);
        return Result.SUCCESS();
    }

    @PutMapping(value = "/{id}")
    public Result updateById(@PathVariable("id") String id,
                             @RequestBody Permission permission) {
        Permission update = iPermissionService.updateById(id, permission);
        return new Result(ResultCode.SUCCESS, update);
    }

    @PostMapping(value = "")
    public Result insert(@RequestBody Permission permission) {
        Permission insert = iPermissionService.insert(permission);
        return new Result(ResultCode.SUCCESS, insert);
    }
}
