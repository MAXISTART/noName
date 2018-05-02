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
          <f7-link open-popup @click="getAllData">明细一览</f7-link>
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

                    <f7-page infinite-scroll pull-to-refresh @ptr:refresh="refreshBuyData()" @infinite="onScrollBuyOrders">

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
                                                    <f7-swipeout-button @click="changeNumber(item.goodId)">修改</f7-swipeout-button>
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
                                <f7-list-item accordion-item :title="buyOrder.createTime" v-for="(buyOrder, index) in approvedBuyOrders">
                                    <f7-accordion-content>
                                        <f7-list>
                                            <f7-list-item v-for="(item, index) in buyOrder.buyOrderItems"
                                                          :title="item.name + '(' + item.spec + ')' + '[' + item.number + item.unit + ']'"
                                                          :key="index">
                                            </f7-list-item>
                                            <f7-list-item>
                                                <f7-button big fill color="green" style="display: block;text-align: center;width: 100%">审批通过</f7-button>
                                            </f7-list-item>
                                            <f7-list-item>
                                                <f7-button big fill color="red" style="display: block;text-align: center;width: 100%">审批未通过</f7-button>
                                            </f7-list-item>
                                        </f7-list>

                                    </f7-accordion-content>
                                </f7-list-item>
                            </f7-list>
                        </f7-block>

                    </f7-page>

              </f7-tab>
              <f7-tab id="tab2">
                <f7-block>
                  <p>
                    This is tab 2 content
                  </p>
                  <p>
                    Ut ac lobortis lacus, non pellentesque arcu. Quisque sodales sapien malesuada, condimentum nunc at, viverra lacus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vivamus eu pulvinar turpis, id tristique quam. Aenean venenatis molestie diam, sit amet condimentum nisl pretium id. Donec diam tortor, mollis in vehicula id, vehicula consectetur nulla. Quisque posuere rutrum mauris, eu rutrum turpis blandit at. Proin volutpat tortor sit amet metus porttitor accumsan. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Ut dapibus posuere dictum.
                  </p>
                  <p>
                    Fusce luctus turpis nunc, id porta orci blandit eget. Aenean sodales quam nec diam varius, in ornare ipsum condimentum. Aenean eleifend, nulla sit amet volutpat adipiscing, ligula nulla pharetra risus, vitae consequat leo tortor eu nunc. Vivamus at fringilla metus. Duis neque lectus, sagittis in volutpat a, pretium vel turpis. Nam accumsan auctor libero, quis sodales felis faucibus quis. Etiam vestibulum sed nisl vel aliquet. Aliquam pellentesque leo a lacus ultricies scelerisque. Vestibulum vestibulum fermentum tincidunt. Proin eleifend metus non quam pretium, eu vehicula ipsum egestas. Nam eget nibh enim. Etiam sem leo, pellentesque a elit vel, egestas rhoncus enim. Morbi ultricies adipiscing tortor, vitae condimentum lacus hendrerit nec. Phasellus laoreet leo quis purus elementum, ut fringilla justo eleifend. Nunc ultricies a sapien vitae auctor. Aliquam id erat elementum, laoreet est et, dapibus ligula.
                  </p>
                </f7-block>
              </f7-tab>
              <f7-tab id="tab3">
                <f7-block>
                  <p>
                    This is tab 3 content
                  </p>
                  <p>
                    Nulla gravida libero eget lobortis iaculis. In sed elit eu nibh adipiscing faucibus. Sed ac accumsan lacus. In ut diam quis turpis fringilla volutpat. In ultrices dignissim consequat. Cras pretium tortor et lorem condimentum posuere. Nulla facilisi. Suspendisse pretium egestas lacus ac laoreet. Mauris rhoncus quis ipsum quis tristique. Vivamus ultricies urna quis nunc egestas, in euismod turpis fringilla. Nam tellus massa, vehicula eu sapien non, dapibus tempor lorem. Fusce placerat orci arcu, eu dignissim enim porttitor vel. Nullam porttitor vel dolor sed feugiat. Suspendisse potenti. Maecenas ac mattis odio. Sed vel ultricies lacus, sed posuere libero.
                  </p>
                  <p>
                    Nulla gravida libero eget lobortis iaculis. In sed elit eu nibh adipiscing faucibus. Sed ac accumsan lacus. In ut diam quis turpis fringilla volutpat. In ultrices dignissim consequat. Cras pretium tortor et lorem condimentum posuere. Nulla facilisi. Suspendisse pretium egestas lacus ac laoreet. Mauris rhoncus quis ipsum quis tristique. Vivamus ultricies urna quis nunc egestas, in euismod turpis fringilla. Nam tellus massa, vehicula eu sapien non, dapibus tempor lorem. Fusce placerat orci arcu, eu dignissim enim porttitor vel. Nullam porttitor vel dolor sed feugiat. Suspendisse potenti. Maecenas ac mattis odio. Sed vel ultricies lacus, sed posuere libero.
                  </p>
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
        buyLoading: false,
        buyOrderItems: [
          {
              title: 'Swipe left on me please',
              isDelete: true
          }, {
              title: 'Swipe left on me too',
              isDelete: true
          }, {
              title: 'You can\'n delete me',
              isDelete: true
          }
      ],
        approvingBuyOrders: [
            {
                createTime: '无正在审批中的订单',
                buyOrderItems : []
            }
        ],
        approvedBuyOrders: [
            {
                createTime: '无已经审批的订单',
                buyOrderItems : []
            }
        ],
        buyOrderPageSize: 20,
        buyOrderPage: 0,
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
          if (this.buyLoading) {
              return;
          }

          this.buyLoading = true;

          let self = this;

          setTimeout(function () {
              self.loading = false;
              this.f7.alert('scroll!', '');
          }, 1000)

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
                  }else{
                      f7.alert(res.msg, '警告');
                  }
              }, err => {
                  f7.hidePreloader();
                  f7.alert(Enum.SYSTEM_ERROR.msg,'警告');
              });
          });
      },

      changeNumber: function (goodId) {
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
          if(tempBuyOrder){
              // 如果总采购单存在的话
              if(tempBuyOrder.buyOrderItems){
                  // 如果存在明细
                  delete tempBuyOrder.buyOrderItems[goodId];
                  // 删除后还得存回去
                  sessionStorage.setItem('tempBuyOrder', JSON.stringify(tempBuyOrder));
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
                  if(res.data.data.length != 0){
                      if(approval === 2){
                          self.approvingBuyOrders = res.data.data;
                      }else if(approval === -1){
                          self.approvedBuyOrders = res.data.data;
                      }
                  }else{
                      self.approvingBuyOrders = [
                          {
                              createTime: '无正在审批中的订单',
                              buyOrderItems : []
                          }
                      ];
                      self.approvedBuyOrders = [
                          {
                              createTime: '无审批过的订单',
                              buyOrderItems : []
                          }
                      ];
                  }
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
              if(res.code === Enum.USER_LOGIN_SUCCESS.code){
                  // 登录成功，且用户为普通用户
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
          self.getBuyData();
          setTimeout(function () {
              self.$f7.pullToRefreshDone()
          }, 1000)
      },

      getBuyData: function () {
          this.getTempBuyOrderItems();
          this.getBuyOrdersByApprovalResult(2);
          this.getBuyOrdersByApprovalResult(-1);
      },

      getAllData: function() {
          this.getBuyData();
      }
  }
}
</script>

<style lang="less">
  html.ios-gt-8 .navbar .center{
    font-weight: 500;
  }
</style>
