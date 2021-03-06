package cn.lx.ihrm.common.domain.system.response;

import cn.lx.ihrm.common.domain.system.Permission;
import cn.lx.ihrm.common.domain.system.Role;
import cn.lx.ihrm.common.domain.system.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * cn.lx.ihrm.common.domain.system.response
 *
 * @Author Administrator
 * @date 16:13
 */
@Data
@NoArgsConstructor
public class ProfileResponse {
    private String mobile;
    private String username;

    private String company;
    private Map roles;

    /**
     * 前端首页展示所需数据，包括权限
     * 首页需要展示用户名，侧边栏菜单，这两项是必须的
     * @param user
     */
    public ProfileResponse(User user) {
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.company = user.getCompanyName();
        //角色数据
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        Map rolesMap = new HashMap<>();
        for (Role role : user.getRoles()) {
            for (Permission perm : role.getPermissions()) {
                String code = perm.getCode();
                if (perm.getType() == 1) {
                    menus.add(code);
                } else if (perm.getType() == 2) {
                    points.add(code);
                } else {
                    apis.add(code);
                }
            }
        }
        rolesMap.put("menus", menus);
        rolesMap.put("points", points);
        rolesMap.put("apis", points);
        this.roles = rolesMap;
    }


    public ProfileResponse(User user, List<Permission> permissions) {
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.company = user.getCompanyName();
        //角色数据
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        Map rolesMap = new HashMap<>();

        for (Permission perm : permissions) {
            String code = perm.getCode();
            if (perm.getType() == 1) {
                menus.add(code);
            } else if (perm.getType() == 2) {
                points.add(code);
            } else {
                apis.add(code);
            }
        }

        rolesMap.put("menus", menus);
        rolesMap.put("points", points);
        rolesMap.put("apis", points);
        this.roles = rolesMap;
    }
}
