package cn.lx.ihrm.company.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.company.Company;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.company.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * cn.lx.ihrm.company.controller
 *
 * @Author Administrator
 * @date 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Autowired
    private ICompanyService iCompanyService;

    @GetMapping(value = "")
    public Result findAll(){
        List<Company> companies=iCompanyService.findAll();
        return new Result(ResultCode.SUCCESS,companies);
    }

    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id")String id){
       Company company=iCompanyService.findById(id);
        return new Result(ResultCode.SUCCESS,company);
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable("id")String id){
       iCompanyService.deleteById(id);
       return Result.SUCCESS();
    }

    @PutMapping(value = "/{id}")
    public Result updateById(@PathVariable("id")String id,
                             @RequestBody Company company){
        Company update = iCompanyService.updateById(id, company);
        return new Result(ResultCode.SUCCESS,update);
    }

    @PostMapping(value = "")
    public Result insert(@RequestBody Company company){
        Company insert = iCompanyService.insert(company);
        return new Result(ResultCode.SUCCESS,insert);
    }
}
