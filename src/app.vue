<template>
  <!-- App -->
  <div id="app">

    <!-- Statusbar -->
    <f7-statusbar></f7-statusbar>

    <!-- Left Panel -->
    <f7-panel left cover layout="dark">
      <f7-view id="left-panel-view">
        <f7-pages>
          <f7-page>
            <f7-block-title>LEFT PANEL</f7-block-title>
            <f7-block>
              <p>
                This is a side panel. You can close it by clicking outsite or on this link: <f7-link close-panel="left">close me</f7-link>. You can put here anything, even another isolated view like in <f7-link open-panel="right">Right Panel</f7-link>
              </p>
            </f7-block>
            <f7-block-title>FRAMEWORK7 KITCHEN SINK</f7-block-title>
            <f7-list>
              <f7-list-item v-for="(item, index) in items"
                  media="<i class='icon icon-f7'></i>"
                  link-close-panel
                  link-view=".view-main"
                  :link="item.link"
                  :title="item.title"
                  :key="index"
              ></f7-list-item>
            </f7-list>
            <f7-block>
              <p>
                Long text block goes here. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras sem urna, gravida non scelerisque id, fringilla ac velit. Phasellus elementum a ipsum at ornare. Mauris sagittis rhoncus euismod. Integer convallis augue eu lacus ultrices, in dictum elit consequat. Nulla faucibus massa id felis egestas eleifend. Proin consequat dignissim magna ut scelerisque. Vestibulum ac lorem semper, posuere sapien nec, pharetra massa. Nulla a tellus facilisis, sollicitudin quam porta, aliquam lorem. Fusce dignissim eros ac diam molestie, ut ultrices lorem tristique. Ut facilisis augue ac nisi egestas malesuada. Nunc posuere tortor quis eleifend mollis. Aliquam erat volutpat. Donec feugiat elit tellus, nec convallis orci elementum in. Sed urna mi, vestibulum id tempus id, pretium et ante. Pellentesque eget sollicitudin ligula. Phasellus pellentesque velit eu porta suscipit.
              </p>
            </f7-block>
          </f7-page>
        </f7-pages>
      </f7-view>
    </f7-panel>

    <!-- Right Panel -->
    <f7-panel right reveal layout="dark">
      <f7-view id="right-panel-view" navbar-through :dynamic-navbar="true">
        <f7-navbar v-if="$theme.ios" title="Right Panel" sliding></f7-navbar>
        <f7-pages>
          <f7-page>
            <f7-navbar v-if="$theme.material" title="Right Panel" sliding></f7-navbar>
            <f7-block>
              <p>
                This is a right side panel. You can close it by clicking outsite or on this link: <f7-link close-panel>close me</f7-link>. You can put here anything, even another isolated view, try it:
              </p>
            </f7-block>
            <f7-list>
              <f7-list-item
                title="Right panel page 2"
                link="/sidePanels/panelRight2/"
              ></f7-list-item>
              <f7-list-item
                title="Right panel page 3"
                link="/sidePanels/panelRight3/"
              ></f7-list-item>
            </f7-list>
          </f7-page>
        </f7-pages>       
      </f7-view>
    </f7-panel>

    <!-- Main Views -->
    <f7-views>
      <f7-view id="main-view" navbar-through toolbar-through :dynamic-navbar="true" main>
        <!-- iOS Theme Navbar -->
        <f7-navbar v-if="$theme.ios">
          <f7-nav-center sliding>Framework7</f7-nav-center>
          <f7-nav-right>
            <f7-link icon="icon-bars" open-panel="left"></f7-link>
          </f7-nav-right>
        </f7-navbar>
        <!-- Pages -->
        <f7-pages>
          <main-page></main-page>
        </f7-pages>
        <!-- IOS Theme Toolbar -->
        <f7-toolbar v-if="$theme.ios">
          <f7-link open-popup @click="getFirstData">明细一览</f7-link>
          <f7-link open-popover>Menu</f7-link>
        </f7-toolbar>
      </f7-view>
    </f7-views>

    <!-- popup -->
    <f7-popup tablet-fullscreen>
      <f7-view navbar-fixed>
        <f7-pages>
          <f7-page with-subnavbar>

            <f7-navbar title="总明细">
              <f7-nav-right>
                <f7-link close-popup>退出</f7-link>
              </f7-nav-right>
              <f7-subnavbar>
                <f7-buttons>
                  <f7-button tab-link="#tab1" active>采购明细</f7-button>
                  <f7-button tab-link="#tab2">申领明细</f7-button>
                  <f7-button tab-link="#tab3">库存操作</f7-button>
                </f7-buttons>
              </f7-subnavbar>
            </f7-navbar>
            <f7-tabs>

              <f7-tab id="tab1" active>

                    <f7-page infinite-scroll pull-to-refresh @ptr:refresh="refreshBuyData" @infinite="onScrollBuyOrders">

                        <f7-block>
                            <f7-block-title>临时采购单</f7-block-title>
                            <f7-list accordion inset>
                                <f7-list-item accordion-item title="临时采购单">
                                    <f7-accordion-content>
                                        <f7-list>
                                            <f7-list-item v-for="(item, index) in buyOrderItems"
                                                          swipeout
                                                          :title="item.title"
                                                          swipeout
                                                          @swipeout:deleted="onDeleteBuyOrderItem(item.goodId)"
                                                          :key="index">
                                                <f7-swipeout-actions>
                                                    <f7-swipeout-button @click="changeBuyOrderNumber(item.goodId)">修改</f7-swipeout-button>
                                                    <f7-swipeout-button delete v-if="item.isDelete">删除</f7-swipeout-button>
                                                </f7-swipeout-actions>
                                            </f7-list-item>
                                            <f7-list-item>
                                                <f7-link @click="applyBuyOrder" v-if="buyOrderItems.length > 0" style="display: block;text-align: center;width: 100%">申请此采购单</f7-link>
                                                <f7-link v-if="buyOrderItems.length == 0" style="display: block;text-align: center;width: 100%">当前无申请内容</f7-link>
                                            </f7-list-item>
                                        </f7-list>
                                    </f7-accordion-content>
                                </f7-list-item>
                            </f7-list>
                        </f7-block>


                        <f7-block>
                            <f7-block-title>当前审批中的采购单</f7-block-title>
                            <f7-list accordion inset>
                                <f7-list-item accordion-item :title="buyOrder.createTime" v-for="(buyOrder, index) in approvingBuyOrders">
                                    <f7-accordion-content>
                                        <f7-list>
                                            <f7-list-item v-for="(item, index) in buyOrder.buyOrderItems"
                                                          :title="item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']'"
                                                          :key="index">
                                            </f7-list-item>
                                        </f7-list>
                                    </f7-accordion-content>
                                </f7-list-item>
                            </f7-list>
                        </f7-block>


                        <f7-block>
                            <f7-block-title>已经被审批的采购单</f7-block-title>
                            <f7-list accordion inset>
                                <f7-list-item accordion-item :title="buyOrder.lastmodifiedTime" v-for="(buyOrder, index) in approvedBuyOrders">
                                    <f7-accordion-content>
                                        <f7-list>
                                            <f7-list-item v-for="(item, index) in buyOrder.buyOrderItems"
                                                          :title="item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']'"
                                                          :key="index">
                                            </f7-list-item>
                                            <f7-list-item v-if="buyOrder.approvalResult === 1">
                                                <f7-button big fill color="green" style="display: block;text-align: center;width: 100%" >审批通过</f7-button>
                                            </f7-list-item>
                                            <f7-list-item v-if="buyOrder.approvalResult === 0">
                                                <f7-button big fill color="red" style="display: block;text-align: center;width: 100%" >审批未通过</f7-button>
                                            </f7-list-item>
                                        </f7-list>

                                    </f7-accordion-content>
                                </f7-list-item>
                            </f7-list>
                        </f7-block>

                    </f7-page>

              </f7-tab>
              <f7-tab id="tab2">

                  <f7-page infinite-scroll pull-to-refresh @ptr:refresh="refreshTakeData" @infinite="onScrollTakeOrders">

                      <f7-block>
                          <f7-block-title>临时申领单</f7-block-title>
                          <f7-list accordion inset>
                              <f7-list-item accordion-item title="临时申领单">
                                  <f7-accordion-content>
                                      <f7-list>
                                          <f7-list-item v-for="(item, index) in takeOrderItems"
                                                        swipeout
                                                        :title="item.title"
                                                        swipeout
                                                        @swipeout:deleted="onDeleteTakeOrderItem(item.goodId)"
                                                        :key="index">
                                              <f7-swipeout-actions>
                                                  <f7-swipeout-button @click="changeTakeOrderNumber(item.goodId)">修改</f7-swipeout-button>
                                                  <f7-swipeout-button delete v-if="item.isDelete">删除</f7-swipeout-button>
                                              </f7-swipeout-actions>
                                          </f7-list-item>
                                          <f7-list-item>
                                              <f7-link @click="applyTakeOrder" v-if="takeOrderItems.length > 0" style="display: block;text-align: center;width: 100%">申请此申领单</f7-link>
                                              <f7-link v-if="takeOrderItems.length == 0" style="display: block;text-align: center;width: 100%">当前无申请内容</f7-link>
                                          </f7-list-item>
                                      </f7-list>
                                  </f7-accordion-content>
                              </f7-list-item>
                          </f7-list>
                      </f7-block>


                      <f7-block>
                          <f7-block-title>当前审批中的申领单</f7-block-title>
                          <f7-list accordion inset>
                              <f7-list-item accordion-item :title="takeOrder.createTime" v-for="(takeOrder, index) in approvingTakeOrders">
                                  <f7-accordion-content>
                                      <f7-list>
                                          <f7-list-item v-for="(item, index) in takeOrder.takeOrderItems"
                                                        :title="item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']'"
                                                        :key="index">
                                          </f7-list-item>
                                      </f7-list>
                                  </f7-accordion-content>
                              </f7-list-item>
                          </f7-list>
                      </f7-block>


                      <f7-block>
                          <f7-block-title>已经被审批的申领单</f7-block-title>
                          <f7-list accordion inset>
                              <f7-list-item accordion-item :title="takeOrder.lastmodifiedTime" v-for="(takeOrder, index) in approvedTakeOrders">
                                  <f7-accordion-content>
                                      <f7-list>
                                          <f7-list-item v-for="(item, index) in takeOrder.takeOrderItems"
                                                        :title="item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']'"
                                                        :key="index">
                                          </f7-list-item>
                                          <f7-list-item v-if="takeOrder.approvalResult === 1">
                                              <f7-button big fill color="green" style="display: block;text-align: center;width: 100%" >审批通过</f7-button>
                                          </f7-list-item>
                                          <f7-list-item v-if="takeOrder.approvalResult === 0">
                                              <f7-button big fill color="red" style="display: block;text-align: center;width: 100%" >审批未通过</f7-button>
                                          </f7-list-item>
                                      </f7-list>

                                  </f7-accordion-content>
                              </f7-list-item>
                          </f7-list>
                      </f7-block>

                  </f7-page>

              </f7-tab>

              <f7-tab id="tab3">
                <f7-block>



                </f7-block>
              </f7-tab>
            </f7-tabs>
          </f7-page>

        </f7-pages>
      </f7-view>
    </f7-popup>

    <!-- Login Screen -->
    <f7-login-screen id="login-screen">
      <f7-view>
        <f7-pages>
           <f7-page login-screen>
            <f7-login-screen-title>Framework7</f7-login-screen-title>
            <f7-list form>
              <f7-list-item>
                <f7-label>Username</f7-label>
                <f7-input name="username" placeholder="Your username" type="text" v-model="username"></f7-input>
              </f7-list-item>
              <f7-list-item>
                <f7-label>Password</f7-label>
                <f7-input name="password" type="password" placeholder="Your password" v-model="password"></f7-input>
              </f7-list-item>
            </f7-list>
            <f7-list>
              <f7-list-button title="Sign In" @click="closeLogin"></f7-list-button>
              <f7-list-label>
                <div>Some text about login information.</div>
                <div>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</div>
              </f7-list-label>
            </f7-list>
          </f7-page> 
        </f7-pages>
      </f7-view>
    </f7-login-screen>

    <f7-popover style="width: 240px">
      <f7-list>
        <f7-list-button v-for="(btn, index) in popoverBtns" 
          :href="btn.link"
          :key="index"
          close-popover
        >{{btn.title}}</f7-list-button>
      </f7-list>
    </f7-popover>
  </div>
