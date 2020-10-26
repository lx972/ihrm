package cn.lx.ihrm.common.domain.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * cn.lx.ihrm.common.domain.system.vo
 *
 * @Author Administrator
 * @date 17:25
 */
@Data
public class AssignRolesVO {
    private String userId;
    private List<String> roleIds=new ArrayList<>();
}
