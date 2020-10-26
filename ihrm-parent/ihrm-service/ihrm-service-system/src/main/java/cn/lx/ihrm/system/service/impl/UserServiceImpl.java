package cn.lx.ihrm.system.service.impl;

import cn.lx.ihrm.common.domain.system.Role;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.system.dao.RoleDao;
import cn.lx.ihrm.system.dao.UserDao;
import cn.lx.ihrm.system.service.IUserService;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    @Value(value = "${jjwt.config.secretString}")
    private String secretString;

    /**
     * 查询所有
     *
     * @param queryMap
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<User> findAll(Map<String, String> queryMap, int page, int size) {
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
                if (!StringUtils.isEmpty(queryMap.get("hasDept"))&&
                        "1".equals(queryMap.get("hasDept"))) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                }else if (!StringUtils.isEmpty(queryMap.get("hasDept"))&&
                        "0".equals(queryMap.get("hasDept"))){
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
        Page<User> userPage = userDao.findAll(specification,pageRequest);
        return userPage;
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    /**
     * 新增数据
     *
     * @param user
     * @return
     */
    @Override
    public User insert(User user) {

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
    public User updateById(String id, User user) {
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
     * @param userId
     * @param roleIds
     */
    @Override
    public void assignRoles(String userId, List<String> roleIds) {
        User user = userDao.findById(userId).get();
        Set<Role> roles=new HashSet<>();
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
    public Set<String> getUserRoles(String userId) {
        User user = userDao.findById(userId).get();
        Set<Role> roles = user.getRoles();
        log.info("roles:{}", JSON.toJSONString(roles));
        Set<String> roleIds =new HashSet<>();
        for (Role role : roles) {
            roleIds.add(role.getId());
        }
        log.info("roles:{}", JSON.toJSONString(roleIds));
        return roleIds;
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param companyId
     * @param companyName
     * @return
     */
    @Override
    public String login(String username, String password, String companyId, String companyName)throws CommonException {
        User user = userDao.findUserByUsernameAndPassword(username, password);
        if (user==null){
            throw new CommonException(ResultCode.E10002);
        }
        //使用jjwt创建令牌
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        Map<String, Object> claims=new HashMap<>();
        claims.put("userId",user.getId());
        claims.put("companyId",companyId);
        claims.put("companyName",companyName);
        String jws =Jwts.builder()
                //.setIssuer("me")
                //.setSubject("Bob")
                //.setAudience("you")
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30)) //a java.util.Date
                //.setNotBefore(notBefore) //a java.util.Date
                //.setIssuedAt(new Date()) // for example, now
                //.setId(UUID.randomUUID()) //just an example id
                .signWith(secretKey)
                .addClaims(claims)
                .compact();
        return jws;
    }

    /**
     * 根据用户id查询出权限和用户信息并封装
     *
     * @param userId
     * @return
     */
    @Override
    public ProfileResponse profile(String userId) {
        User user = userDao.findById(userId).get();
        if (user==null){
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        ProfileResponse profileResponse = new ProfileResponse(user);
        return profileResponse;
    }


}
