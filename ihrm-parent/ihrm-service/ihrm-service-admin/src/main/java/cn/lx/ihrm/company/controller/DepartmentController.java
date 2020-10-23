package cn.lx.ihrm.company.controller;

import cn.lx.ihrm.common.controller.BaseController;
import cn.lx.ihrm.common.domain.company.Company;
import cn.lx.ihrm.common.domain.company.Department;
import cn.lx.ihrm.common.domain.company.response.DeptListResult;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.company.service.ICompanyService;
import cn.lx.ihrm.company.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @Author Administrator
 * @date 15:58
 */
@RestController
@CrossOrigin
@RequestMapping("/department")
public class DepartmentController extends BaseController {

    @Autowired
    private IDepartmentService iDepartmentService;

    @Autowired
    private ICompanyService iCompanyService;

    @GetMapping(value = "")
    public Result findAll(){
        //查询所有部门，形成树结构
        List<Department> departmentList=iDepartmentService.findAll();
        //查询企业信息
        Company company = iCompanyService.findById(getCompanyId());
        DeptListResult deptListResult = new DeptListResult(company, departmentList);
        return new Result(ResultCode.SUCCESS,deptListResult);
    }

    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable("id")String id){
       Department department=iDepartmentService.findById(id);
        return new Result(ResultCode.SUCCESS,department);
    }

    @DeleteMapping(value = "/{id}")
    public Result deleteById(@PathVariable("id")String id){
       iDepartmentService.deleteById(id);
       return Result.SUCCESS();
    }

    @PutMapping(value = "/{id}")
    public Result updateById(@PathVariable("id")String id,
                             @RequestBody Department department){
        Department update = iDepartmentService.updateById(id, department);
        return new Result(ResultCode.SUCCESS,update);
    }

    @PostMapping(value = "")
    public Result insert(@RequestBody Department department){
        String companyId = getCompanyId();
        Department insert = iDepartmentService.insert(department,companyId);
        return new Result(ResultCode.SUCCESS,insert);
    }
}
