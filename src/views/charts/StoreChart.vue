s<template>
    <section class="chart-container">
        <el-row>
            <el-col>
                <div id="chartColumn" style="width:100%; height:1000px;"></div>
            </el-col>
        </el-row>
    </section>
</template>

<script>
    import echarts from 'echarts'
    import util from '../../common/js/util'
    import { requestApi, Enum, msgUtils } from '../../api/api';
    import { getNewData } from '../../api/tableRowAndColumn';

    export default {
        data() {
            return {
                chartColumn: null,
                storeData: null

            }
        },

        methods: {
            getStoreData() {
                // 获取所有数据，然后绘制
                requestApi.store.findAll(this, 0, 999999).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.storeData = res.data.content;
                        let category = [];
                        let numberData = [];
                        let priceData = [];
                        for(let i = 0; i < this.storeData.length; i++){
                            category.push(this.storeData[i].goodItem.name);
                            numberData.push(this.storeData[i].number);
                            priceData.push(this.storeData[i].totalPrice);
                        }
                        this.storeData.category = category;
                        this.storeData.numberData = numberData;
                        this.storeData.priceData = priceData;
                    }
                }, err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                }).then((res) => {
                    this.drawCharts();
                });

            },
            drawColumnChart() {
                this.chartColumn = echarts.init(document.getElementById('chartColumn'));

                var option = {
                    tooltip : {
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    legend: {
                        data: ['库存','当前库存总价']
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis:  {
                        type: 'value'
                    },
                    yAxis: {
                        type: 'category',
                        data: this.storeData.category
                    },
                    series: [
                        {
                            name: '库存',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: this.storeData.numberData
                        },
                        {
                            name: '当前库存总价',
                            type: 'bar',
                            stack: '总量',
                            label: {
                                normal: {
                                    show: true,
                                    position: 'insideRight'
                                }
                            },
                            data: this.storeData.priceData
                        }
                    ]
                };

                this.chartColumn.setOption(option);
            },

            drawCharts() {
                this.drawColumnChart()
            },
        },

        mounted: function () {
            this.getStoreData();
        },
        updated: function () {
            this.getStoreData();
        }
    }
</script>

<style scoped>
    .chart-container {
        width: 100%;
        float: left;
    }
    /*.chart div {
        height: 400px;
        float: left;
    }*/

    .el-col {
        padding: 30px 20px;
    }
</style>
