package cn.lx.ihrm.company.dao;

import cn.lx.ihrm.common.domain.company.Company;
import cn.lx.ihrm.common.domain.company.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * cn.lx.ihrm.Department.dao
 * JpaRepository<Company,String>中有两个泛型，第一个是指操作的对象，对象中必须有和
 * 数据库的映射，第二个是主键的类型
 * JpaSpecificationExecutor<Company> 它是一个条件构造器，后面会将如何使用
 *
 * @Author Administrator
 * @date 15:54
 */
public interface DepartmentDao extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {
}
