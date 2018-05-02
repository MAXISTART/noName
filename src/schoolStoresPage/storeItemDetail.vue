<template>
  <f7-page>
    <f7-navbar title="物品信息" back-link="Forms" sliding>
      <f7-nav-right>
        <f7-link icon="icon-bars" open-panel="left"></f7-link>
      </f7-nav-right>
    </f7-navbar>
    <f7-block>

      <f7-card>
        <f7-card-header>
          物品名称：&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp   {{ storeItem.goodItem.name }}
        </f7-card-header>
        <f7-card-content>

          <f7-list form>
            <f7-list-item>
              <f7-icon slot="media" f7="document_text"></f7-icon>
              <f7-label>规格</f7-label>
              <f7-input type="text" :placeholder="storeItem.goodItem.spec + ''" readonly="readonly"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="list"></f7-icon>
              <f7-label>当前库存</f7-label>
              <f7-input type="text" :placeholder="storeItem.number + ''" readonly="readonly"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="layers"></f7-icon>
              <f7-label>单位</f7-label>
              <f7-input type="text" :placeholder="storeItem.goodItem.unit + ''" readonly="readonly"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="money_yen"></f7-icon>
              <f7-label>单价</f7-label>
              <f7-input type="text" :placeholder="storeItem.goodItem.price + ''" readonly="readonly"/>
            </f7-list-item>
            <f7-list-item>
              <f7-icon slot="media" f7="menu"></f7-icon>
              <f7-label>总价</f7-label>
              <f7-input type="text" :placeholder="storeItem.totalPrice + ''" readonly="readonly"/>
            </f7-list-item>
          </f7-list>

        </f7-card-content>
        <f7-card-footer class="grid-wrapper">
          <f7-grid class="modals-grid">
            <f7-col width="100">
              <f7-button big fill color="green" @click="addToBuyOrder(storeItem.goodItem)">添加到采购单中</f7-button>
            </f7-col>
          </f7-grid>

          <f7-grid class="modals-grid" >
            <f7-col width="100">
              <f7-button big fill color="green">添加到申领单中</f7-button>
            </f7-col>
          </f7-grid>

          <f7-grid class="modals-grid">
            <f7-col width="100">
              <f7-button big fill color="red">添加到库存操作单中</f7-button>
            </f7-col>
          </f7-grid>


        </f7-card-footer>
      </f7-card>

    </f7-block>
  </f7-page>
</template>

<script>
  import { requestApi, Enum } from '../api/api';
  export default {
    data() {
      return {
          storeItem: {
              goodItem: {
                  name: '',
                  sort: '',
                  price: '',
                  spec: '',
                  number: ''
              },

          }
      }
    },
    methods: {
        addToBuyOrder: function (goodItem) {
            let f7 = this.$f7
            f7.prompt('', '请输入数量', function (value) {
                if(value <= 0){
                    f7.alert('警告！', ' ' + value + ' 小于等于0，不符合要求');
                }else{
                    // 从session中获取临时采购单
                    let tempBuyOrder = JSON.parse(sessionStorage.getItem('tempBuyOrder'));
                    // buyOrderItems 实际上是一个map，storeItemId对应一个buyOrderItem

                    if(!tempBuyOrder){
                        tempBuyOrder = {};
                    }
                    let buyOrderItem = {};
                    buyOrderItem.goodId = goodItem.id;
                    buyOrderItem.name = goodItem.name;
                    buyOrderItem.sort = goodItem.sort;
                    buyOrderItem.spec = goodItem.spec;
                    buyOrderItem.unit = goodItem.unit;
                    buyOrderItem.price = goodItem.price;
                    buyOrderItem.number = value;

                    if(tempBuyOrder.buyOrderItems){
                        tempBuyOrder.buyOrderItems[goodItem.id] = buyOrderItem;
                    }else{
                        tempBuyOrder.buyOrderItems = {};
                        tempBuyOrder.buyOrderItems[goodItem.id] = buyOrderItem;
                    }
                    // console.log(tempBuyOrder);
                    sessionStorage.setItem('tempBuyOrder', JSON.stringify(tempBuyOrder));
                    f7.alert('成功！', '添加至临时采购单成功');
                }
            })
        },
        getStoreItem: function () {
            this.showCustomPreloader();
            // 获取前一个页面传过来的id
            let formData = new FormData();
            formData.append('storeItemId', this.$route.params.id);
            requestApi.store.findOne(this, formData).then(res => {
                res = res.body;
                if(res.code === Enum.SUCCESS.code){
                    this.storeItem = res.data;
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
            self.$f7.showPreloader('获取物品信息中...');
        },
        hideCustomPreloader: function(){
            let self = this;
            self.$f7.hidePreloader();
        },
    },
      mounted () {
        this.getStoreItem();
      }
  }
</script>

<style lang="less">
  .fullButton {
    width: 100%
  }
  .modals-grid{
    margin: 1em 0;
    width: 100%;
  }
  .grid-wrapper {
    width: 100%;
    flex-direction: column;
  }
</style>
