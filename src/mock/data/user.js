import Mock from 'mockjs';
const LoginUsers = [
  {
    id: 1,
    username: 'admin',
    password: '123456',
    avatar: 'https://raw.githubusercontent.com/taylorchen709/markdown-images/master/vueadmin/user.png',
    name: '张某某'
  }
];

const Users = [];
const WarehouseManager = [];
const Things = ['铅笔', '橡皮擦', '刀'];
const enterWarehouseRecords = [];

for (let i = 0; i < 86; i++) {
  Users.push(Mock.mock({
    id: Mock.Random.guid(),
    name: Mock.Random.cname(),
    addr: Mock.mock('@county(true)'),
    'age|18-60': 1,
    birth: Mock.Random.date(),
    sex: Mock.Random.integer(0, 1)
  }));
}

/* 仓库负责人名单 */
for(let i = 0; i < 10; i++) {
    WarehouseManager.push(Mock.mock({
        id: Mock.Random.guid(),
        warehouseManagerName: Mock.Random.cname()
    }));
}

/* 入库记录 */
for(let i = 0; i < 100; i++) {
    enterWarehouseRecords.push(Mock.mock({
        id: Mock.Random.guid(),
        thingName: Mock.Random.cname(),
        type: Mock.Random.cname(),
        size: Mock.Random.cname(),
        unit: Mock.Random.cword(1, 1),
        total: Mock.Random.natural(1, 10000),
        unitPrice: Mock.Random.float( 1, 1000000, 2, 2),
        totalPrice: Mock.Random.float( 1, 9007199254740992, 2, 2),
        personInCharge: Mock.Random.cname(),
        enterTime: Mock.Random.date(),
        updateTime: Mock.Random.date(),
        submitTime: Mock.Random.date(),
        submitPerson: Mock.Random.cname()
    }));
}

export { LoginUsers, Users, WarehouseManager, Things, enterWarehouseRecords};
