package cn.lx.ihrm.system.service.impl;

import cn.lx.ihrm.common.domain.company.Department;
import cn.lx.ihrm.common.domain.system.Permission;
import cn.lx.ihrm.common.domain.system.Role;
import cn.lx.ihrm.common.domain.system.User;
import cn.lx.ihrm.common.domain.system.response.ProfileResponse;
import cn.lx.ihrm.common.entity.IdWorker;
import cn.lx.ihrm.common.entity.Result;
import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import cn.lx.ihrm.common.feign.DepartmentFeign;
import cn.lx.ihrm.common.utils.BeanWrapperUtil;
import cn.lx.ihrm.system.dao.RoleDao;
import cn.lx.ihrm.system.dao.UserDao;
import cn.lx.ihrm.system.service.IPermissionService;
import cn.lx.ihrm.system.service.IUserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * cn.lx.ihrm.user.service.impl
 *
 * @Author Administrator
 * @date 15:57
 */
@Service
@Slf4j
@RefreshScope
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IPermissionService iPermissionService;

    @Autowired
    private DepartmentFeign departmentFeign;

    /**
     * 查询所有
     *
     * @param queryMap
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<User> findAll(Map<String, String> queryMap, int page, int size) throws CommonException {
        //page从0开始
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Specification<User> specification = new Specification<User>() {
            /**
             * Creates a WHERE clause for a query of the referenced entity in form of a {@link Predicate} for the given
             * {@link Root} and {@link CriteriaQuery}.
             *
             * @param root            must not be {@literal null}.
             * @param query           must not be {@literal null}.
             * @param criteriaBuilder must not be {@literal null}.
             * @return a {@link Predicate}, may be {@literal null}.
             */
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                //是否分配部门 1 分配， 0    未分配
                if (!StringUtils.isEmpty(queryMap.get("hasDept")) &&
                        "1".equals(queryMap.get("hasDept"))) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                } else if (!StringUtils.isEmpty(queryMap.get("hasDept")) &&
                        "0".equals(queryMap.get("hasDept"))) {
                    predicates.add(criteriaBuilder.isNull(root.get("departmentId")));
                }
                //部门id
                if (!StringUtils.isEmpty(queryMap.get("departmentId"))) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), queryMap.get("departmentId")));
                }
                //企业id
                if (!StringUtils.isEmpty(queryMap.get("companyId"))) {
                    predicates.add(criteriaBuilder.equal(root.get("companyId").as(String.class), queryMap.get("companyId")));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        Page<User> userPage = userDao.findAll(specification, pageRequest);
        return userPage;
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public User findById(String id) throws CommonException {
        return userDao.findById(id).get();
    }

    /**
     * 新增数据
     *
     * @param user
     * @return
     */
    @Override
    public User insert(User user) throws CommonException {

        user.setId(idWorker.nextId() + "");
        user.setCreateTime(new Date());
        user.setPassword("123456");
        user.setEnableState(1);

        return userDao.save(user);
    }

    /**
     * 根据id修改数据
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    public User updateById(String id, User user) throws CommonException {
        User queryCompany = userDao.findById(id).get();
        if (null == queryCompany) {
            throw new CommonException(ResultCode.E20001);
        }

        //获取对象中属性值为null的属性名集合
        String[] result = BeanWrapperUtil.getNullFieldNames(user);
        //拷贝company中不为null的属性值到queryCompany
        BeanUtils.copyProperties(user, queryCompany, result);

        return userDao.save(queryCompany);
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(String id) {
        userDao.deleteById(id);
    }

    /**
     * 分配角色
     *
     * @param userId
     * @param roleIds
     */
    @Override
    public void assignRoles(String userId, List<String> roleIds) throws CommonException {
        User user = userDao.findById(userId).get();
        Set<Role> roles = new HashSet<>();
        for (String roleId : roleIds) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        user.setRoles(roles);
        userDao.save(user);
    }

    /**
     * 获取userId拥有的角色
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> getUserRoles(String userId) throws CommonException {
        User user = userDao.findById(userId).get();
        Set<Role> roles = user.getRoles();
        log.info("roles:{}", JSON.toJSONString(roles));
        Set<String> roleIds = new HashSet<>();
        for (Role role : roles) {
            roleIds.add(role.getId());
        }
        log.info("roles:{}", JSON.toJSONString(roleIds));
        return roleIds;
    }

    /**
     * 用户登录
     *
     * @param mobile
     * @param password
     * @return
     */
    @Override
    public Serializable login(String mobile, String password) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(mobile, mobile);
        if (user == null) {
            throw new CommonException(ResultCode.E10002);
        }

        Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            //collect user principals and credentials in a gui specific manner
            //such as username/password html form, X509 certificate, OpenID, etc.
            //We'll use the username/password example here since it is the most common.
            //(do you know what movie this is from? ;)
            UsernamePasswordToken token = new UsernamePasswordToken(mobile, password);
            //this is all you have to do to support 'remember me' (no config - built in!):
            token.setRememberMe(false);
            try {
                currentUser.login(token);

                //print their identifying principal (in this case, a username):
                log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");

                //if no exception, that's it, we're done!
                return currentUser.getSession().getId();
            } catch (AuthenticationException ae) {
                //unexpected condition - error?
                throw new CommonException(ResultCode.E10002);
            }
        }

        return null;
    }

    /**
     * 根据用户id查询出权限和用户信息并封装
     *
     * @param principal
     * @return
     */
    @Override
    public ProfileResponse profile(String principal) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(principal, principal);
        if (user == null) {
            throw new CommonException(ResultCode.UNAUTHENTICATED);
        }
        ProfileResponse profileResponse = null;
        if ("saasAdmin".equals(user.getLevel())) {
            //获取所有权限
            Permission permission = new Permission();
            List<Permission> permissions = iPermissionService.findAll(permission);
            profileResponse = new ProfileResponse(user, permissions);
        } else if ("coAdmin".equals(user.getLevel())) {
            //获取企业可见的权限
            Permission permission = new Permission();
            permission.setEnVisible(1);
            List<Permission> permissions = iPermissionService.findAll(permission);
            profileResponse = new ProfileResponse(user, permissions);
        } else if ("user".equals(user.getLevel())) {
            profileResponse = new ProfileResponse(user);
        }
        return profileResponse;
    }

    /**
     * 根据用户名获取角色名集合
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getRoleNamesForUser(String username) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(username, username);
        if (null == user) {
            throw new CommonException(ResultCode.E10003);
        }
        Set<String> roleNames = new HashSet<>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    /**
     * 根据用户名获取用户权限代号集合
     *
     * @param username
     * @return
     */
    @Override
    public Set<String> getPermissions(String username) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(username, username);
        if (null == user) {
            throw new CommonException(ResultCode.E10003);
        }
        Set<String> permissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getCode());
            }
        }
        return permissions;
    }

    /**
     * 根据用户名获取密码
     *
     * @param username
     * @return
     */
    @Override
    public String findPasswordByMobileOrUsername(String username) throws CommonException {
        User user = userDao.findUserByMobileOrUsername(username, username);
        if (null == user) {
            throw new CommonException(ResultCode.E10003);
        }
        return user.getPassword();
    }

    /**
     * 员工批量导入
     *
     * @param file
     * @param companyId
     * @param companyName
     */
    @Override
    public void importExcel1(MultipartFile file, String companyId, String companyName) throws CommonException {
        try {
            InputStream is = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            //获取一个sheet
            XSSFSheet sheet = workbook.getSheetAt(0);

            //在循环内部创建性能不好
            Object propertyArray[] = new Object[11];
            //循环所有行
            //从第二行开始，第一行是表头，表头是固定的，也就是说表的格式是固定的
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                //具体某一行
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                //循环该行所有单元格
                for (int j = 0; j <= row.getLastCellNum(); j++) {
                    XSSFCell cell = row.getCell(j);

                    if (cell == null) {
                        propertyArray[j] = null;
                        continue;
                    }
                    Object value = getValue1(cell);
                    propertyArray[j] = value;
                }

                Result departmentResult = departmentFeign.findByCompanyIdAndCode(companyId, (String) propertyArray[5]);
                if (departmentResult.isSuccess() == false || departmentResult.getData() == null) {
                    throw new CommonException(ResultCode.E30002);
                }
                Department department = JSON.parseObject(
                        JSON.toJSONString(departmentResult.getData()), Department.class);
                String departmentId = department.getId();
                String departmentName = department.getName();
                //创建一个用户对象
                User user = new User();
                //为用户对象赋值
                setUserProperty1(companyId, companyName, departmentId, departmentName, propertyArray, user);
                userDao.save(user);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(ResultCode.E10004);
        }
    }

    /**
     * 员工批量导入（可承受百万级别）
     *
     * @param file
     * @param companyId
     * @param companyName
     */
    @Override
    public void importExcel2(MultipartFile file, String companyId, String companyName) throws CommonException {
        try {
            InputStream is = file.getInputStream();
            //创建一个可以存储多个数据对象的容器
            OPCPackage opcPackage = OPCPackage.open(is);
            //创建一个表格读取器
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            //获取表共享字符串对象
            SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
            //获取表样式对象
            StylesTable stylesTable = xssfReader.getStylesTable();
            //使用xml读取器工厂创建一个xml读取器
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            //在循环内部创建性能不好
            String[] propertyArray = new String[6];
            //注册事件处理器
            xmlReader.setContentHandler(
                    //该类可以单独处理xssf（xlsx）流处理的一个部分（xml）
                    new XSSFSheetXMLHandler(stylesTable,
                            sharedStringsTable,
                            //自定义一个过程实现，处理某一个流片段
                            new XSSFSheetXMLHandler.SheetContentsHandler() {
                                //用户对象
                                private User user = new User();
                                private int rowNum = 0;

                                /**
                                 * A row with the (zero based) row number has started
                                 *
                                 * @param rowNum
                                 */
                                @Override
                                public void startRow(int rowNum) {
                                    log.info("开始解析第" + rowNum + "行");
                                    this.rowNum = rowNum;
                                }

                                /**
                                 * A row with the (zero based) row number has ended
                                 *
                                 * @param rowNum
                                 */
                                @Override
                                public void endRow(int rowNum) {
                                    log.info("完成解析第" + rowNum + "行");
                                    if (rowNum > 0) {
                                        //存储该行数据到数据库
                                        Result departmentResult = departmentFeign.findByCompanyIdAndCode(companyId, propertyArray[5]);
                                        if (departmentResult.isSuccess() == false || departmentResult.getData() == null) {
                                            throw new CommonException(ResultCode.E30002);
                                        }
                                        Department department = JSON.parseObject(
                                                JSON.toJSONString(departmentResult.getData()), Department.class);
                                        String departmentId = department.getId();
                                        String departmentName = department.getName();

                                        //为用户对象赋值
                                        try {
                                            setUserProperty2(companyId, companyName, departmentId, departmentName, propertyArray, user);
                                        } catch (ParseException e) {
                                            log.error(e.getMessage(), e);
                                            throw new CommonException(ResultCode.E10004);
                                        }
                                        userDao.save(user);
                                    }
                                }

                                /**
                                 * A cell, with the given formatted value (may be null),
                                 * and possibly a comment (may be null), was encountered.
                                 * <p>
                                 * Sheets that have missing or empty cells may result in
                                 * sparse calls to <code>cell</code>. See the code in
                                 * <code>src/examples/src/org/apache/poi/xssf/eventusermodel/XLSX2CSV.java</code>
                                 * for an example of how to handle this scenario.
                                 *
                                 * @param cellReference
                                 * @param formattedValue
                                 * @param comment
                                 */
                                @Override
                                public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                                    //第一行数据是表头，不需要解析
                                    if (this.rowNum > 0) {
                                        //将一行数据解析到数组中
                                        log.info("formattedValue:{}",formattedValue);
                                        switch (cellReference.substring(0, 1)) {
                                            case "A":
                                                propertyArray[0] = formattedValue;
                                                break;
                                            case "B":
                                                propertyArray[1] = formattedValue;
                                                break;
                                            case "C":
                                                propertyArray[2] = formattedValue;
                                                break;
                                            case "D":
                                                propertyArray[3] = formattedValue;
                                                break;
                                            case "E":
                                                propertyArray[4] = formattedValue;
                                                break;
                                            case "F":
                                                propertyArray[5] = formattedValue;
                                                break;
                                        }
                                    }
                                }
                            },
                            false));

            Iterator<InputStream> iterator = xssfReader.getSheetsData();
            while (iterator.hasNext()) {
                InputStream inputStream = iterator.next();
                InputSource inputSource = new InputSource(inputStream);
                xmlReader.parse(inputSource);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(ResultCode.E10004);
        }
    }

    /**
     * 导出当月员工表
     *
     * @param month
     * @param companyId
     * @param response
     */
    @Override
    public void export(String month, String companyId, HttpServletResponse response) throws CommonException {
        //构造日期
        Calendar instance = Calendar.getInstance();
        //设置月份
        instance.set(Calendar.MONTH, Integer.parseInt(month) - 1);

        //设置时分秒
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        //当前月第一天
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date before = instance.getTime();

        //设置时分秒
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        //当前月最后一天
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date after = instance.getTime();

        log.info("查找日期在{}和{}之间的员工",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(before),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(after));
        Specification<User> specification = new Specification<User>() {
            /**
             * Creates a WHERE clause for a query of the referenced entity in form of a {@link Predicate} for the given
             * {@link Root} and {@link CriteriaQuery}.
             *
             * @param root            must not be {@literal null}.
             * @param query           must not be {@literal null}.
             * @param criteriaBuilder must not be {@literal null}.
             * @return a {@link Predicate}, may be {@literal null}.
             */
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate1 = criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
                Predicate predicate2 = criteriaBuilder.between(root.get("timeOfEntry").as(Date.class), before, after);
                return criteriaBuilder.and(predicate1, predicate2);
            }
        };
        List<User> userList = userDao.findAll(specification);
        log.info("导出" + month + "月份的员工：{}个", userList.size());

        //创建表格，并输出
        //createXLSX1(month, response, userList);
        //createXLSX2(month, response, userList);
        createXLSX3(month, response, userList);
    }

    /**
     * 创建表格，并输出（使用模板表格输出，并可以承受百万数据量）
     *
     * @param month
     * @param response
     * @param userList
     */
    private void createXLSX3(String month, HttpServletResponse response, List<User> userList) {
        ClassPathResource classPathResource = new ClassPathResource("templates.xlsx");

        XSSFWorkbook xssfWorkbook = null;
        SXSSFWorkbook workbook = null;
        try {
            xssfWorkbook = new XSSFWorkbook(classPathResource.getInputStream());
            workbook = new SXSSFWorkbook(xssfWorkbook);

            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            XSSFRow xssfSheetRow = xssfSheet.getRow(0);

            //获取单元格的样式必须从xssf工作空间中获取，然后再设置到sxssf中
            CellStyle[] cellStyle = new CellStyle[xssfSheetRow.getLastCellNum() + 1];
            for (int i = 0; i < xssfSheetRow.getLastCellNum(); i++) {
                cellStyle[i] = xssfSheetRow.getCell(i).getCellStyle();
            }

            SXSSFSheet sxssfSheet = workbook.getSheetAt(0);
            //不支持获取模板的原始数据，你可以看SXSSFWorkbook的构造方法，上面有详细说明
            //SXSSFRow row = sxssfSheet.getRow(0);

            //表内容
            User user = null;
            for (int i = 0; i < userList.size(); i++) {
                SXSSFRow content = sxssfSheet.createRow(i + 1);
                user = userList.get(i);
                SXSSFCell cell0 = content.createCell(0);
                cell0.setCellValue(user.getUsername());
                cell0.setCellStyle(cellStyle[0]);

                SXSSFCell cell1 = content.createCell(1);
                cell1.setCellValue(user.getMobile());
                cell1.setCellStyle(cellStyle[1]);

                SXSSFCell cell2 = content.createCell(2);
                cell2.setCellValue(user.getWorkNumber());
                cell2.setCellStyle(cellStyle[2]);

                SXSSFCell cell3 = content.createCell(3);
                cell3.setCellValue(user.getFormOfEmployment());
                cell3.setCellStyle(cellStyle[3]);

                SXSSFCell cell4 = content.createCell(4);
                cell4.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(user.getTimeOfEntry()));
                cell4.setCellStyle(cellStyle[4]);

                Result departmentFeignResult = departmentFeign.findById(user.getDepartmentId());
                if (departmentFeignResult.isSuccess() == false || departmentFeignResult.getData() == null) {
                    throw new CommonException(ResultCode.E20002);
                }
                Department department = JSON.parseObject(JSON.toJSONString(departmentFeignResult.getData()), Department.class);
                SXSSFCell cell5 = content.createCell(5);
                cell5.setCellValue(department.getCode());
                cell5.setCellStyle(cellStyle[5]);
            }

            String fileName = URLEncoder.encode(month + "月员工信息表.xlsx",
                    "UTF-8");
            response.setContentType("application/octet-stream");
            response.addHeader("content-disposition",
                    "attachment;filename=" +
                            new String(fileName.getBytes(Charset.defaultCharset()), "ISO8859-1"));
            response.addHeader("fileName", fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(ResultCode.SERVER_ERROR);
        } finally {
            try {
                xssfWorkbook.close();
                workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new CommonException(ResultCode.SERVER_ERROR);
            }

        }
    }

    /**
     * 创建表格，并输出（使用模板表格输出）
     *
     * @param month
     * @param response
     * @param userList
     */
    private void createXLSX2(String month, HttpServletResponse response, List<User> userList) {
        ClassPathResource classPathResource = new ClassPathResource("templates.xlsx");

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(classPathResource.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CommonException(ResultCode.SERVER_ERROR);
        }
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(0);

        //单元格样式
        XSSFCellStyle[] cellStyle = new XSSFCellStyle[row.getLastCellNum() + 1];
        for (int i = 0; i < row.getLastCellNum(); i++) {
            cellStyle[i] = row.getCell(i).getCellStyle();
        }

        //表内容
        User user = null;
        for (int i = 0; i < userList.size(); i++) {
            XSSFRow content = sheet.createRow(i + 1);
            user = userList.get(i);
            XSSFCell cell0 = content.createCell(0);
            cell0.setCellValue(user.getUsername());
            cell0.setCellStyle(cellStyle[0]);

            XSSFCell cell1 = content.createCell(1);
            cell1.setCellValue(user.getMobile());
            cell1.setCellStyle(cellStyle[1]);

            XSSFCell cell2 = content.createCell(2);
            cell2.setCellValue(user.getWorkNumber());
            cell2.setCellStyle(cellStyle[2]);

            XSSFCell cell3 = content.createCell(3);
            cell3.setCellValue(user.getFormOfEmployment());
            cell3.setCellStyle(cellStyle[3]);

            XSSFCell cell4 = content.createCell(4);
            cell4.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(user.getTimeOfEntry()));
            cell4.setCellStyle(cellStyle[4]);

            Result departmentFeignResult = departmentFeign.findById(user.getDepartmentId());
            if (departmentFeignResult.isSuccess() == false || departmentFeignResult.getData() == null) {
                throw new CommonException(ResultCode.E20002);
            }
            Department department = JSON.parseObject(JSON.toJSONString(departmentFeignResult.getData()), Department.class);
            XSSFCell cell5 = content.createCell(5);
            cell5.setCellValue(department.getCode());
            cell5.setCellStyle(cellStyle[5]);
        }
        try {
            String fileName = URLEncoder.encode(month + "月员工信息表.xlsx",
                    "UTF-8");
            response.setContentType("application/octet-stream");
            response.addHeader("content-disposition",
                    "attachment;filename=" +
                            new String(fileName.getBytes(Charset.defaultCharset()), "ISO8859-1"));
            response.addHeader("fileName", fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(ResultCode.SERVER_ERROR);
        }
    }


    /**
     * 创建表格，并输出
     *
     * @param month
     * @param response
     * @param userList
     */
    private void createXLSX1(String month, HttpServletResponse response, List<User> userList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        //单元格样式
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderColor(XSSFCellBorder.BorderSide.TOP, new XSSFColor(Color.RED));
        cellStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor(Color.RED));
        cellStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT, new XSSFColor(Color.RED));
        cellStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, new XSSFColor(Color.RED));

        //表头
        XSSFRow header = sheet.createRow(0);
        String headerString = "用户名,手机号,工号,聘用形式,入职时间,部门编码";
        String[] split = headerString.split(",");
        for (int i = 0; i < split.length; i++) {
            XSSFCell cell = header.createCell(i);
            cell.setCellValue(split[i]);
            cell.setCellStyle(cellStyle);
        }


        //表内容
        User user = null;
        for (int i = 0; i < userList.size(); i++) {
            XSSFRow content = sheet.createRow(i + 1);
            user = userList.get(i);
            XSSFCell cell0 = content.createCell(0);
            cell0.setCellValue(user.getUsername());
            cell0.setCellStyle(cellStyle);

            XSSFCell cell1 = content.createCell(1);
            cell1.setCellValue(user.getMobile());
            cell1.setCellStyle(cellStyle);

            XSSFCell cell2 = content.createCell(2);
            cell2.setCellValue(user.getWorkNumber());
            cell2.setCellStyle(cellStyle);

            XSSFCell cell3 = content.createCell(3);
            cell3.setCellValue(user.getFormOfEmployment());
            cell3.setCellStyle(cellStyle);

            XSSFCell cell4 = content.createCell(4);
            cell4.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(user.getTimeOfEntry()));
            cell4.setCellStyle(cellStyle);

            Result departmentFeignResult = departmentFeign.findById(user.getDepartmentId());
            if (departmentFeignResult.isSuccess() == false || departmentFeignResult.getData() == null) {
                throw new CommonException(ResultCode.E20002);
            }
            Department department = JSON.parseObject(JSON.toJSONString(departmentFeignResult.getData()), Department.class);
            XSSFCell cell5 = content.createCell(5);
            cell5.setCellValue(department.getCode());
            cell5.setCellStyle(cellStyle);
        }
        try {
            String fileName = URLEncoder.encode(month + "月员工信息表.xlsx",
                    "UTF-8");
            response.setContentType("application/octet-stream");
            response.addHeader("content-disposition",
                    "attachment;filename=" +
                            new String(fileName.getBytes(Charset.defaultCharset()), "ISO8859-1"));
            response.addHeader("fileName", fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonException(ResultCode.SERVER_ERROR);
        }
    }

    /**
     * 为新增的员工对象赋值
     * 对应于百万级别
     *
     * @param companyId
     * @param companyName
     * @param departmentId
     * @param departmentName
     * @param propertyArray
     * @param user
     * @throws ParseException
     */
    private void setUserProperty2(String companyId, String companyName, String departmentId, String departmentName, String[] propertyArray, User user) throws ParseException {
        user.setId(idWorker.nextId() + "");
        user.setUsername(propertyArray[0]);
        user.setMobile(propertyArray[1]);
        user.setWorkNumber(propertyArray[2]);
        user.setFormOfEmployment(Integer.valueOf((propertyArray[3])));
        user.setTimeOfEntry(new SimpleDateFormat("yyyy/MM/dd").parse(propertyArray[4]));
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        user.setDepartmentName(departmentName);
        user.setPassword("123456");
        user.setEnableState(1);
        user.setCreateTime(new Date());
        user.setInServiceStatus(1);
        user.setLevel("user");
        user.setDepartmentId(departmentId);
    }

    /**
     * 为新增的员工对象赋值
     *
     * @param companyId
     * @param companyName
     * @param departmentId
     * @param departmentName
     * @param propertyArray
     * @param user
     * @throws ParseException
     */
    private void setUserProperty1(String companyId, String companyName, String departmentId, String departmentName, Object[] propertyArray, User user) throws ParseException {
        user.setId(idWorker.nextId() + "");
        user.setUsername((String) propertyArray[0]);
        user.setMobile((String) propertyArray[1]);
        user.setWorkNumber(Integer.toString(((Double) propertyArray[2]).intValue()));
        user.setFormOfEmployment(Integer.valueOf(((Double) propertyArray[3]).intValue()));
        user.setTimeOfEntry((Date) propertyArray[4]);
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        user.setDepartmentName(departmentName);
        user.setPassword("123456");
        user.setEnableState(1);
        user.setCreateTime(new Date());
        user.setInServiceStatus(1);
        user.setLevel("user");
        user.setDepartmentId(departmentId);
    }

    /**
     * 获取单元格中的值
     *
     * @param cell
     * @return
     */
    private Object getValue1(XSSFCell cell) {
        Object value = null;
        switch (cell.getCellType()) {
            case STRING:
                log.info("string:" + cell.getStringCellValue());
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                log.info("boolean:" + cell.getBooleanCellValue());
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if ((DateUtil.isCellDateFormatted(cell))) {
                    log.info("日期:" + cell.getDateCellValue());
                    value = cell.getDateCellValue();
                } else {
                    log.info("number:" + cell.getNumericCellValue());
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA:
                log.info("fomula:" + cell.getNumericCellValue());
                value = cell.getCellFormula();
                break;
            case BLANK:
                log.info("空白表格");
                value = null;
                break;
        }
        return value;
    }


    /**
     * 获取单元格中的值
     *
     * @param cell
     * @return
     */
    private Object getValue2(SXSSFCell cell) {
        Object value = null;
        switch (cell.getCellType()) {
            case STRING:
                log.info("string:" + cell.getStringCellValue());
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                log.info("boolean:" + cell.getBooleanCellValue());
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if ((DateUtil.isCellDateFormatted(cell))) {
                    log.info("日期:" + cell.getDateCellValue());
                    value = cell.getDateCellValue();
                } else {
                    log.info("number:" + cell.getNumericCellValue());
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA:
                log.info("fomula:" + cell.getNumericCellValue());
                value = cell.getCellFormula();
                break;
            case BLANK:
                log.info("空白表格");
                value = null;
                break;
        }
        return value;
    }


}
