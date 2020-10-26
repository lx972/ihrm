package cn.lx.ihrm.system.service;

import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.exception.CommonException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * cn.lx.ihrm.user.service
 *
 * @Author Administrator
 * @date 15:57
 */
public interface IUserService {

    /**
     * 查询所有
     *
     * @param queryMap
     * @param page
     * @param size
     * @return
     */
    Page<User> findAll(Map<String, String> queryMap, int page, int size);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    User findById(String id);

    /**
     * 新增数据
     *
     * @param user
     * @return
     */
    User insert(User user);

    /**
     * 根据id修改数据
     *
     * @param id
     * @param user
     * @return
     */
    User updateById(String id, User user);

    /**
     * 根据id删除数据
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 分配角色
     *
     * @param userId
     * @param roleIds
     */
    void assignRoles(String userId, List<String> roleIds);

    /**
     * 获取userId拥有的角色
     *
     * @param userId
     * @return
     */
    Set<String> getUserRoles(String userId);

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param companyId
     * @param companyName
     * @return
     */
    String login(String username, String password, String companyId, String companyName) throws CommonException;

    /**
     * 根据用户id查询出权限和用户信息并封装
     * @param userId
     * @return
     */
    ProfileResponse profile(String userId);
}
