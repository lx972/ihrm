package cn.lx.ihrm.employee.dao;

import cn.lx.ihrm.common.domain.employee.EmployeePositive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 数据访问接口
 */
public interface PositiveDao extends JpaRepository<EmployeePositive, String>, JpaSpecificationExecutor<EmployeePositive> {
    EmployeePositive findByUserId(String uid);
}
