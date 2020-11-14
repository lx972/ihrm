package cn.lx.ihrm.system.service;

import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.exception.CommonException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
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
    Page<User> findAll(Map<String, String> queryMap, int page, int size) throws CommonException;

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    User findById(String id) throws CommonException;

    /**
     * 新增数据
     *
     * @param user
     * @return
     */
    User insert(User user) throws CommonException;

    /**
     * 根据id修改数据
     *
     * @param id
     * @param user
     * @return
     */
    User updateById(String id, User user) throws CommonException;

    /**
     * 根据id删除数据
     *
     * @param id
     */
    void deleteById(String id) throws CommonException;

    /**
     * 分配角色
     *
     * @param userId
     * @param roleIds
     */
    void assignRoles(String userId, List<String> roleIds) throws CommonException;

    /**
     * 获取userId拥有的角色
     *
     * @param userId
     * @return
     */
    Set<String> getUserRoles(String userId) throws CommonException;

    /**
     * 用户登录
     *
     * @param mobile
     * @param password
     * @return
     */
    Serializable login(String mobile, String password) throws CommonException;

    /**
     * 根据用户id查询出权限和用户信息并封装
     *
     * @param principal
     * @return
     */
    ProfileResponse profile(String principal) throws CommonException;

    /**
     * 根据用户名获取角色名集合
     *
     * @param username
     * @return
     */
    Set<String> getRoleNamesForUser(String username) throws CommonException;

    /**
     * 根据用户名获取用户权限代号集合
     *
     * @param username
     * @return
     */
    Set<String> getPermissions(String username) throws CommonException;

    /**
     * 根据用户名获取密码
     *
     * @param username
     * @return
     */
    String findPasswordByMobileOrUsername(String username) throws CommonException;

    /**
     * 员工批量导入
     *
     * @param file
     * @param companyId
     * @param companyName
     */
    void importExcel1(MultipartFile file, String companyId, String companyName) throws CommonException;


    /**
     * 员工批量导入（可承受百万级别）
     *
     * @param file
     * @param companyId
     * @param companyName
     * @throws CommonException
     */
    void importExcel2(MultipartFile file, String companyId, String companyName) throws CommonException;

    /**
     * 导出当月员工表
     *
     * @param month
     * @param companyId
     * @param response
     */
    void export(String month, String companyId, HttpServletResponse response) throws CommonException;
}
