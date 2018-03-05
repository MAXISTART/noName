export const ERR_OK0 = 0; // 操作成功
export const ERR_OK1 = 1;
export const ERR_OK2 = 2;

export const SYS_ERROR_MSG = '系统错误，请重新刷新页面！   ';

export const Enum = {
    SYSTEM_ERROR:{code:-2, msg: "系统内部错误"},
    SUCCESS:{code: 0, msg: "成功"},
    UNKNOWN_ERROR:{code: -1, msg: "未知错误"},
    PARAM_ERROR:{code: 1, msg: "参数不正确"},
    NAME_SPEC_REPEAT:{code: 2, msg: "同样名字和规格的已经重复"},
    STORE_UNSATISFY:{code: 3, msg: "库存不足"},
    NAME_REPEAT:{code: 4, msg: "种类名字已经重复"},
    RESULT_OUT:{code: 5, msg: "审核结果已经出了，不能再修改订单"},
    PERMISSION_NOT_ALLOWED:{code: 6, msg: "用户权限不足"},
    NAME_FORMAT_ERROR:{code: 7, msg: "用户名格式不符"},
    USER_NOT_FOUND:{code: 8, msg: "用户不存在或者密码错误"},
    USER_LOGIN_SUCCESS:{code: 9, msg: "用户登录成功，当前用户为:普通用户"},
    ADMIN_LOGIN_SUCCESS:{code: 10, msg: "用户登录成功，当前用户为:管理员"},
    NOT_LOGIN:{code: 11, msg: "用户尚未登录或者登录已经过期"},
    LOGIN_OUT_SUCCESS:{code: 12, msg: "用户登出成功"},
    MAIL_FORMAT_ERROR:{code: 13, msg: "邮箱格式错误"},
    MOBILE_FORMAT_ERROR:{code: 14, msg: "手机号格式错误"},
    REGISTER_SUCCESS:{code: 15, msg: "注册成功"},
    USER_AREADLY_IN_SESSION_BEFORE_REGISTER:{code: 16, msg: "当前已经有用户登录，请先登出用户再注册"}
}