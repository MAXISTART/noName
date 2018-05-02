<template>
  <f7-page>
    <f7-navbar title="个人信息" back-link="Forms" sliding>
      <f7-nav-right>
        <f7-link icon="icon-bars" open-panel="left"></f7-link>
      </f7-nav-right>
    </f7-navbar>
    <f7-block>

      <f7-card>
        <f7-card-header>
          当前用户所属部门：{{ userInfo.departmentName }}
        </f7-card-header>
        <f7-card-content>

          <f7-list form>
            <f7-list-item>
              <f7-icon slot="media" f7="social_github"></f7-icon>
              <f7-label>用户昵称</f7-label>
              <f7-input type="text" placeholder="无"  v-model="userInfo.name"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="phone"></f7-icon>
              <f7-label>电话号码</f7-label>
              <f7-input type="text" placeholder="无" v-model="userInfo.phoneNumber"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="lock"></f7-icon>
              <f7-label>账号密码</f7-label>
              <f7-input type="text" placeholder="无" v-model="userInfo.password"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="email"></f7-icon>
              <f7-label>邮箱号码</f7-label>
              <f7-input type="text" placeholder="无" v-model="userInfo.mailbox"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="chats"></f7-icon>
              <f7-label>微信账号</f7-label>
              <f7-input type="text" placeholder="无" v-model="userInfo.weixin"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="chat"></f7-icon>
              <f7-label>qq账号</f7-label>
              <f7-input type="text" placeholder="无" v-model="userInfo.qqNumber"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="data"></f7-icon>
              <f7-label>详情描述</f7-label>
              <f7-input type="textarea" v-model="userInfo.description"></f7-input>
            </f7-list-item>
            <f7-list-item>
              <f7-button class="fullButton" big @click="updateUserInfo">确认修改信息</f7-button>
            </f7-list-item>
          </f7-list>

        </f7-card-content>
        <f7-card-footer>更多信息请联系管理员</f7-card-footer>
      </f7-card>

    </f7-block>
  </f7-page>
</template>

<script>
  import { requestApi, Enum } from '../api/api';
  export default {
    data() {
      return {
          userInfo: JSON.parse(sessionStorage.getItem('user'))
      }
    },
    methods: {
        updateUserInfo: function () {
            this.showCustomPreloader();
            requestApi.user.updateBySelf(this, this.userInfo).then(res => {
                res = res.body;
                if(res.code === Enum.SUCCESS.code){
                    // 更新成功，把更新后的user设置到session中
                    sessionStorage.setItem('user', JSON.stringify(this.userInfo));
                    this.$f7.alert('修改成功', '成功');
                    this.hideCustomPreloader();
                }
                else{
                    // 登录失败，报错
                    this.hideCustomPreloader();
                    this.$f7.alert(res.msg, '错误信息');
                }
            }, error => {
                this.hideCustomPreloader();
                this.$f7.alert(Enum.SYSTEM_ERROR.msg, '错误信息');
            });
        },

        showCustomPreloader: function(){
            let self = this;
            self.$f7.showPreloader('修改用户信息中...');
        },
        hideCustomPreloader: function(){
            let self = this;
            self.$f7.hidePreloader();
        },
    }
  }
</script>

<style lang="less">
  .fullButton {
    width: 100%
  }
</style>
