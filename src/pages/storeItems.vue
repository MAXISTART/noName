<template>
  <f7-page>
    <f7-navbar title="物品明细" back-link="返回" sliding>
      <f7-nav-right>
        <f7-link icon="icon-bars" open-panel="left"></f7-link>
      </f7-nav-right>
    </f7-navbar>
    <f7-searchbar
            cancel-link="取消"
            placeholder="输入要搜索的关键词"
            search-list="#search-list"
            :clear-button="true"
    ></f7-searchbar>
    <f7-list class="searchbar-not-found">
      <f7-list-item title="没匹配到任何物品"></f7-list-item>
    </f7-list>
    <f7-list class="searchbar-found" id="search-list" media-list>
      <f7-list-item v-for="(item, index) in items"
                    :title="item.goodItem.name"
                    :subtitle="'规格：' + item.goodItem.spec"
                    :key="index"
                    media-item v-bind:link="'/storeItemDetail/ ' + item.id"
      >{{ item.number }}{{ item.goodItem.unit }}</f7-list-item>
    </f7-list>
  </f7-page>
</template>

<script>
    import { requestApi, Enum } from '../api/api';
    export default {
        data: function () {
            return {
                items: []
            }
        },
        methods: {
            getStoreData() {
                this.showCustomPreloader();
                // 获取所有数据
                requestApi.store.findAll(this, 0, 999999).then(res => {
                    res = res.body;
                    this.hideCustomPreloader();
                    if(res.code === Enum.SUCCESS.code){
                        this.items = res.data.content;
                    }else{
                        this.$f7.alert(res.msg, '错误信息');
                    }
                }, err => {
                    //msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                    this.hideCustomPreloader();
                    this.$f7.alert(Enum.SYSTEM_ERROR.msg, '错误信息');
                }).then((res) => {

                });
            },
            showCustomPreloader: function(){
                let self = this;
                self.$f7.showPreloader('获取信息中...');
            },
            hideCustomPreloader: function(){
                let self = this;
                self.$f7.hidePreloader();
            }
        },
        mounted () {
            this.getStoreData();
        }
    }
</script>

<style lang="less">

</style>
