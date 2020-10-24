package cn.lx.ihrm.system.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.entity.PageResult;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * cn.lx.ihrm.user.controller
 *
 * @Author Administrator
 * @date 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService iUserService;

    @GetMapping(value = "")
    public Result findAll(@RequestParam Map<String,String> queryMap, int page, int size) {
        Page<User> userPage = iUserService.findAll(queryMap,page, size);

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
