export default [
    {
        path: '/about/',
        component: require('./pages/about.vue')
    },
    {
        path: '/userInfo/',
        component: require('./pages/userInfo.vue')
    },
    {
        path: '/storeItems/',
        component: require('./pages/storeItems.vue')
    },
    {
        path: '/storeItemDetail/:id',
        component: require('./pages/storeItemDetail.vue')
    },
    {
        path: '/',
        component: require('./pages/mainPage.vue')
    },
]
