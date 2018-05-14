<template>
  <f7-page>
    <f7-navbar title="说明" back-link="返回" sliding>
      <f7-nav-right>
        <f7-link icon="icon icon-bars" open-panel="left"></f7-link>
      </f7-nav-right>
    </f7-navbar>
    <f7-block-title>功能总体概述</f7-block-title>
    <f7-block>
      <p>1. 用户添加采购单和</p>
    </f7-block>

    <f7-block-title>采购单下单示例</f7-block-title>
    <f7-timeline>
      <f7-timeline-item v-for="(item, index) in items1"
        inner
        :day="item.day"
        :month="item.month"
        :content="item.content"
        :key="index"
      ></f7-timeline-item>
    </f7-timeline>
    <f7-block-title>SIDE BY SIDE</f7-block-title>
    <f7-timeline sides>
      <f7-timeline-item v-for="(item, index) in items2"
        inner
        :day="item.day"
        :month="item.month"
        :content="item.content"
        :key="index"
      ></f7-timeline-item>
    </f7-timeline>
    <f7-block-title>ONLY TABLET SIDE BY SIDE</f7-block-title>
    <f7-timeline tablet-sides>
      <f7-timeline-item v-for="(item, index) in items2"
        inner
        :day="item.day"
        :month="item.month"
        :content="item.content"
        :key="index"
      ></f7-timeline-item>
    </f7-timeline>
    <f7-block-title>FORCED SIDES</f7-block-title>
    <f7-timeline sides>
      <f7-timeline-item v-for="(item, index) in items3"
        inner
        :side="item.side"
        :day="item.day"
        :month="item.month"
        :content="item.content"
        :key="index"
      ></f7-timeline-item>
    </f7-timeline>
    <f7-block-title>RICH CONTENT</f7-block-title>
    <f7-timeline>
      <f7-timeline-item day="21" month="DEC" inner>
        <div class="timeline-item-time">12:56</div>
        <div class="timeline-item-title">Item Title</div>
        <div class="timeline-item-subtitle">Item Subtitle</div>
        <div class="timeline-item-text">
          Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolor fugiat ipsam hic porro enim, accusamus perferendis, quas commodi alias quaerat eius nemo deleniti. Odio quasi quos quis iure, aperiam pariatur?
        </div>
        <div class="timeline-item-time">15:07</div>
        <div class="timeline-item-title">Item Title</div>
        <div class="timeline-item-subtitle">Item Subtitle</div>
        <div class="timeline-item-text">
          Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolor fugiat ipsam hic porro enim, accusamus perferendis, quas commodi alias quaerat eius nemo deleniti. Odio quasi quos quis iure, aperiam pariatur?
        </div>
      </f7-timeline-item>
      <f7-timeline-item day="22" month="DEC">
        <div class="timeline-item-inner">
          <div class="timeline-item-time">12:56</div>
          <div class="timeline-item-title">Item Title</div>
          <div class="timeline-item-subtitle">Item Subtitle</div>
          <div class="timeline-item-text">
            Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolor fugiat ipsam hic porro enim, accusamus perferendis, quas commodi alias quaerat eius nemo deleniti. Odio quasi quos quis iure, aperiam pariatur?
          </div>
        </div>
        <div class="timeline-item-inner">
          <div class="timeline-item-time">15:07</div>
          <div class="timeline-item-title">Item Title</div>
          <div class="timeline-item-subtitle">Item Subtitle</div>
          <div class="timeline-item-text">
            Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dolor fugiat ipsam hic porro enim, accusamus perferendis, quas commodi alias quaerat eius nemo deleniti. Odio quasi quos quis iure, aperiam pariatur?
          </div>
        </div>
      </f7-timeline-item>
      <f7-timeline-item day="23" month="DEC" class="timeline-card">
        <f7-card title="Card Header" content="Card Content" footer="Card Footer"></f7-card>  
      </f7-timeline-item>
      <f7-timeline-item day="24" month="DEC" class="timeline-list">
        <f7-list inset>
          <f7-list-item link="#">Item 1</f7-list-item>
          <f7-list-item link="#">Item 1</f7-list-item>
          <f7-list-item link="#">Item 1</f7-list-item>
        </f7-list>
      </f7-timeline-item>
      <f7-timeline-item day="25" month="DEC" content="Plain text"></f7-timeline-item>
    </f7-timeline>
    <f7-block-title>INSIDE CONTENT BLOCK</f7-block-title>
    <f7-block inner>
      <f7-timeline>
        <f7-timeline-item v-for="(item, index) in items1"
          inner
          :day="item.day"
          :month="item.month"
          :content="item.content"
          :key="index"
        ></f7-timeline-item>
      </f7-timeline>
    </f7-block>
  </f7-page>
</template>

<script>
export default {
  data () {
    return {
      items1: [
        {
          day: '1.',
          month: '登录',
          content: '无论如何先登录系统'
        }, {
          day: '2.',
          month: '选择物品',
          content: '进入“物品明细”，选择物品然后点击“添加到采购单中”，在输入数量后，屏幕提示“添加成功”后就可以后退继续选择要添加的物品，注意！这里是可以一次选择多种物品进行添加，尽量做到一个采购单含多种物品，'
        }, {
          day: '3.',
          month: '修改采购单',
          content: '点击屏幕左下角的“我的明细”，并且选择采购明细的那一页，点击“临时采购单”，可以查看上一步中自己所添加的物品以及数量，这里可以修改刚才所填写的数量，同时也可以删除不要的物品，只需要向右滑动想操作的条目即可看到'
        }, {
          day: '4.',
          month: '申请采购单',
          content: '如果确定采购单无误，即可点击申请此采购单，需要对采购单进行下描述，尽可能地描述清楚这个单的物品的用途。'
        },{
          day: '5.',
          month: '查看',
          content: '刚才的采购单添加成功后会进入审批过程，在“当前审批的采购单”中，你能在最上面看到刚才你所申请的单子，标题就是申请时间'
        }
      ],
      items2: [
        {
          day: '21',
          month: 'DEC',
          content: 'Some text goes here'
        }, {
          day: '22',
          month: 'DEC',
          content: 'Another text goes here'
        }, {
          day: '23',
          month: 'DEC',
          content: 'Just plain text'
        }, {
          day: '24',
          month: 'DEC',
          content: 'One more text here'
        }
      ],
      items3: [
        {
          side: 'right',
          day: '21',
          month: 'DEC',
          content: 'Some text goes here'
        }, {
          side: 'right',
          day: '22',
          month: 'DEC',
          content: 'Another text goes here'
        }, {
          side: 'left',
          day: '23',
          month: 'DEC',
          content: 'Just plain text'
        }, {
          side: 'left',
          day: '24',
          month: 'DEC',
          content: 'One more text here'
        }
      ]
    }
  }
}
</script>

<style lang="less">
.timeline-card{
  .timeline-item-content{
    width: 100%;
  }
}
.timeline-list{
  .timeline-item-content{
    width: 100%;
  }
}
</style>
