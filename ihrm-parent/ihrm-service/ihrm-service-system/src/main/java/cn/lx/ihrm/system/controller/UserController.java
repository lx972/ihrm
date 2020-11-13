package cn.lx.ihrm.system.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.domain.system.vo.AssignRolesVO;
import cn.lx.ihrm.common.entity.PageResult;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * cn.lx.ihrm.user.controller
 *
 * @Author Administrator
 * @date 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
@RefreshScope
@Slf4j
public class UserController extends BaseController {

    @Autowired
    private IUserService iUserService;


    @GetMapping(value = "/export/{month}")
    public void export(@PathVariable("month")String month) {
        iUserService.export(month,getCompanyId(),response);
    }

    @PostMapping(value = "/import")
    public Result importExcel(@RequestParam("file") MultipartFile file) {
        iUserService.importExcel(file,getCompanyId(),getCompanyName());
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping(value = "/getRoleNames")
    public Result getRoleNames() {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        Set<String> roleNames = iUserService.getRoleNamesForUser((String) principal);
        return new Result(ResultCode.SUCCESS, roleNames);
    }

    @GetMapping(value = "/getPermissions")
    public Result getPermissions() {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        Set<String> permissions = iUserService.getPermissions((String) principal);
        return new Result(ResultCode.SUCCESS, permissions);
    }

    @GetMapping(value = "/profile")
    public Result profile() {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        ProfileResponse profileResponse = iUserService.profile((String) principal);
        return new Result(ResultCode.SUCCESS, profileResponse);
    }

    @PostMapping(value = "/login")
    public Result login(@RequestBody User user) {
        if (user == null || StringUtils.isEmpty(user.getMobile())
                || StringUtils.isEmpty(user.getPassword())) {
            throw new CommonException(ResultCode.E10001);
        }
        Serializable sessionId = iUserService.login(user.getMobile(), user.getPassword());
        return new Result(ResultCode.SUCCESS, sessionId);
    }

    @PostMapping(value = "/logout")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new Result(ResultCode.SUCCESS);
    }


    @PutMapping(value = "/role/{userId}")
    public Result assignRoles(@PathVariable("userId") String userId,
                              @RequestBody AssignRolesVO assignRolesVO) {
        if (StringUtils.isEmpty(userId) || assignRolesVO.getRoleIds().size() == 0) {
            throw new CommonException(ResultCode.E30001);
        }
        iUserService.assignRoles(userId, assignRolesVO.getRoleIds());
        return new Result(ResultCode.SUCCESS);
    }

    @GetMapping(value = "/role/{userId}")
    public Result getUserRoles(@PathVariable("userId") String userId) {
        Set<String> roleIds = iUserService.getUserRoles(userId);
        return new Result(ResultCode.SUCCESS, roleIds);
    }

    @GetMapping(value = "")
    public Result findAll(@RequestParam Map<String, String> queryMap, int page, int size) {
        Page<User> userPage = iUserService.findAll(queryMap, page, size);

        PageResult<User> userPageResult = new PageResult<>(userPage.getTotalElements(),
                userPage.getContent());
        return new Result(ResultCode.SUCCESS, userPageResult);
    }

    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id") String id) {
        User user = iUserService.findById(id);
        return new Result(ResultCode.SUCCESS, user);
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable("id") String id) {
        iUserService.deleteById(id);
        return Result.SUCCESS();
    }

    @PutMapping(value = "/{id}")
    public Result updateById(@PathVariable("id") String id,
                             @RequestBody User user) {
        User update = iUserService.updateById(id, user);
        return new Result(ResultCode.SUCCESS, update);
    }

    @PostMapping(value = "")
    public Result insert(@RequestBody User user) {
        user.setCompanyId(getCompanyId());
        user.setCompanyName(getCompanyName());
        User insert = iUserService.insert(user);
        return new Result(ResultCode.SUCCESS, insert);
    }
}