</template>

<script>
import MainPage from './pages/mainPage.vue'
import { requestApi, Enum, TimeUtil } from './api/api';

export default {
  data () {
    return {
      username: '15521394967',
      password: '123456',
        dataPreLoaded: false,

        // 属于采购板块的
        buyOrderDataFinished: false,
        buyOrderItems: [],
        approvingBuyOrders: [],
        approvedBuyOrders: [],
        buyOrderPageSize: 10,
        buyOrderPage: 0,

        // 属于申领板块的
        takeOrderDataFinished: false,
        takeOrderItems: [],
        approvingTakeOrders: [],
        approvedTakeOrders: [],
        takeOrderPageSize: 10,
        takeOrderPage: 0,


      items: [
        {
          title: 'Forms',
          link: '/forms/'
        }, {
          title: 'List View',
          link: '/listView/'
        }, {
          title: 'Media Lists',
          link: '/mediaLists/'
        }, {
          title: 'Modals',
          link: '/modals/'
        }, {
          title: 'Navbars And Toolbars',
          link: '/navbarsAndToolbars/'
        }, {
          title: 'Popover',
          link: '/popover/'
        }, {
          title: 'Side Panels',
          link: '/sidePanels/'
        }, {
          title: 'Swipe To Delete',
          link: '/swipeToDelete/',
        }, {
          title: 'Swiper Slider',
          link: '/swiperSlider/',          
        }, {
          title: 'Tabs',
          link: '/tabs/'
        }
      ],
      popoverBtns: [
        {
          title: 'Modals',
          link: '/modals/'
        }, {
          title: 'Popover',
          link: '/popover/'
        }, {
          title: 'Tabs',
          link: '/tabs/'
        }, {
          title: 'Side Panels',
          link: '/sidePanels/'
        }, {
          title: 'List View',
          link: '/listView/'
        }, {
          title: 'Forms',
          link: '/forms/'
        }
      ]
    }
  },
  components: { MainPage },
  methods: {

      onScrollBuyOrders: function () {
          // 滚动加载的，是数组push的类型，并不是重新赋值
          let self = this;
          // 下面是获取审核中的单和已经通过的单
          // 审批结果，-1表示已经审核了（包含通过和未通过），2表示还未审核
          self.buyOrderPage += 1;
          let formData  = new FormData();
          formData .append('approvalResult', -1);
          formData .append('page', self.buyOrderPage);
          formData .append('size', self.buyOrderPageSize);
          let user = JSON.parse(sessionStorage.getItem('user'));
          formData .append('requestorId', user.id);

          // self.showCustomPreloader();
          requestApi.buyOrder.findByParam(self, formData).then(res => {
              res = res.body;
              // self.hideCustomPreloader();
              if(res.code === Enum.SUCCESS.code){
                  if(res.data.data.length != 0){
                      self.buyOrderDataFinished = false;
                      console.log(res.data);
                      for(let i = 0; i < res.data.data.length; i++){
                          self.approvedBuyOrders.push(res.data.data[i]);
                      }
                  }else if( res.data.total <= ((self.buyOrderPage + 1) * self.buyOrderPageSize)){
                      // 数据已经记载完
                      self.buyOrderDataFinished = true;
                      //self.$f7.alert('已经没有更多数据', '提醒');
                  }
                  self.computeData();
              }
              else{
                  self.$f7.alert(res.msg, '错误信息');
              }
          }, error => {
              // self.hideCustomPreloader();
              self.$f7.alert(Enum.SYSTEM_ERROR.msg, '错误信息');
          });
      },

      applyBuyOrder: function () {
          let f7 = this.$f7;
          let self = this;
          f7.prompt('', '请描述下该采购单', function (description) {
              f7.showPreloader('添加采购单中...');
              let tempBuyOrder = JSON.parse(sessionStorage.getItem('tempBuyOrder'));
              let buyOrderItems = [];
              for(let itemName in tempBuyOrder.buyOrderItems){
                  let item = tempBuyOrder.buyOrderItems[itemName];
                  buyOrderItems.push(item);
              }
              // 这个并不会存回sessionStorage，所以并不影响sessionStorage中的tempBuyOrder
              tempBuyOrder.buyOrderItems = buyOrderItems;
              // 接下来填充其他信息
              let userInfo = JSON.parse(sessionStorage.getItem('user'));
              tempBuyOrder.departmentId = userInfo.departmentId;
              tempBuyOrder.description = description;

              requestApi.buyOrder.add(self, tempBuyOrder).then(res => {
                  res = res.body;
                  f7.hidePreloader();
                  if(res.code === Enum.SUCCESS.code){
                      f7.alert('成功', '添加采购单成功');
                      tempBuyOrder = null;
                      sessionStorage.setItem('tempBuyOrder', JSON.stringify(tempBuyOrder));
                      self.getTempBuyOrderItems();
                      self.getBuyOrdersByApprovalResult(2);
                      self.computeData();
                  }else{
                      f7.alert(res.msg, '警告');
                  }
              }, err => {
                  f7.hidePreloader();
                  f7.alert(Enum.SYSTEM_ERROR.msg,'警告');
              });
          });

      },

      changeBuyOrderNumber: function (goodId) {
          var self = this;
          let f7 = this.$f7
          f7.prompt('', '请输入数量', function (value) {
              if(value <= 0){
                  f7.alert('警告！', ' ' + value + ' 小于等于0，不符合要求');
              }else{
                  // 从session中获取临时采购单
                  let tempBuyOrder = JSON.parse(sessionStorage.getItem('tempBuyOrder'));
                  if(!tempBuyOrder){
                      f7.alert('出错，请刷新页面', '出错！');
                  }
                  if(tempBuyOrder.buyOrderItems){
                      tempBuyOrder.buyOrderItems[goodId].number = value;
                  }else{
                      f7.alert('出错，请刷新页面', '出错！');
                  }
                  // console.log(tempBuyOrder);
                  sessionStorage.setItem('tempBuyOrder', JSON.stringify(tempBuyOrder));
                  f7.alert('成功！', '修改采购单成功');
                  // 更新数据
                  self.getTempBuyOrderItems();
              }
          })
      },

      onDeleteBuyOrderItem: function (goodId) {
          // 删除采购单明细中的一个数据
          let tempBuyOrder = JSON.parse(sessionStorage.getItem('tempBuyOrder'));
          let self = this;
          if(tempBuyOrder){
              // 如果总采购单存在的话
              if(tempBuyOrder.buyOrderItems){
                  // 如果存在明细
                  delete tempBuyOrder.buyOrderItems[goodId];
                  // 删除后还得存回去,因为删除的动画只是表面，实际并没有真正删除
                  sessionStorage.setItem('tempBuyOrder', JSON.stringify(tempBuyOrder));
                  self.getTempBuyOrderItems();
              }
          }
      },

      getTempBuyOrderItems: function() {
          let tempBuyOrder = JSON.parse(sessionStorage.getItem('tempBuyOrder'));
          let items = [];
          let self = this;
          if(tempBuyOrder){
              // 如果总采购单存在的话
              if(tempBuyOrder.buyOrderItems){
                  // 如果存在明细
                  for(let itemName in tempBuyOrder.buyOrderItems){
                      let item = tempBuyOrder.buyOrderItems[itemName];
                      item.title = item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']';
                      item.isDelete = true;
                      items.push(item);
                  }
              }
          }
          self.buyOrderItems = items;
      },

      getBuyOrdersByApprovalResult(approval) {
          let self = this;
          // 下面是获取审核中的单和已经通过的单
          // 审批结果，-1表示已经审核了（包含通过和未通过），2表示还未审核
          let formData  = new FormData();
          if(approval === 2){
              formData .append('page', 0);
              formData .append('size', 999);
          }else if( approval === -1 ){
              formData .append('page', self.buyOrderPage);
              formData .append('size', self.buyOrderPageSize);
          }

          formData .append('approvalResult', approval);

          let user = JSON.parse(sessionStorage.getItem('user'));
          formData .append('requestorId', user.id);

          // self.showCustomPreloader();
          requestApi.buyOrder.findByParam(self, formData).then(res => {
              res = res.body;
              // self.hideCustomPreloader();
              if(res.code === Enum.SUCCESS.code){
                  console.log(res.data);
                  if(approval === 2){
                      self.approvingBuyOrders = res.data.data;
                  }else if(approval === -1){
                      self.approvedBuyOrders = res.data.data;
                  }
                  self.buyOrderDataFinished = true;
                  self.computeData();
              }
              else{
                  self.$f7.alert(res.msg, '错误信息');
              }
          }, error => {
              // self.hideCustomPreloader();
              self.$f7.alert(Enum.SYSTEM_ERROR.msg, '错误信息');
          });

      },


      onScrollTakeOrders: function () {
          // 滚动加载的，是数组push的类型，并不是重新赋值
          let self = this;
          // 下面是获取审核中的单和已经通过的单
          // 审批结果，-1表示已经审核了（包含通过和未通过），2表示还未审核
          self.takeOrderPage += 1;
          let formData  = new FormData();
          formData .append('approvalResult', -1);
          formData .append('page', self.takeOrderPage);
          formData .append('size', self.takeOrderPageSize);
          let user = JSON.parse(sessionStorage.getItem('user'));
          formData .append('requestorId', user.id);

          // self.showCustomPreloader();
          requestApi.takeOrder.findByParam(self, formData).then(res => {
              res = res.body;
              // self.hideCustomPreloader();
              if(res.code === Enum.SUCCESS.code){
                  if(res.data.data.length != 0){
                      self.takeOrderDataFinished = false;
                      console.log(res.data);
                      for(let i = 0; i < res.data.data.length; i++){
                          self.approvedTakeOrders.push(res.data.data[i]);
                      }
                  }else if( res.data.total <= ((self.takeOrderPage + 1) * self.takeOrderPageSize)){
                      // 数据已经记载完
                      self.takeOrderDataFinished = true;
                      //self.$f7.alert('已经没有更多数据', '提醒');
                  }
                  self.computeData();
              }
              else{
                  self.$f7.alert(res.msg, '错误信息');
              }
          }, error => {
              // self.hideCustomPreloader();
              self.$f7.alert(Enum.SYSTEM_ERROR.msg, '错误信息');
          });
      },

      applyTakeOrder: function () {
          let f7 = this.$f7;
          let self = this;
          f7.prompt('', '请描述下该采购单', function (description) {
              f7.showPreloader('添加采购单中...');
              let tempTakeOrder = JSON.parse(sessionStorage.getItem('tempTakeOrder'));
              let takeOrderItems = [];
              for(let itemName in tempTakeOrder.takeOrderItems){
                  let item = tempTakeOrder.takeOrderItems[itemName];
                  takeOrderItems.push(item);
              }
              // 这个并不会存回sessionStorage，所以并不影响sessionStorage中的tempTakeOrder
              tempTakeOrder.takeOrderItems = takeOrderItems;
              // 接下来填充其他信息
              let userInfo = JSON.parse(sessionStorage.getItem('user'));
              tempTakeOrder.departmentId = userInfo.departmentId;
              tempTakeOrder.description = description;

              requestApi.takeOrder.add(self, tempTakeOrder).then(res => {
                  res = res.body;
                  f7.hidePreloader();
                  if(res.code === Enum.SUCCESS.code){
                      f7.alert('成功', '添加采购单成功');
                      tempTakeOrder = null;
                      sessionStorage.setItem('tempTakeOrder', JSON.stringify(tempTakeOrder));
                      self.getTempTakeOrderItems();
                      self.getTakeOrdersByApprovalResult(2);
                      self.computeData();
                  }else{
                      f7.alert(res.msg, '警告');
                  }
              }, err => {
                  f7.hidePreloader();
                  f7.alert(Enum.SYSTEM_ERROR.msg,'警告');
              });
          });

      },

      changeTakeOrderNumber: function (goodId) {
          var self = this;
          let f7 = this.$f7
          f7.prompt('', '请输入数量', function (value) {
              if(value <= 0){
                  f7.alert('警告！', ' ' + value + ' 小于等于0，不符合要求');
              }else{
                  // 从session中获取临时采购单
                  let tempTakeOrder = JSON.parse(sessionStorage.getItem('tempTakeOrder'));
                  if(!tempTakeOrder){
                      f7.alert('出错，请刷新页面', '出错！');
                  }
                  if(tempTakeOrder.takeOrderItems){
                      tempTakeOrder.takeOrderItems[goodId].number = value;
                  }else{
                      f7.alert('出错，请刷新页面', '出错！');
                  }
                  // console.log(tempTakeOrder);
                  sessionStorage.setItem('tempTakeOrder', JSON.stringify(tempTakeOrder));
                  f7.alert('成功！', '修改采购单成功');
                  // 更新数据
                  self.getTempTakeOrderItems();
              }
          })
      },

      onDeleteTakeOrderItem: function (goodId) {
          // 删除采购单明细中的一个数据
          let tempTakeOrder = JSON.parse(sessionStorage.getItem('tempTakeOrder'));
          let self = this;
          if(tempTakeOrder){
              // 如果总采购单存在的话
              if(tempTakeOrder.takeOrderItems){
                  // 如果存在明细
                  delete tempTakeOrder.takeOrderItems[goodId];
                  // 删除后还得存回去,因为删除的动画只是表面，实际并没有真正删除
                  sessionStorage.setItem('tempTakeOrder', JSON.stringify(tempTakeOrder));
                  self.getTempTakeOrderItems();
              }
          }
      },

      getTempTakeOrderItems: function() {
          let tempTakeOrder = JSON.parse(sessionStorage.getItem('tempTakeOrder'));
          let items = [];
          let self = this;
          if(tempTakeOrder){
              // 如果总采购单存在的话
              if(tempTakeOrder.takeOrderItems){
                  // 如果存在明细
                  for(let itemName in tempTakeOrder.takeOrderItems){
                      let item = tempTakeOrder.takeOrderItems[itemName];
                      item.title = item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']';
                      item.isDelete = true;
                      items.push(item);
                  }
              }
          }
          self.takeOrderItems = items;
      },

      getTakeOrdersByApprovalResult(approval) {
          let self = this;
          // 下面是获取审核中的单和已经通过的单
          // 审批结果，-1表示已经审核了（包含通过和未通过），2表示还未审核
          let formData  = new FormData();
          if(approval === 2){
              formData .append('page', 0);
              formData .append('size', 999);
          }else if( approval === -1 ){
              formData .append('page', self.takeOrderPage);
              formData .append('size', self.takeOrderPageSize);
          }

          formData .append('approvalResult', approval);

          let user = JSON.parse(sessionStorage.getItem('user'));
          formData .append('requestorId', user.id);

          // self.showCustomPreloader();
          requestApi.takeOrder.findByParam(self, formData).then(res => {
              res = res.body;
              // self.hideCustomPreloader();
              if(res.code === Enum.SUCCESS.code){
                  console.log(res.data);
                  if(approval === 2){
                      self.approvingTakeOrders = res.data.data;
                  }else if(approval === -1){
                      self.approvedTakeOrders = res.data.data;
                  }
                  self.takeOrderDataFinished = true;
                  self.computeData();
              }
              else{
                  self.$f7.alert(res.msg, '错误信息');
              }
          }, error => {
              // self.hideCustomPreloader();
              self.$f7.alert(Enum.SYSTEM_ERROR.msg, '错误信息');
          });

      },


      closeLogin: function () {
          this.showCustomPreloader();
          var loginParams = { name: this.username, password: this.password };
          requestApi.user.login(this, loginParams).then(res => {
              this.logining = false;
              res = res.body;
              if(res.code === Enum.USER_LOGIN_SUCCESS.code || res.code === Enum.ADMIN_LOGIN_SUCCESS.code){
                  // 登录成功
                  sessionStorage.setItem('user', JSON.stringify(res.data));
                  this.hideCustomPreloader();
                  window.f7.closeModal('#login-screen')
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


/*          this.$f7.alert(loginText, 'Framework7', function () {
            window.f7.closeModal('#login-screen')
          })*/

      },

      showCustomPreloader: function(){
          let self = this;
          self.$f7.showPreloader('获取数据中...');
      },
      hideCustomPreloader: function(){
          let self = this;
          self.$f7.hidePreloader();
      },

      refreshBuyData: function () {
          let self = this;
          // 刷心的话会重置page
          self.buyOrderPage = 0;
          self.getBuyData();
          setTimeout(function () {
              self.$f7.pullToRefreshDone()
          }, 1000)
      },

      refreshTakeData: function () {
          let self = this;
          // 刷心的话会重置page
          self.takeOrderPage = 0;
          self.getTakeData();
          setTimeout(function () {
              self.$f7.pullToRefreshDone()
          }, 1000)
      },

      getBuyData: function () {
          // 此方法获取得到的数据是直接赋值进去的而不是填充进去的
          this.getTempBuyOrderItems();
          this.getBuyOrdersByApprovalResult(2);
          this.getBuyOrdersByApprovalResult(-1);
      },

      getTakeData: function () {
          // 此方法获取得到的数据是直接赋值进去的而不是填充进去的
          this.getTempTakeOrderItems();
          this.getTakeOrdersByApprovalResult(2);
          this.getTakeOrdersByApprovalResult(-1);
      },

      getFirstData: function () {
          // 第一次加载时的data，根据当前状态是否有变化来判断的
          let self = this;
          this.getTempBuyOrderItems();
          this.getTempTakeOrderItems();
          if(!self.dataPreLoaded){
              self.getAllData();
              self.dataPreLoaded = true;
          }
      },

      computeData: function () {
          // 该方法类似生命周期里面的最后一步，无论是哪种加载，最终都要进行判断数据是否为空
          let self = this;
          let approvingDefault = [
              {
                  createTime: '无正在审批中的订单',
                  buyOrderItems : []
              }
          ];
          let approvedDefault = [
              {
                  createTime: '无审批过的订单',
                  buyOrderItems : []
              }
          ];
          if(self.approvingBuyOrders.length === 0){
              self.approvingBuyOrders = approvingDefault;
          }
          if(self.approvedBuyOrders.length === 0){
              self.approvedBuyOrders = approvedDefault;
          }
          if(self.approvingTakeOrders.length === 0){
              self.approvingTakeOrders = approvingDefault;
          }
          if(self.approvedTakeOrders.length === 0){
              self.approvedTakeOrders = approvedDefault;
          }

          // 检查数据是否加载完了
          if(self.buyOrderDataFinished){
              // 如果已经结束，就需要去除加载符号，但是滑动触发不能去掉，因为别的地方还用到
              // self.$f7.detachInfiniteScroll(self.$$('.infinite-scroll'));
              self.$$('.infinite-scroll-preloader').eq(0).hide();
          }else {
              // 添加下拉触发以及下拉图标
              // self.$f7.attachInfiniteScroll(self.$$('.infinite-scroll'));
              self.$$('.infinite-scroll-preloader').eq(0).show();
          }

          // 检查数据是否加载完了
          if(self.takeOrderDataFinished){
              // 如果已经结束，就需要去除加载符号，但是滑动触发不能去掉，因为别的地方还用到
              // self.$f7.detachInfiniteScroll(self.$$('.infinite-scroll'));
              self.$$('.infinite-scroll-preloader').eq(1).hide();
          }else {
              // 添加下拉触发以及下拉图标
              // self.$f7.attachInfiniteScroll(self.$$('.infinite-scroll'));
              self.$$('.infinite-scroll-preloader').eq(1).show();
          }
      },

      getAllData: function() {
          this.getBuyData();
          this.getTakeData();
      }
  }
}
</script>

<style lang="less">
  html.ios-gt-8 .navbar .center{
    font-weight: 500;
  }
</style>
