import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import { LoginUsers, Users, WarehouseManager, Things, enterWarehouseRecords } from './data/user';
let _Users = Users;
let _WareHouseManager = WarehouseManager;
let _Things = Things;
let _EnterWarehouseRecords = enterWarehouseRecords;

export default {
  /**
   * mock bootstrap
   */
  bootstrap() {
    let mock = new MockAdapter(axios);

    // mock success request
    mock.onGet('/success').reply(200, {
      msg: 'success'
    });

    // mock error request
    mock.onGet('/error').reply(500, {
      msg: 'failure'
    });

    //登录
    mock.onPost('/login').reply(config => {
      let {username, password} = JSON.parse(config.data);
      return new Promise((resolve, reject) => {
        let user = null;
        setTimeout(() => {
          let hasUser = LoginUsers.some(u => {
            if (u.username === username && u.password === password) {
              user = JSON.parse(JSON.stringify(u));
              user.password = undefined;
              return true;
            }
          });

          if (hasUser) {
            resolve([200, { code: 200, msg: '请求成功', user }]);
          } else {
            resolve([200, { code: 500, msg: '账号或密码错误' }]);
          }
        }, 1000);
      });
    });

    //获取用户列表
    mock.onGet('/user/list').reply(config => {
      let {name} = config.params;
      let mockUsers = _Users.filter(user => {
        if (name && user.name.indexOf(name) == -1) return false;
        return true;
      });
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          resolve([200, {
            users: mockUsers
          }]);
        }, 1000);
      });
    });

    // 获取入库记录
      mock.onGet('/warehouse/enterWarehouseRecord').reply(config => {
        let bool = false;
        let enterWarehouseRecordFilter = config.params;
        for(let key in enterWarehouseRecordFilter) {
          if(enterWarehouseRecordFilter[key] !== '') {
            bool = true;
            break;
          }
        }
        if(bool) {
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve([200,{
                        enterWarehouseRecords: _EnterWarehouseRecords
                    }]);
                }, 1000);
            });
        }
      });

      // 获取入库记录(分页)
      mock.onGet('/warehouse/enterWarehouseRecordListpage').reply(config => {
          let {tableId, currentPage, pageSize, queryKey} = config.params;
          let enterWarehouseRecordsList = [];
          let enterWarehouseKey = [
              {
                label: '全部',
                value: 'all'
              },
              {
                label: '名称',
                value: 'thingName'
              },
              {
                  label: '种类',
                  value: 'type'
              },
              {
                  label: '规格',
                  value: 'size'
              },
              {
                  label: '单位',
                  value: 'unit'
              },
              {
                  label: '数量',
                  value: 'total'
              },
              {
                  label: '单价',
                  value: 'unitPrice'
              },
              {
                  label: '总价',
                  value: 'totalPrice'
              },
              {
                  label: '负责人',
                  value: 'personInCharge'
              },
              {
                  label: '入库时间',
                  value: 'enterTime'
              },
              {
                  label: '更新时间',
                  value: 'updateTime'
              },
              {
                  label: '提交时间',
                  value: 'submitTime'
              },
              {
                  label: '提交人',
                  value: 'submitPerson'
              }
          ];
          let total = 0;
          if(tableId === 'enterWarehouseRecords') {
            if(currentPage !== '' && pageSize !== '') {
                switch (queryKey) {
                    case 'all': {total = _EnterWarehouseRecords.length;
                        enterWarehouseRecordsList = _EnterWarehouseRecords.filter((u, index) => index < pageSize * currentPage && index >= pageSize * (currentPage - 1));
                        break;}
                    case 'thingName': {
                        break;}
                    case 'type': {break;}
                    case 'size': {break;}
                    case 'unit': {break;}
                    case 'total': {break;}
                    case 'unitPrice': {break;}
                    case 'totalPrice': {break;}
                    case 'personInCharge': {break;}
                    case 'enterTime': {break;}
                    case 'updateTime': {break;}
                    case 'submitTime': {break;}
                    case 'submitPerson': {break;}
                    default: break;
                }
                if(queryKey === 'all') {

                }else if(queryKey === 'thingName') {

                }
            }
          }
          return new Promise((resolve, reject) => {
            setTimeout(() => {
                resolve([200, {
                    enterWarehouseKey: enterWarehouseKey,
                    total: total,
                    enterWarehouseRecordsList: enterWarehouseRecordsList
                }]);
            }, 1000);
          });
      });

    //获取用户列表（分页）
    mock.onGet('/user/listpage').reply(config => {
      let {page, name} = config.params;
      let mockUsers = _Users.filter(user => {
        if (name && user.name.indexOf(name) == -1) return false;
        return true;
      });
      let total = mockUsers.length;
      mockUsers = mockUsers.filter((u, index) => index < 20 * page && index >= 20 * (page - 1));
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          resolve([200, {
            total: total,
            users: mockUsers
          }]);
        }, 1000);
      });
    });

    //删除用户
    mock.onGet('/user/remove').reply(config => {
      let { id } = config.params;
      _Users = _Users.filter(u => u.id !== id);
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          resolve([200, {
            code: 200,
            msg: '删除成功'
          }]);
        }, 500);
      });
    });

    //批量删除用户
    mock.onGet('/user/batchremove').reply(config => {
      let { ids } = config.params;
      ids = ids.split(',');
      _Users = _Users.filter(u => !ids.includes(u.id));
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          resolve([200, {
            code: 200,
            msg: '删除成功'
          }]);
        }, 500);
      });
    });

    //编辑用户
    mock.onGet('/user/edit').reply(config => {
      let { id, name, addr, age, birth, sex } = config.params;
      _Users.some(u => {
        if (u.id === id) {
          u.name = name;
          u.addr = addr;
          u.age = age;
          u.birth = birth;
          u.sex = sex;
          return true;
        }
      });
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          resolve([200, {
            code: 200,
            msg: '编辑成功'
          }]);
        }, 500);
      });
    });

    //新增用户
    mock.onGet('/user/add').reply(config => {
      let { name, addr, age, birth, sex } = config.params;
      _Users.push({
        name: name,
        addr: addr,
        age: age,
        birth: birth,
        sex: sex
      });
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          resolve([200, {
            code: 200,
            msg: '新增成功'
          }]);
        }, 500);
      });
    });

    // 获取仓库管理员名单
    mock.onGet('/warehouseManager/list').reply(() => {
        return new Promise((resolve, reject) => {
          setTimeout(() => {
            resolve([200, {
              code: 200,
              warehouseManager: _WareHouseManager
            }]);
          }, 1000);
        });
    });

      // 获取仓库已有物品名单
      mock.onGet('/warehouse/things').reply(() => {
          return new Promise((resolve, reject) => {
              setTimeout(() => {
                  resolve([200, {
                      code: 200,
                      things: _Things
                  }]);
              }, 1000);
          });
      });

  }
};