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

export { LoginUsers, Users, WarehouseManager, Things};
