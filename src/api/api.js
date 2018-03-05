
let base = 'http://localhost/schoolStore/admin';
// 模块变量
let user = 'user';
let buy = 'buyOrder';
let department = 'department';
let config = 'config';
let good = 'good';
let permission = 'permission';
let storeItem = 'store';
let take = 'takeOrder';
let storeOperation = 'storeOperation';

let getApi = (moduleName, methodName) => {
    return base + '/' +  moduleName + '/' +  methodName;
}


export const requestApi = {
    user: {
        login: (ref, params) => {return ref.$http.post(getApi(user,'login'), params)},
        findByDepartment: (ref, params) => {return ref.$http.post(getApi(user,'findUsersByDepartmentId'), params)},
        findByParam: (ref, params) => {return ref.$http.post(getApi(user,'findUserBySearchParams'), params)},
        update: (ref, params) => {return ref.$http.post(getApi(user,'updateUser'), params)},
        addUser: (ref, params) => {return ref.$http.post(getApi(user,'addUser'), params)},
        delete: (ref, params) => {return ref.$http.post(getApi(user,'deleteUser'), params)},
        deletes: (ref, params) => {return ref.$http.post(getApi(user,'deleteUsers'), params)},
        removePermissions: (ref, params) => {return ref.$http.post(getApi(user,'removePermissions'), params)},
        addPermissions: (ref, params) => {return ref.$http.post(getApi(user,'addPermissions'), params)}
    },
    config: {
        getInitData: (ref) => {return ref.$http.get(getApi(config,'getInitData'))}
    },
    good: {
        findByParam: (ref, params) => {return ref.$http.post(getApi(good,'findGoodItemsBySearchParams'), params)},
        update: (ref, params) => {return ref.$http.post(getApi(good,'updateGoodItem'), params)},
        findAllSorts: (ref) => {return ref.$http.get(getApi(good,'findAllSorts'))},
        addGoodItem: (ref, params) => {return ref.$http.post(getApi(good,'addGoodItem'), params)},
        delete: (ref, params) => {return ref.$http.post(getApi(good,'deleteGoodItem'), params)},
        deletes: (ref, params) => {return ref.$http.post(getApi(good,'deleteGoodItems'), params)},
        findByName: (ref, name) => {return ref.$http.get(getApi(good,'findAllGoodItemByName')+'?name='+name)}
    },
    takeOrder: {
        findByParam: (ref, params) => {return ref.$http.post(getApi(take,'findTakeOrdersBySearchParams'), params)},
        update: (ref, params) => {return ref.$http.post(getApi(take,'updateTakeOrder'), params)},
        addTakeOrder: (ref, params) => {return ref.$http.post(getApi(take,'addTakeOrder'), params)},
        delete: (ref, params) => {return ref.$http.post(getApi(take,'deleteTakeOrder'), params)},
        deletes: (ref, params) => {return ref.$http.post(getApi(take,'deleteTakeOrders'), params)},
        approve: (ref, params) => {return ref.$http.post(getApi(take,'approve'), params)},
        quickOutput: (ref, params) => {return ref.$http.post(getApi(take,'quickOutput'), params)},
        findById: (ref, params) => {return ref.$http.post(getApi(take,'findTakeOrderById'), params)},
    },
    buyOrder: {
        findByParam: (ref, params) => {return ref.$http.post(getApi(buy,'findBuyOrdersBySearchParams'), params)},
        update: (ref, params) => {return ref.$http.post(getApi(buy,'updateBuyOrder'), params)},
        addBuyOrder: (ref, params) => {return ref.$http.post(getApi(buy,'addBuyOrder'), params)},
        delete: (ref, params) => {return ref.$http.post(getApi(buy,'deleteBuyOrder'), params)},
        deletes: (ref, params) => {return ref.$http.post(getApi(buy,'deleteBuyOrders'), params)},
        approve: (ref, params) => {return ref.$http.post(getApi(buy,'approve'), params)},
        quickInput: (ref, params) => {return ref.$http.post(getApi(buy,'quickInput'), params)},
        findById: (ref, params) => {return ref.$http.post(getApi(buy,'findBuyOrderById'), params)},
    },
    department: {
        findAll: (ref, page, size) => {return ref.$http.get(getApi(department,'findAllDepartments')+'?page='+page+'&&size='+size)},
        update: (ref, params) => {return ref.$http.post(getApi(department,'updateDepartment'), params)},
        addDepartment: (ref, params) => {return ref.$http.post(getApi(department,'addDepartment'), params)},
        delete: (ref, params) => {return ref.$http.post(getApi(department,'deleteDepartment'), params)},
        deletes: (ref, params) => {return ref.$http.post(getApi(department,'deleteDepartments'), params)}
    },
    permission: {
        findAll: (ref, page, size) => {return ref.$http.get(getApi(permission,'findAllPermissions')+'?page='+page+'&&size='+size)},
        update: (ref, params) => {return ref.$http.post(getApi(permission,'updatePermission'), params)},
        addPermission: (ref, params) => {return ref.$http.post(getApi(permission,'addPermission'), params)},
        delete: (ref, params) => {return ref.$http.post(getApi(permission,'deletePermission'), params)},
        deletes: (ref, params) => {return ref.$http.post(getApi(permission,'deletePermissions'), params)}
    }
}



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
    USER_AREADLY_IN_SESSION_BEFORE_REGISTER:{code: 16, msg: "当前已经有用户登录，请先登出用户再注册"},
    GET_SORTS_FAIL: {code: 17, msg: "获取种类失败"}
}

export const msgUtils = {
    success: (ref, msg) => {
        ref.$message({
            showClose: true,
            message: msg,
            center: true,
            type: 'success'
        });
    },
    warning: (ref, msg) => {
        ref.$message({
            showClose: true,
            message: msg,
            center: true,
            type: 'warning'
        });
    },
    error: (ref, msg) => {
        ref.$message({
            showClose: true,
            message: msg,
            center: true,
            type: 'error'
        });
    }
}

export const permissionMap = {
    admin: "管理员",
    user: "普通用户"
}