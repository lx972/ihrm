package cn.lx.ihrm.company.service.impl;

import cn.lx.ihrm.common.domain.company.Company;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.company.dao.CompanyDao;
import cn.lx.ihrm.company.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * cn.lx.ihrm.company.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
public class CompanyServiceImpl implements ICompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<Company> findAll() {
        List<Company> companies = companyDao.findAll();
        return companies;
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Company findById(String id) {
        return companyDao.findById(id).get();
    }

    /**
     * 新增数据
     *
     * @param company
     * @return
     */
    @Override
    public Company insert(Company company) {

        company.setId(idWorker.nextId() + "");
        company.setCreateTime(new Date());
        //启用
        company.setState(1);
        //待审核
        company.setAuditState("0");
        //当前余额
        company.setBalance(0d);

        return companyDao.save(company);
    }

    /**
     * 根据id修改数据
     *  @param id
     * @param company
     * @return
     */
    @Override
    public Company updateById(String id, Company company) {
        Company queryCompany = companyDao.findById(id).get();
        if (null==queryCompany){
            throw new CommonException(ResultCode.E20001);
        }
        company.setId(id);
        //目前还不知道这里是不是有选择性的更新数据
       return companyDao.save(company);
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(String id) {
       companyDao.deleteById(id);
    }
}
