package cn.lx.ihrm.system.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.domain.system.vo.AssignRolesVO;
import cn.lx.ihrm.common.entity.PageResult;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.TokenUtil;
import cn.lx.ihrm.system.service.IUserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
public class UserController extends BaseController {

    @Autowired
    private IUserService iUserService;

    @Value(value = "${jjwt.config.secretString}")
    private String secretString;

    @GetMapping(value = "/profile")
    public Result profile() {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        Claims claims = TokenUtil.decodeToken(token, secretString);
        //String userId = "1063705482939731968";
        String userId = (String) claims.get("userId");
        ProfileResponse profileResponse = iUserService.profile(userId);
        return new Result(ResultCode.SUCCESS, profileResponse);
    }

    @PostMapping(value = "/login")
    public Result login(@RequestBody User user) {
        if (user == null || StringUtils.isEmpty(user.getUsername())
                || StringUtils.isEmpty(user.getPassword())) {
            throw new CommonException(ResultCode.E10001);
        }
        String jws = iUserService.login(user.getUsername(), user.getPassword()
                , getCompanyId(), getCompanyName());
        return new Result(ResultCode.SUCCESS, jws);
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
