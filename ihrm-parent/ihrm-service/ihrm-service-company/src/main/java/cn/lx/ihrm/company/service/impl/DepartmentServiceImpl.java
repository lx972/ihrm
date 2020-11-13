package cn.lx.ihrm.company.service.impl;

import cn.lx.ihrm.common.domain.company.Department;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.company.dao.DepartmentDao;
import cn.lx.ihrm.company.service.IDepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * cn.lx.ihrm.department.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<Department> findAll() {
        List<Department> departments = departmentDao.findAll();
        //要将数据拼装成树形结构
        //首先查出所有父部门
        Specification<Department> specification = new Specification<Department>() {
            /**
             * 重写此方法拼装查询条件
             * @param root 包含了指定泛型对象的所有信息
             * @param criteriaQuery 一般不用
             * @param criteriaBuilder 用于条件构造
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("pid").as(String.class), "0");
            }
        };
        List<Department> parentDepartments = departmentDao.findAll(specification);
        for (Department parentDepartment : parentDepartments) {
            handleTree(parentDepartment,departments);
        }
        return parentDepartments;
    }

    /**
     * 递归，封装成树结构
     * @param parent
     * @param departments
     */
    public void handleTree(Department parent,List<Department> departments){
        List<Department> children=new ArrayList<>();
        for (Department department : departments) {
            if (department.getPid().equals(parent.getId())){
                children.add(department);
                handleTree(department,departments);
            }
        }
        parent.setChildren(children);
    }


    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Department findById(String id) {
        return departmentDao.findById(id).get();
    }

    /**
     * 新增数据
     *
     * @param department
     * @return
     */
    @Override
    public Department insert(Department department,String companyId) {
        department.setId(idWorker.nextId() + "");
        department.setCreateTime(new Date());
        department.setCompanyId(companyId);
        return departmentDao.save(department);
    }

    /**
     * 根据id修改数据
     *  @param id
     * @param department
     * @return
     */
    @Override
    public Department updateById(String id, Department department) {
        Department queryDepartment = departmentDao.findById(id).get();
        if (null==queryDepartment){
            throw new CommonException(ResultCode.E20001);
        }

        //获取对象中属性值为null的属性名集合
        String[] result = BeanWrapperUtil.getNullFieldNames(department);
        //拷贝company中不为null的属性值到queryCompany
        BeanUtils.copyProperties(department,queryDepartment,result);

        return departmentDao.save(queryDepartment);
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(String id) {
       departmentDao.deleteById(id);
    }

    /**
     * 根据企业id和部门代码查找部门详细信息
     *
     * @param companyId
     * @param code
     * @return
     */
    @Override
    public Department findByCompanyIdAndCode(String companyId, String code) {
        Specification<Department> specification = new Specification<Department>() {
            /**
             * Creates a WHERE clause for a query of the referenced entity in form of a {@link Predicate} for the given
             * {@link Root} and {@link CriteriaQuery}.
             *
             * @param root            must not be {@literal null}.
             * @param query           must not be {@literal null}.
             * @param criteriaBuilder must not be {@literal null}.
             * @return a {@link Predicate}, may be {@literal null}.
             */
            @Override
            public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
                Predicate predicate2 = criteriaBuilder.equal(root.get("code").as(String.class), code);
                return criteriaBuilder.and(predicate1,predicate2);

            }
        };
        Department department = departmentDao.findOne(specification).get();
        return department;
    }
}
