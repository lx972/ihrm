package cn.lx.ihrm.common.domain.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pe_role")
@Getter
@Setter
public class Role implements Serializable {
    private static final long serialVersionUID = 594829320797158219L;
    @Id
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 说明
     */
    private String description;
    /**
     * 企业id
     */
    private String companyId;

    /**
     * @ManyToMany(mappedBy="roles")    角色与用户关系多对多
     * mappedBy="roles" mappedBy属性定义了此类为双向关系的维护端，角色维护用户
     */
    @JsonIgnore
    @ManyToMany(mappedBy="roles")
    private Set<User> users = new HashSet<User>(0);//角色与用户   多对多


    /**
     * 角色和权限之间的关系，大同小异
     */
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="pe_role_permission",
            joinColumns={@JoinColumn(name="role_id",referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="permission_id",referencedColumnName="id")})
    private Set<Permission> permissions = new HashSet<Permission>(0);//角色与模块  多对多

    /**
     * 用户拥有的权限id
     */
    @Transient
    private Set<String> permIds = new HashSet<String>(0);
}
