package cn.lx.ihrm.common.entity;

public enum ResultCode {

    SUCCESS(true,10000,"操作成功！"),
    //---系统错误返回码-----
    FAIL(false,10001,"操作失败"),
    UNAUTHENTICATED(false,10002,"您还未登录"),
    UNAUTHORISE(false,10003,"权限不足"),
    SERVER_ERROR(false,99999,"抱歉，系统繁忙，请稍后重试！"),
    JWT_ERROR(false,88888,"令牌不正确！"),
    LOGIN_SUCCESS(true,10004,"登录成功！"),
    LOGIN_FAIL(false,10005,"登录失败！"),

    //---用户操作返回码----
    E10001(false,10001,"用户名或密码不能为空"),
    E10002(false,10002,"用户名或密码错误"),
    E10003(false,10003,"不存在该用户"),
    E10004(false,10004,"员工导入失败"),
    //---企业操作返回码----
    E20001(false,20001,"不存在此id对应的企业数据"),
    E20002(false,20002,"不存在此id对应的部门数据"),
    //---权限操作返回码----

    //---其他操作返回码----
    E30001(false,30001,"传入参数为空"),
    E30002(false,30002,"请求超时");

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    ResultCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
