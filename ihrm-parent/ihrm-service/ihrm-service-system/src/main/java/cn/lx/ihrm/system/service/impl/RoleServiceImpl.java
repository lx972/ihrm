package cn.lx.ihrm.system.service.impl;

import cn.lx.ihrm.common.domain.system.Permission;
import cn.lx.ihrm.common.domain.system.Role;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.system.dao.PermissionDao;
import cn.lx.ihrm.system.dao.RoleDao;
import cn.lx.ihrm.system.service.IRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Set;

/**
 * cn.lx.ihrm.role.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PermissionDao permissionDao;


    /**
     * 查询所有
     *
     * @param companyId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Role> findAll(String companyId, int page, int size) {
        //page从0开始
        PageRequest pageRequest = new PageRequest(page - 1, size);
        Specification<Role> specification = new Specification<Role>() {
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
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
            }
        };
        Page<Role> rolePage = roleDao.findAll(specification, pageRequest);
        return rolePage;
    }


    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Role findById(String id) {
        Role role = roleDao.findById(id).get();
        //封装用户拥有的权限id
        Set<String> permIds = role.getPermIds();
        for (Permission permission : role.getPermissions() ) {
            permIds.add(permission.getId());
        }
        role.setPermIds(permIds);
        return role;
    }

    /**
     * 新增数据
     *
     * @param role
     * @return
     */
    @Override
    public Role insert(Role role, String companyId) {
        role.setId(idWorker.nextId() + "");
        role.setCompanyId(companyId);
        return roleDao.save(role);
    }

    /**
     * 根据id修改数据
     *
     * @param id
     * @param role
     * @return
     */
    @Override
    public Role updateById(String id, Role role) {
        Role queryRole = roleDao.findById(id).get();
        if (null == queryRole) {
            throw new CommonException(ResultCode.E20001);
        }

        //获取对象中属性值为null的属性名集合
        String[] result = BeanWrapperUtil.getNullFieldNames(role);
        //拷贝company中不为null的属性值到queryCompany
        BeanUtils.copyProperties(role, queryRole, result);

        return roleDao.save(queryRole);
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(String id) {
        roleDao.deleteById(id);
    }

    /**
     * 给角色分配权限
     *  @param roleId
     * @param permIds
     */
    @Override
    public void assignPrem(String roleId, Set<String> permIds) {
        //根据id查询出原本的信息
        Role role = roleDao.findById(roleId).get();
        //查询出所有的权限，并封装
        Set<Permission> permissions = new HashSet<>();
        for (String permId : permIds) {
            Permission permission = permissionDao.findById(permId).get();
            permissions.add(permission);
        }
        role.setPermissions(permissions);
        roleDao.save(role);
    }
}
