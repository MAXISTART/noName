<template>
  <f7-page name="main">
    <f7-navbar>
      <f7-nav-center sliding>校园仓库</f7-nav-center>
      <f7-nav-right>
        <f7-link icon="icon-bars" open-panel="right"></f7-link>
      </f7-nav-right>
    </f7-navbar>
    <f7-block>
      <f7-button href="/about/">关于该系统</f7-button>
    </f7-block>
    <f7-block-title>常用功能</f7-block-title>
    <f7-list>
      <f7-list-item
        link-ignore-cache
        media="<i class='f7-icons'>info</i>"
        link="/userInfo/"
        title="个人信息">
      </f7-list-item>
      <f7-list-item
              link-ignore-cache
              media="<i class='f7-icons'>list</i>"
              link="/storeItems/"
              title="物品信息">
      </f7-list-item>
    </f7-list>
  </f7-page>
</template>

<script>
export default {
  data () {
    return {

    }
  },
  mounted () {

  }
}
</script>