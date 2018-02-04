import Login from './views/Login.vue'
import NotFound from './views/404.vue'
import Home from './views/Home.vue'
import Main from './views/Main.vue'
import buyThingApply from './views/nav1/buyThingApply';
import Table from './views/nav1/Table.vue'
// import Form from './views/nav1/Form.vue'
import user from './views/nav1/user.vue'
import enterWarehouseRecord from './views/nav2/enterWarehouseRecord.vue'
import Page5 from './views/nav2/Page5.vue'
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
        name: '办公用品库',
        iconCls: 'el-icon-message',//图标样式class
        children: [
            { path: '/main', component: Main, name: '办公用品采购（暂时不用）', hidden: true },
            { path: '/buyThingApply', component: buyThingApply, name: '采购申请表' },
            { path: '/table', component: Table, name: '办公用品申领表' },
            { path: '/user', component: user, name: '列表' },
        ]
    },
    {
        path: '/',
        component: Home,
        name: '库存',
        iconCls: 'fa fa-id-card-o',
        children: [
            { path: '/enterWarehouseRecord', component: enterWarehouseRecord, name: '入库报表' },
            { path: '/page5', component: Page5, name: '库存表' }
        ]
    },
    {
        path: '/',
        component: Home,
        name: '',
        iconCls: 'fa fa-address-card',
        leaf: true,//只有一个节点
        children: [
            { path: '/page6', component: Page6, name: '申领记录' }
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