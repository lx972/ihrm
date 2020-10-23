package cn.lx.ihrm.company.service;

import cn.lx.ihrm.common.domain.company.Department;

import java.util.List;

/**
 * cn.lx.ihrm.company.service
 *
 * @Author Administrator
 * @date 15:57
 */
public interface IDepartmentService {

    /**
     * 查询所有
     *
     * @return
     */
    List<Department> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Department findById(String id);

    /**
     * 新增数据
     *
     * @param department
     * @return
     */
    Department insert(Department department,String companyId);

    /**
     * 根据id修改数据
     *  @param id
     * @param department
     * @return
     */
    Department updateById(String id, Department department);

    /**
     * 根据id删除数据
     *
     * @param id
     */
    void deleteById(String id);
}
