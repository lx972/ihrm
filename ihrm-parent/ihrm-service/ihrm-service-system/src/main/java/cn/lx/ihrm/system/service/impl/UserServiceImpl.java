package cn.lx.ihrm.system.service.impl;

import cn.lx.ihrm.common.domain.system.Permission;
import cn.lx.ihrm.common.domain.system.Role;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.system.dao.RoleDao;
import cn.lx.ihrm.system.dao.UserDao;
import cn.lx.ihrm.system.service.IPermissionService;
import cn.lx.ihrm.system.service.IUserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;

/**
 * cn.lx.ihrm.user.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
@Slf4j
@RefreshScope
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IPermissionService iPermissionService;

    /**
     * 查询所有
     *
     * @param queryMap
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<User> findAll(Map<String, String> queryMap, int page, int size) throws CommonException {
        //page从0开始
        PageRequest pageRequest = new PageRequest(page - 1, size);
        Specification<User> specification = new Specification<User>() {
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
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                //是否分配部门 1 分配， 0    未分配
                if (!StringUtils.isEmpty(queryMap.get("hasDept")) &&
                        "1".equals(queryMap.get("hasDept"))) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                } else if (!StringUtils.isEmpty(queryMap.get("hasDept")) &&
                        "0".equals(queryMap.get("hasDept"))) {
                    predicates.add(criteriaBuilder.isNull(root.get("departmentId")));
                }
                //部门id
                if (!StringUtils.isEmpty(queryMap.get("departmentId"))) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), queryMap.get("departmentId")));
                }
                //企业id
                if (!StringUtils.isEmpty(queryMap.get("companyId"))) {
                    predicates.add(criteriaBuilder.equal(root.get("companyId").as(String.class), queryMap.get("companyId")));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        Page<User> userPage = userDao.findAll(specification, pageRequest);
        return userPage;
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public User findById(String id) throws CommonException {
        return userDao.findById(id).get();
    }

    /**
     * 新增数据
     *
     * @param user
     * @return
     */
    @Override
    public User insert(User user) throws CommonException {

        user.setId(idWorker.nextId() + "");
        user.setCreateTime(new Date());
        user.setPassword("123456");
        user.setEnableState(1);

        return userDao.save(user);
    }

    /**
     * 根据id修改数据
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    public User updateById(String id, User user) throws CommonException {
        User queryCompany = userDao.findById(id).get();
        if (null == queryCompany) {
            throw new CommonException(ResultCode.E20001);
        }

        //获取对象中属性值为null的属性名集合
        String[] result = BeanWrapperUtil.getNullFieldNames(user);
        //拷贝company中不为null的属性值到queryCompany
        BeanUtils.copyProperties(user, queryCompany, result);

        return userDao.save(queryCompany);
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(String id) {
        userDao.deleteById(id);
    }

    /**
     * 分配角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    public void assignRoles(String userId, List<String> roleIds) throws CommonException {
        User user = userDao.findById(userId).get();
        Set<Role> roles = new HashSet<>();
        for (String roleId : roleIds) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        user.setRoles(roles);
        userDao.save(user);
    }

    /**
     * 获取userId拥有的角色
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> getUserRoles(String userId) throws CommonException {
        User user = userDao.findById(userId).get();
        Set<Role> roles = user.getRoles();
        log.info("roles:{}", JSON.toJSONString(roles));
        Set<String> roleIds = new HashSet<>();
        for (Role role : roles) {
            roleIds.add(role.getId());
        }
        log.info("roles:{}", JSON.toJSONString(roleIds));
        return roleIds;
    }

    /**
     * 用户登录
     *
     * @param mobile
     * @param password
     * @return
     */
    @Override
    public Serializable login(String mobile, String password) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(mobile, mobile);
        if (user == null) {
            throw new CommonException(ResultCode.E10002);
        }

        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            //collect user principals and credentials in a gui specific manner
            //such as username/password html form, X509 certificate, OpenID, etc.
            //We'll use the username/password example here since it is the most common.
            //(do you know what movie this is from? ;)
            UsernamePasswordToken token = new UsernamePasswordToken(mobile, password);
            //this is all you have to do to support 'remember me' (no config - built in!):
            token.setRememberMe(false);
            try {
                currentUser.login(token);

                //print their identifying principal (in this case, a username):
                log.info( "User [" + currentUser.getPrincipal() + "] logged in successfully." );

                //if no exception, that's it, we're done!
                 return currentUser.getSession().getId();
            } catch (AuthenticationException ae) {
                //unexpected condition - error?
                throw new CommonException(ResultCode.E10002);
            }
        }

        return null;
    }

    /**
     * 根据用户id查询出权限和用户信息并封装
     *
     * @param principal
     * @return
     */
    @Override
    public ProfileResponse profile(String principal) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(principal,principal);
        if (user == null) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        ProfileResponse profileResponse = null;
        if ("saasAdmin".equals(user.getLevel())) {
            //获取所有权限
            Permission permission = new Permission();
            List<Permission> permissions = iPermissionService.findAll(permission);
            profileResponse = new ProfileResponse(user, permissions);
        } else if ("coAdmin".equals(user.getLevel())) {
            //获取企业可见的权限
            Permission permission = new Permission();
            permission.setEnVisible(1);
            List<Permission> permissions = iPermissionService.findAll(permission);
            profileResponse = new ProfileResponse(user, permissions);
        } else if ("user".equals(user.getLevel())) {
            profileResponse = new ProfileResponse(user);
        }
        return profileResponse;
    }

    /**
     * 根据用户名获取角色名集合
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getRoleNamesForUser(String username) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(username, username);
        if (null == user) {
            throw new CommonException(ResultCode.E10003);
        }
        Set<String> roleNames = new HashSet<>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    /**
     * 根据用户名获取用户权限代号集合
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getPermissions(String username) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(username, username);
        if (null == user) {
            throw new CommonException(ResultCode.E10003);
        }
        Set<String> permissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getCode());
            }
        }
        return permissions;
    }

    /**
     * 根据用户名获取密码
     *
     * @param username
     * @return
     */
    @Override
    public String findPasswordByMobileOrUsername(String username) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(username, username);
        if (null == user) {
            throw new CommonException(ResultCode.E10003);
        }
        return user.getPassword();
    }


}
