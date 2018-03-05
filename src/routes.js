import Login from './views/Login.vue'
import NotFound from './views/404.vue'
import Home from './views/Home.vue'
import GoodItem from './views/nav1/GoodItem.vue'
import Department from './views/nav1/Department.vue'
import Permission from './views/nav1/Permission.vue'
import User from './views/nav1/User.vue'
import BuyOrder from './views/nav2/BuyOrder.vue'
import TakeOrder from './views/nav2/TakeOrder.vue'
import Form from './views/nav1/Form.vue'
import Table from './views/nav2/Table.vue'
import Page6 from './views/nav3/Page6.vue'
import echarts from './views/charts/echarts.vue'

let routes = [
    {
        path: '/login',
        component: Login,
        name: '',
        hidden: true
    },
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    //{ path: '/main', component: Main },
    {
        path: '/',
        component: Home,
        name: '基础信息',
        iconCls: 'el-icon-message',//图标样式class
        children: [
            { path: '/department', component: Department, name: '部门' },
            { path: '/user', component: User, name: '用户' },
            { path: '/permission', component: Permission, name: '权限' },
            { path: '/goodItem', component: GoodItem, name: '用品' },
            { path: '/form', component: Form, name: '自定义表格' }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '库存相关',
        iconCls: 'fa fa-id-card-o',
        children: [
            { path: '/buyOrder', component: BuyOrder, name: '采购' },
            { path: '/takeOrder', component: TakeOrder, name: '申领' },
            { path: '/storeItem', component: BuyOrder, name: '库存' },
            { path: '/storeOperation', component: BuyOrder, name: '库存操作' },
            { path: '/table', component: Table, name: '页面5' }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '',
        iconCls: 'fa fa-address-card',
        leaf: true,//只有一个节点
        children: [
            { path: '/page6', component: Page6, name: '导航三' }
        ]
    },
    {
        path: '/',
        component: Home,
        name: 'Charts',
        iconCls: 'fa fa-bar-chart',
        children: [
            { path: '/echarts', component: echarts, name: 'echarts' }
        ]
    },
    {
        path: '*',
        hidden: true,
        redirect: { path: '/404' }
    }
];

export default routes;