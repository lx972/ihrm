package cn.lx.ihrm.system.service;

import cn.lx.ihrm.common.domain.system.Role;

import java.util.List;

/**
 * cn.lx.ihrm.role.service
 *
 * @Author Administrator
 * @date 15:57
 */
public interface IRoleService {

    /**
     * 查询所有
     *
     * @return
     */
    List<Role> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Role findById(String id);

    /**
     * 新增数据
     *
     * @param role
     * @return
     */
    Role insert(Role role, String companyId);

    /**
     * 根据id修改数据
     *  @param id
     * @param role
     * @return
     */
    Role updateById(String id, Role role);

    /**
     * 根据id删除数据
     *
     * @param id
     */
    void deleteById(String id);
}
