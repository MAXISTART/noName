import babelpolyfill from 'babel-polyfill'
import Vue from 'vue'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
//import './assets/theme/theme-green/index.css'
import VueRouter from 'vue-router'
import store from './vuex/store'
import Vuex from 'vuex'
//import NProgress from 'nprogress'
//import 'nprogress/nprogress.css'
import routes from './routes'
import Mock from './mock'
Mock.bootstrap();
import axios from 'axios';
import 'font-awesome/css/font-awesome.min.css'
import $ from 'jquery';
import VueResource from 'vue-resource';
import {msgUtils} from "./api/api";


Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.use(VueResource)
Vue.use(Vuex)

Vue.http.interceptors.push(function(request, next) {//拦截器
// 跨域携带cookie
    request.credentials = true;
    next()
})




//NProgress.configure({ showSpinner: false });
const router = new VueRouter({
  routes
})

// 设置拦截器，判断当前页面是否包含 user 这个属性在，如果不在就回到登录页面
router.beforeEach((to, from, next) => {
  //NProgress.start();
  let user = JSON.parse(sessionStorage.getItem('user'));
  if (to.path == '/login') {
    // 如果用户已经存在，就返回初始页面

    if(user){
        next({ path: '/adminHome' })
    }else{
        next()
    }
  }
  if (!user && to.path != '/login') {
    next({ path: '/login' })
  } else {
    next()
  }
})


var NOT_LOGIN = 11;
//拦截http请求中返回的code，如果登录超时就给提示然后自动跳转
Vue.http.interceptors.push((request, next) => {
    next((response) => {
        //与后台约定登录失效的返回码
        //console.log("返回码：" + response.body.code);
        if (response.body.code === NOT_LOGIN) {
            // 移出浏览器中存储的用户信息
            sessionStorage.removeItem('user');
            // 设置他是因为后台登录超时而被迫需要重新登录
            sessionStorage.setItem('not_login', 'error');
            // 跳转登录界面
            router.push({ path: '/login' });
        }
        return response;
    });
});

//router.afterEach(transition => {
//NProgress.done();
//});

new Vue({
  //el: '#app',
  //template: '<App/>',
  router,
  store,
  //components: { App }
  render: h => h(App)
}).$mount('#app')

