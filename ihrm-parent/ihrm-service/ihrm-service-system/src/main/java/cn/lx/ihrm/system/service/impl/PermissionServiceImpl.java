package cn.lx.ihrm.system.service.impl;

import cn.lx.ihrm.common.domain.system.Permission;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.system.dao.PermissionDao;
import cn.lx.ihrm.system.service.IPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * cn.lx.ihrm.permission.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询所有
     *
     * @param permission
     * @return
     */
    @Override
    public List<Permission> findAll(Permission permission) {
        Specification<Permission> specification = new Specification<Permission>() {
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
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(permission.getType())) {
                    predicates.add(criteriaBuilder.equal(root.get("type").as(Integer.class), permission.getType()));
                }
                if (!StringUtils.isEmpty(permission.getPid())) {
                    predicates.add(criteriaBuilder.equal(root.get("pid").as(String.class), permission.getPid()));
                }
                if (!StringUtils.isEmpty(permission.getEnVisible())) {
                    predicates.add(criteriaBuilder.equal(root.get("enVisible").as(Integer.class), permission.getEnVisible()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        List<Permission> permissions = permissionDao.findAll(specification);
        return permissions;
    }


    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Permission findById(String id) {
        return permissionDao.findById(id).get();
    }

    /**
     * 新增数据
     *
     * @param permission
     * @return
     */
    @Override
    public Permission insert(Permission permission) {
        permission.setId(idWorker.nextId() + "");
        return permissionDao.save(permission);
    }

    /**
     * 根据id修改数据
     *
     * @param id
     * @param permission
     * @return
     */
    @Override
    public Permission updateById(String id, Permission permission) {
        Permission queryPermission = permissionDao.findById(id).get();
        if (null == queryPermission) {
            throw new CommonException(ResultCode.E20001);
        }

        //获取对象中属性值为null的属性名集合
        String[] result = BeanWrapperUtil.getNullFieldNames(permission);
        //拷贝company中不为null的属性值到queryCompany
        BeanUtils.copyProperties(permission, queryPermission, result);

        return permissionDao.save(queryPermission);
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(String id) {
        permissionDao.deleteById(id);
    }
}
