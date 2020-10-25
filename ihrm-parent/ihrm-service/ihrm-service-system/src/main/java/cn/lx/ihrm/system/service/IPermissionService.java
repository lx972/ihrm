package cn.lx.ihrm.system.service;

import cn.lx.ihrm.common.domain.system.Permission;

import java.util.List;

/**
 * cn.lx.ihrm.permission.service
 *
 * @Author Administrator
 * @date 15:57
 */
public interface IPermissionService {

    /**
     * 查询所有
     *
     * @return
     * @param permission
     */
    List<Permission> findAll(Permission permission);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Permission findById(String id);

    /**
     * 新增数据
     *
     * @param permission
     * @return
     */
    Permission insert(Permission permission);

    /**
     * 根据id修改数据
     *  @param id
     * @param permission
     * @return
     */
    Permission updateById(String id, Permission permission);

    /**
     * 根据id删除数据
     *
     * @param id
     */
    void deleteById(String id);
}
