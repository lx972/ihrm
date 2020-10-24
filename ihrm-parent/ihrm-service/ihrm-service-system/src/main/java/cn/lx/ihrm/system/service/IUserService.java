package cn.lx.ihrm.system.service;

import cn.lx.ihrm.common.domain.system.User;
import org.springframework.data.domain.Page;

import java.util.Map;

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
     * @return
     * @param queryMap
     * @param page
     * @param size
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
     *  @param id
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
}