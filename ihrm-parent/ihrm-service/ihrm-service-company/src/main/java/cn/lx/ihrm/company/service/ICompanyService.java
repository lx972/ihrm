package cn.lx.ihrm.company.service;

import cn.lx.ihrm.common.domain.company.Company;

import java.util.List;

/**
 * cn.lx.ihrm.company.service
 *
 * @Author Administrator
 * @date 15:57
 */
public interface ICompanyService {

    /**
     * 查询所有
     *
     * @return
     */
    List<Company> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Company findById(String id);

    /**
     * 新增数据
     *
     * @param company
     * @return
     */
    Company insert(Company company);

    /**
     * 根据id修改数据
     *  @param id
     * @param company
     * @return
     */
    Company updateById(String id, Company company);

    /**
     * 根据id删除数据
     *
     * @param id
     */
    void deleteById(String id);
}
