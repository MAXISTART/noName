<template>
  <f7-page>
    <f7-navbar title="操作界面" back-link="返回" sliding>
      <f7-nav-right>
        <f7-link icon="icon-bars" open-panel="left"></f7-link>
      </f7-nav-right>
    </f7-navbar>
    <f7-block>
      <f7-grid class="modals-grid">
        <f7-col width="33">
          <f7-button fill raised color="green" href="/userInfo/">个人信息</f7-button>
        </f7-col>
        <f7-col width="33">
          <f7-button fill raised color="green" href="/storeItems/">物品明细</f7-button>
        </f7-col>
        <f7-col width="33">
          <f7-button fill raised color="green">采购相关</f7-button>
        </f7-col>
      </f7-grid>

      <f7-grid class="modals-grid">
        <f7-col width="45">
          <f7-button fill raised color="green">申领相关</f7-button>
        </f7-col>
        <f7-col width="45">
          <f7-button fill raised color="green">库存操作</f7-button>
        </f7-col>
      </f7-grid>

    </f7-block>
  </f7-page>
</template>

<script>
export default {}
</script>

<style lang="less" scoped>
  .modals-grid{
    margin: 1em 0;
  }
</style>