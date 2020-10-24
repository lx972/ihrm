package cn.lx.ihrm.system.service.impl;

import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.system.dao.UserDao;
import cn.lx.ihrm.system.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * cn.lx.ihrm.user.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

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
}
