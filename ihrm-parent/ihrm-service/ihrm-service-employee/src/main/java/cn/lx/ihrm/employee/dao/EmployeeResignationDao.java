package cn.lx.ihrm.employee.dao;

import cn.lx.ihrm.common.domain.employee.EmployeeResignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 数据访问接口
 */
public interface EmployeeResignationDao extends JpaRepository<EmployeeResignation, String>, JpaSpecificationExecutor<EmployeeResignation> {
    EmployeeResignation findByUserId(String uid);
}
