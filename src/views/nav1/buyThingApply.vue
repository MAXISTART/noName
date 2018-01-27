<template>
    <el-form status-icon ref="addNewThingForm" :model="form" :rules="rules" label-width="80px" style="margin:20px;width:60%;min-width:600px;">
        <el-form-item label="入库时间" prop="enterDate">
            <el-date-picker v-model="form.enterDate" type="date" placeholder="请选择入库日期">
            </el-date-picker>
        </el-form-item>
        <el-form-item label="负责人" prop="warehouseManagerName">
            <el-select v-model="warehouseManagerDefault" placeholder="请选择负责人">
                <el-option v-for="item in warehouseManagers" :label="item.warehouseManagerName" :key="item.id" :value="item.id"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="thingName">
            <el-input v-model="form.thingName" placeholder="请输入物品名称" required></el-input>
        </el-form-item>
        <el-form-item label="种类" prop="type">
            <el-input v-model="form.type" placeholder="请输入种类"></el-input>
        </el-form-item>
        <el-form-item label="规格" prop="size">
            <el-input v-model="form.size" placeholder="请输入规格"></el-input>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
            <el-input v-model="form.unit" placeholder="请输入单位"></el-input>
        </el-form-item>
        <el-form-item label="数量" required prop="total">
            <el-input v-model="form.total" placeholder="请输入数量" @blur="computeTotalPrice"></el-input>
        </el-form-item>
        <el-form-item label="单价" required prop="unitPrice">
            <el-input v-model="form.unitPrice" placeholder="请输入单价" @blur="computeTotalPrice">
                <template slot="append">元</template>
            </el-input>
        </el-form-item>
        <el-form-item label="总价" prop="totalPrice">
            <el-input v-model="form.totalPrice" readonly>
                <template slot="append">元</template>
            </el-input>
        </el-form-item>
        <el-form-item label="来源" prop="resource">
            <el-input v-model="form.resource" placeholder="请输入来源"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click.prevent="onSubmit('addNewThingForm')">提交</el-button>
            <el-button @click="resetForm('addNewThingForm')">重置</el-button>
        </el-form-item>
    </el-form>
</template>

<script>
    import { getWarehouseManager, getWarehouseThings } from '../../api/api';
    import util from '../../common/js/util.js';
    const ERR_OK = 200;
    export default {
        data() {
            // 验证物品名称是否重复
            var checkThingName = (rule, value, callback) => {
                let things = [];
                let hasThing = false;
                getWarehouseThings().then(res => {
                    things = res.data.things;
                    for(var i in things) {
                        if(things[i] === value) {
                            hasThing = true;
                            break;
                        }
                    }

                    if(hasThing === true) {
                        callback(new Error('该物品已存在！'));
                    }else {
                        callback();
                    }
                });
            };

            // 验证数量
            var checkTotal = (rule, value, callback) => {
                if(!value) {
                    return callback(new Error('请输入数量！'));
                }
                let  rex = /^[1-9]\d*$/;
                setTimeout(() => {
                    if(!rex.test(value)) {
                        callback("	数量格式不正确！");
                    }else {
                        callback();
                    }
                }, 1000);
            };

            // 验证单价
            var checkUnitPrice = (rule, value, callback) => {
                if(!value) {
                    return callback(new Error('请输入单价！'));
                }
                let  rex = /^[0-9]+(.[0-9]{1,2})?$/;
                setTimeout(() => {
                    if(!rex.test(value)) {
                        callback("单价格式不正确！");
                    }else {
                        callback();
                    }
                }, 1000);
            };

            return {
                state2: '',
                /* 仓库负责人名单 */
                warehouseManagers: [],
                warehouseManagerDefault: '',
                form: {
                    enterDate: '',
                    personInCharge: '',
                    thingName: '',
                    type: '',
                    unit: '',
                    total: '',
                    totalPrice: '',
                    unitPrice: '',
                    resource: ''
                },

                rules: {
                    enterDate: [
                        { type:  'date', required: true, message: "请选择入库日期！", trigger: 'change'}
                    ],
                    warehouseManagerName: [
                        {required: true, message: '请选择负责人', trigger: 'blur'}
                    ],
                    thingName: [
                        {validator: checkThingName, trigger: 'blur'}
                    ],
                    type: [
                        {required: true, message: '请输入类型', trigger: 'blur'}
                    ],
                    size: [
                        {required: true, message: '请输入规格', trigger: 'blur'}
                    ],
                    unit: [
                        {required: true, message: '请输入单位', trigger: 'blur'},
                        { max: 1, message: '单位格式只能为1个字', trigger: 'blur' }
                    ],
                    total: [
                        {validator: checkTotal, trigger: 'blur'}
                    ],
                    unitPrice: [
                        {validator: checkUnitPrice, trigger: 'blur'}
                    ],
                    resource: [
                        {required: true, message: '请填写来源信息', trigger: 'blur'}
                    ]
                }
            };
        },
        methods: {
            onSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if(valid) {

                        alert('提交成功！');
                    }else {
                        return false;
                    }
                });
            },

            resetForm(formName) {
                this.$refs[formName].resetFields();
            },

            computeTotalPrice() {
                let rexTotal = /^[0-9]*$/;
                let rexUnitPrice = /^[0-9]+(.[0-9]{1,2})?$/;
                if(this.form.total === '' || this.form.unitPrice === '') {
                    this.form.totalPrice = '';
                    return;
                }
                if(!rexTotal.test(this.form.total)){
                    this.form.totalPrice = '';
                    return;
                }

                if(!rexUnitPrice.test(this.form.unitPrice)) {
                    this.form.totalPrice = '';
                    return;
                }

                let totalPrice = Math.floor(this.form.unitPrice * this.form.total * 100) / 100;
                if(!isNaN (totalPrice)) {
                    this.form.totalPrice = totalPrice;
                }else {
                    this.form.totalPrice = '';
                }
            }
        },

        created() {
            getWarehouseManager().then(res => {
                if(res.data.code === ERR_OK) {
                    this.warehouseManagerDefault = res.data.warehouseManager[0].id;
                    this.warehouseManagers = res.data.warehouseManager;
                }
            });
        }
    }

</script>