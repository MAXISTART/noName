<template>
  <el-form :model="ruleForm2" :rules="rules2" ref="ruleForm2" label-position="left" label-width="0px" class="demo-ruleForm login-container">
    <h3 class="title">系统登录</h3>
    <el-form-item prop="account">
      <el-input type="text" v-model="ruleForm2.account" auto-complete="off" placeholder="账号"></el-input>
    </el-form-item>
    <el-form-item prop="checkPass">
      <el-input type="password" v-model="ruleForm2.checkPass" auto-complete="off" placeholder="密码"></el-input>
    </el-form-item>
    <el-checkbox v-model="checked" checked class="remember">记住密码</el-checkbox>
    <el-form-item style="width:100%;">
      <el-button type="primary" style="width:100%;" @click.native.prevent="handleSubmit2" :loading="logining">登录</el-button>
      <!--<el-button @click.native.prevent="handleReset2">重置</el-button>-->
    </el-form-item>
  </el-form>
</template>

<script>
  import { requestApi, Enum, msgUtils } from '../api/api';
  //import NProgress from 'nprogress'
  export default {
    data() {
      // 判断是否因为登录超时而跳转过来的
      if(sessionStorage.getItem('not_login')){
          if(sessionStorage.getItem('not_login') === 'error'){
              // 弹框提示
              msgUtils.warning(this, Enum.NOT_LOGIN.msg);
          }
      }
      return {
        logining: false,
        ruleForm2: {
          account: 'admin',
          checkPass: '123456'
        },
        rules2: {
          account: [
            { required: true, message: '请输入账号', trigger: 'blur' },
            //{ validator: validaePass }
          ],
          checkPass: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            //{ validator: validaePass2 }
          ]
        },
        checked: true
      };
    },
    methods: {

      handleReset2() {
        this.$refs.ruleForm2.resetFields();
      },

      handleSubmit2(ev) {
          const self = this;
        this.$refs.ruleForm2.validate((valid) => {
          if (valid) {
            //_this.$router.replace('/table');
            this.logining = true;
            var loginParams = { name: this.ruleForm2.account, password: this.ruleForm2.checkPass };
              requestApi.user.login(this, loginParams).then(res => {
              this.logining = false;
              res = res.body;
              if(res.code === Enum.ADMIN_LOGIN_SUCCESS.code){
                // 登录成功，且用户为管理员
                sessionStorage.setItem('user', JSON.stringify(res.data));
                self.$router.push({ path: '/adminHome' });
              }
              else if(res.code == Enum.USER_LOGIN_SUCCESS.code){
                // 登录成功，且用户为普通用户
                  sessionStorage.setItem('user', JSON.stringify(res.data));
                  sessionStorage.removeItem('not_login');
                  self.$router.push({ path: '/userHome' });
              }
              else{
                // 登录失败，报错
                  msgUtils.warning(this, res.msg);
              }
            }, error => {
                  msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
            });
          } else {
            return false;
          }
        });
      }
    }
  }

</script>

<style lang="scss" scoped>
  .login-container {
    /*box-shadow: 0 0px 8px 0 rgba(0, 0, 0, 0.06), 0 1px 0px 0 rgba(0, 0, 0, 0.02);*/
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
    .title {
      margin: 0px auto 40px auto;
      text-align: center;
      color: #505458;
    }
    .remember {
      margin: 0px 0px 35px 0px;
    }
  }
</style>