<template>
    <div>
        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
            <el-button type="primary" size="small" :disabled="this.sels.length === 0" @click="quickAdd">快速入库</el-button>
        </el-col>
        <el-table :data="buyOrdersList" ref="buyOrderTable" v-loading="listLoading" :span-method="arraySpanMethod"
                  @selection-change="selsChange" style="width: 100%;">
            <el-table-column type="selection"></el-table-column>
            <el-table-column label="创建人" prop="createAdmin"></el-table-column>
            <el-table-column label="创建时间" prop="createTime"></el-table-column>
            <el-table-column label="入库明细">
                <el-table-column label="名称" prop="buyOrderItems_name"></el-table-column>
                <el-table-column label="种类" prop="buyOrderItems_sort"></el-table-column>
                <el-table-column label="规格" prop="buyOrderItems_spec"></el-table-column>
                <el-table-column label="单位" prop="buyOrderItems_unit"></el-table-column>
                <el-table-column label="数量" prop="buyOrderItems_number"></el-table-column>
                <el-table-column label="实际单价" prop="buyOrderItems_price"></el-table-column>
            </el-table-column>
            <el-table-column label="审核进度" prop="approvalResult"
                             :filters="[{text: '已通过', value: '0'}, {text: '审核中', value: 1}, {text: '未通过', value: 2}]"
                             :filter-method="filterTag">
                <template slot-scope="scope">
                    <el-popover trigger="hover" placement="top" :disabled="showPopover(scope.row.approvalResult)">
                        <el-button type="primary" round size="small" @click="appoval(scope.row.id, 0)">批准通过</el-button>
                        <el-button type="danger" round size="small" @click="appoval(scope.row.id, 1)">不通过</el-button>
                        <div slot="reference">
                            <el-tag :type="tagChange(scope)">{{scope.row.tag}}</el-tag>
                        </div>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="入库总价" prop="requestTotalPrice"></el-table-column>
            <el-table-column label="申请时间" prop="requestTime"></el-table-column>
            <el-table-column label="发票上传" prop="bills"></el-table-column>
            <el-table-column label="审核时间" prop="approvalTime"></el-table-column>
            <el-table-column label="提交人" prop="updateAdmin"></el-table-column>
            <el-table-column label="更新时间" prop="updateTime"></el-table-column>
            <el-table-column label="描述" prop="description"></el-table-column>
            <el-table-column label="操作" width="170">
                <template slot-scope="scope">
                    <el-button size="small" @click="handleEdit(scope.$index, scope.row)" :disabled="checkInApproval(scope.row.approvalResult)">编辑</el-button>
                    <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 工具条 -->
        <el-col :span="24" class="toolbar">
            <el-button type="danger" :disabled="this.sels.length === 0" @click="batchRemove">批量删除</el-button>
            <el-popover
                    trigger="click"
                    ref="batchApprovalPopover"
                    placement="top-start">
                    <el-button size="mini" type="primary" @click="batchApproval(0)">批准通过</el-button>
                    <el-button type="danger" size="mini" style="float: right;" @click="batchApproval(1)">未通过</el-button>
            </el-popover>
            <el-button type="primary" :disabled="this.sels.length === 0" v-popover:batchApprovalPopover>批量审核</el-button>
            <el-pagination class="pagenation"
                           @size-change="handleSizeChange"
                           @current-change="handleCurrentChange"
                           :current-page="pagenation.currentPage"
                           :page-sizes="pagenation.pageSizes"
                           :page-size="pagenation.pageSize"
                           layout="total, sizes, prev, pager, next, jumper"
                           :total="pagenation.totalElements">
            </el-pagination>
        </el-col>

        <!--编辑界面-->
        <el-dialog fullscreen title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false">
            <el-form :model="editObj" label-width="80px" :rules="editFormRules" ref="editForm">
                <el-form-item label="所属部门" prop="departmentId">
                    <el-select v-model="editObj.departmentId" placeholder="请选择" @change="selectEditDepartment">
                        <el-option
                                v-for="item in allDepartments"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="负责人" prop="requestorId">
                    <el-select v-model="editObj.requestorId" placeholder="请选择">
                        <el-option
                                v-for="item in editRequestors"
                                :key="item.index"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="创建人" prop="createAdmin">
                    <el-input v-model="editObj.createAdmin" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="创建时间" prop="createTime">
                    <el-date-picker
                            :editable="false"
                            v-model="editObj.createTime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            type="datetime"
                            align="right"
                            :picker-options="timeOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="更新人" prop="updateAdmin">
                    <el-input v-model="editObj.updateAdmin" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="更新时间" prop="updateTime">
                    <el-date-picker
                            :editable="false"
                            v-model="editObj.updateTime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            type="datetime"
                            align="right"
                            :picker-options="timeOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="申请时间" prop="requestTime">
                    <el-date-picker
                            :editable="false"
                            v-model="editObj.requestTime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            type="datetime"
                            align="right"
                            :picker-options="timeOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="物品明细" prop="buyOrderItems">
                    <el-table border :data="editObj.buyOrderItems" max-height="200px">
                        <el-table-column label="物品名称" prop="name">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.name"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="种类" prop="sort">
                            <template slot-scope="scope">
                                <el-select v-model="scope.row.sort" placeholder="请选择">
                                    <el-option
                                            v-for="item in sortGroup"
                                            :key="item.value"
                                            :label="item.label"
                                            :value="item.value">
                                    </el-option>
                                </el-select>
                            </template>
                        </el-table-column>
                        <el-table-column label="规格" prop="spec">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.spec"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单价" prop="price">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.price"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="数量" prop="number">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.number"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单位" prop="unit">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.unit"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="50" style="text-align: center;">
                            <template slot-scope="scope">
                               <el-button type="text" size="small" @click="handleEditDelGoodItems(scope.$index, editObj.buyOrderItems)">
                                   <i class="el-icon-close"></i>
                               </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div style="color: red;">
                        {{err_msg}}
                    </div>
                    <el-form-item style="margin-top: 10px;">
                        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleEditAddGoodItems"></el-button>
                    </el-form-item>
                </el-form-item>
                <el-form-item label="入库总价" prop="requestTotalPrice">
                    <el-input v-model="computeEditTotalPrice" readonly auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="editObj.description" type="textarea" :rows="2" autosize></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" :loading="editLoading" @click="handleEditFormSubmit('editForm')">提交</el-button>
            </div>
        </el-dialog>

        <!--新增界面-->
        <el-dialog fullscreen title="新增" :visible.sync="addFormVisible" :close-on-click-modal="false">
            <el-form :model="addObj" label-width="80px" :rules="addFormRules" ref="addForm">
                <el-form-item label="所属部门" prop="departmentId">
                    <el-select v-model="addObj.departmentId" placeholder="请选择" @change="selectAddDepartment">
                        <el-option
                                v-for="item in allDepartments"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="负责人" prop="requestorId">
                    <el-select v-model="addObj.requestorId" placeholder="请选择">
                        <el-option
                                v-for="item in addRequestors"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--<el-form-item label="创建人" prop="createAdmin">-->
                    <!--<el-input v-model="addObj.createAdmin" auto-complete="off"></el-input>-->
                <!--</el-form-item>-->
                <!--<el-form-item label="创建时间" prop="createTime">-->
                    <!--<el-date-picker-->
                            <!--:editable="false"-->
                            <!--v-model="addObj.createTime"-->
                            <!--value-format="yyyy-MM-dd HH:mm:ss"-->
                            <!--type="datetime"-->
                            <!--align="right"-->
                            <!--:picker-options="timeOptions">-->
                    <!--</el-date-picker>-->
                <!--</el-form-item>-->
                <!--<el-form-item label="更新人" prop="updateAdmin">-->
                    <!--<el-input v-model="addObj.updateAdmin" auto-complete="off"></el-input>-->
                <!--</el-form-item>-->
                <!--<el-form-item label="更新时间" prop="updateTime">-->
                    <!--<el-date-picker-->
                            <!--:editable="false"-->
                            <!--v-model="addObj.updateTime"-->
                            <!--value-format="yyyy-MM-dd HH:mm:ss"-->
                            <!--type="datetime"-->
                            <!--align="right"-->
                            <!--:picker-options="timeOptions">-->
                    <!--</el-date-picker>-->
                <!--</el-form-item>-->
                <el-form-item label="申请时间" prop="requestTime">
                    <el-date-picker
                            :editable="false"
                            v-model="addObj.requestTime"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            type="datetime"
                            align="right"
                            :picker-options="timeOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="物品明细" prop="buyOrderItems">
                    <el-table border :data="addObj.buyOrderItems" max-height="200px">
                        <el-table-column label="物品名称" prop="name">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.name"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="种类" prop="sort">
                            <template slot-scope="scope">
                                <el-select v-model="scope.row.sort" placeholder="请选择">
                                    <el-option
                                            v-for="item in sortGroup"
                                            :key="item.value"
                                            :label="item.label"
                                            :value="item.value">
                                    </el-option>
                                </el-select>
                            </template>
                        </el-table-column>
                        <el-table-column label="规格" prop="spec">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.spec"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单价" prop="price">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.price"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="数量" prop="number">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.number"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单位" prop="unit">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.unit"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="50" style="text-align: center;">
                                <template slot-scope="scope">
                                    <el-button type="text" size="small" @click="handleAddFormDelGoodItems(scope.$index, addObj.buyOrderItems)">
                                    <i class="el-icon-close"></i>
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div style="color: red;">
                        {{err_msg}}
                    </div>
                    <el-form-item style="margin-top: 10px;">
                        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAddFormAddGoodItems(addObj.buyOrderItems)"></el-button>
                    </el-form-item>
                </el-form-item>
                <el-form-item label="入库总价" prop="requestTotalPrice">
                    <el-input v-model="computeAddTotalPrice" readonly auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input type="textarea" :rows="2" v-model="addObj.description" autosize></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" :loading="addLoading" @click="handleAddFormSubmit('addForm')">提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import { getBuyOrderList, getWarehouseThings } from '../../api/api';
    import util from '../../common/js/util.js';
    import { getNewDataAndMap } from '@/api/tableRowAndColumn';
    import { ERR_OK0, ERR_OK1, ERR_OK2 } from '@/common/config/config';

    export default {
        data() {
            // 验证物品名称是否重复
            var checkThingName = (rule, value, callback) => {
                let params = {page: 0};
                let things = [];
                let hasThing = false;
                getWarehouseThings(params).then(res => {
                    console.log(res.data);
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

            // 编辑物品的验证
            var checkBuyOrderItems = (rule, value, callback) => {
                var buyOrderItems = this.editObj.buyOrderItems;
                let  rexPrice = /^[0-9]+(.[0-9]{1,2})?$/;
                let  rexNum = /^[1-9]\d*$/;
                let bool = true;
                buyOrderItems.forEach(item => {
                    for(var i in item) {
                        // 验证单价是否保留两位以内小数
                        if(i === 'price') {
                            if(!rexPrice.test(item[i])) {
                                this.err_msg ="单价格式不正确，至多保留两位以内小数！";
                                bool = false;
                            }
                        }
                        // 验证数量是否为整数
                        if(i === 'number') {
                            if(!rexNum.test(item[i])) {
                                this.err_msg ='请在数量一列中输入整数！';
                                bool = false;
                            }
                        }

                        if(item[i] === '') {
                            this.err_msg = '物品明细数据不能为空！';
                            bool = false;
                        }
                    }
                });
                if(bool) {
                    this.err_msg = '';
                }
                callback();
            };

            // 新增物品的验证
            var checkAddBuyOrderItems = (rule, value, callback) => {
                var buyOrderItems = this.addObj.buyOrderItems;
                let  rexPrice = /^[0-9]+(.[0-9]{1,2})?$/;
                let  rexNum = /^[1-9]\d*$/;
                let bool = true;
                buyOrderItems.forEach(item => {
                    for(var i in item) {
                        // 验证单价是否保留两位以内小数
                        if(i === 'price') {
                            if(!rexPrice.test(item[i])) {
                                this.err_msg ="单价格式不正确，至多保留两位以内小数！";
                                bool = false;
                            }
                        }
                        // 验证数量是否为整数
                        if(i === 'number') {
                            if(!rexNum.test(item[i])) {
                                this.err_msg ='请在数量一列中输入整数！';
                                bool = false;
                            }
                        }

                        if(item[i] === '') {
                            this.err_msg = '物品明细数据不能为空！';
                            bool = false;
                        }
                    }
                });
                if(bool) {
                    this.err_msg = '';
                }
                callback();
            };

            return {
                state2: '',
                visible2: false,
                // 页面整体加载状态
                listLoading: false,
                // 所有部门名称与id
                allDepartments: [],
                // 所有user的id和名称
                allUsers: [],
                /* 仓库负责人名单 */
                warehouseManagers: [],
                // 快速入库时点击快速入库的页面加载状态
                enterWarehouseLoading: false,
                // 采购列表显示
                buyOrdersList: [],
                orderItemsNum: [],
                rowIndex1: 0,
                // 单个审核时提交审核的页面加载状态
                approvalLoading: false,
                // 批量删除
                sels: [],
                // 点击批量审核按钮时的页面加载状态
                batchApprovalLoading: false,
                // 分页
                pagenation: {
                    totalElements: 0,
                    currentPage: 0,
                    pageSizes: [10, 20, 50, 100],
                    pageSize: 20
                },

                // 编辑界面相关函数
                editObj: {},
                // 所有部门的采购负责人departmentid与id与name
                editRequestors: [],
                editFormVisible: false,//编辑界面是否显示
                editLoading: false,  // 编辑框提交按钮加载中状态
                timeOptions: {
                    shortcuts: [{
                        text: '今天',
                        onClick(picker) {
                            picker.$emit('pick', new Date());
                        }
                    }, {
                        text: '昨天',
                        onClick(picker) {
                            const date = new Date();
                            date.setTime(date.getTime() - 3600 * 1000 * 24);
                            picker.$emit('pick', date);
                        }
                    }, {
                        text: '一周前',
                        onClick(picker) {
                            const date = new Date();
                            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', date);
                        }
                    }]
                },
                sortGroup: [], // 种类选择框
                nameBool: false,
                err_msg: '',
                editFormRules: {
                    departmentId: [
                        {required: true, message: '请选择部门名称', trigger: 'change'}
                    ],
                    requestorId: [
                        {required: true, message: '请选择部门名称', trigger: 'change'}
                    ],
                    createAdmin: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ],
                    createTime: [
                        {required: true, message: '请输入创建时间', trigger: 'blur'}
                    ],
                    updateAdmin: [
                        {required: true, message: '请输入更新人', trigger: 'blur'}
                    ],
                    updateTime: [
                        {required: true, message: '请输入更新时间', trigger: 'blur'}
                    ],
                    requestTime: [
                        {required: true, message: '请输入申请时间', trigger: 'blur'}
                    ],
                    buyOrderItems: [
                        {required: true, message: '物品明细不能为空', trigger: 'blur'},
                        { validator: checkBuyOrderItems, trigger: 'blur' },
                    ],
                    price: [
                        { validator: checkUnitPrice, trigger: 'blur' },
                        {required: true, message: '请输入数量', trigger: 'blur'}
                    ],
                    sort: [
                        {required: true, message: '请输入物品种类', trigger: 'change' }
                    ],
                    spec: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ],
                    unit: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ]
                },

                // 新增界面
                addFormVisible: false,
                addLoading: false,
                addObj: {
                    departmentId: '',
                    requestTime: '',
                    requestorId: '',
                    description: '',
                    buyOrderItems: []
                },
                // 所有部门的采购负责人departmentid与id与name
                addRequestors: [],
                addFormRules: {
                    departmentId: [
                        {required: true, message: '请选择部门名称', trigger: 'change'}
                    ],
                    requestorId: [
                        {required: true, message: '请选择部门名称', trigger: 'change'}
                    ],
                    createAdmin: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ],
                    createTime: [
                        {required: true, message: '请输入创建时间', trigger: 'blur'}
                    ],
                    updateAdmin: [
                        {required: true, message: '请输入更新人', trigger: 'blur'}
                    ],
                    updateTime: [
                        {required: true, message: '请输入更新时间', trigger: 'blur'}
                    ],
                    requestTime: [
                        {required: true, message: '请输入申请时间', trigger: 'blur'}
                    ],
                    buyOrderItems: [
                        {required: true, message: '物品明细不能为空', trigger: 'blur'},
                        { validator: checkAddBuyOrderItems, trigger: 'blur' },
                    ]
                }
            };
        },

        computed: {
            // 计算编辑框的总价
            computeEditTotalPrice: {
                set: function(value) {
                    this.editObj.totalPrice = value;
                },
                get: function() {
                    let buyOrderItems = this.editObj.buyOrderItems;
                    let totalPrice = 0;
                    if(buyOrderItems) {
                        buyOrderItems.forEach(item => {
                            let tempPrice = item.price * 100 * item.number / 100;
                            totalPrice += tempPrice;
                        });
                    }
                    this.editObj.totalPrice = totalPrice;
                    return this.editObj.totalPrice;
                }
            },

            // 计算新增框的总价
            computeAddTotalPrice: {
                set: function(value) {
                    this.addObj.totalPrice = value;
                },
                get: function() {
                    let buyOrderItems = this.addObj.buyOrderItems;
                    let totalPrice = 0;
                    if(buyOrderItems) {
                        buyOrderItems.forEach(item => {
                            let tempPrice = item.price * 100 * item.number / 100;
                            totalPrice += tempPrice;
                        });
                    }
                    if(Number.isNaN(totalPrice)) {
                        this.addObj.totalPrice = 0;
                    }else {
                        this.addObj.totalPrice = totalPrice;
                    }
                    return this.addObj.totalPrice;
                }
            },
        },

        methods: {
            // onSubmit(formName) {
            //     this.$refs[formName].validate((valid) => {
            //         if(valid) {
            //
            //             alert('提交成功！');
            //         }else {
            //             return false;
            //         }
            //     });
            // },
            //
            // resetForm(formName) {
            //     this.$refs[formName].resetFields();
            // },
            // 获取采购记录数据
            getBuyOrderList(page, size) {
                this.$http.get('/api/admin/buyOrder/findAllBuyOrders?page='+ page + '&size=' + size).then(res => {
                    let code = res.code;
                    if(ERR_OK0 === code) {
                        let buyOrdersList = res.data.data.content;
                        let numberOfElements = res.data.data.numberOfElements;
                        this.pagenation.totalElements = numberOfElements;
                        this.buyOrdersList = getNewDataAndMap("buyOrderItems", buyOrdersList).data;
                        this.orderItemsNum = getNewDataAndMap("buyOrderItems", buyOrdersList).map;
                    }else {
                        let msg = res.msg;
                        alert(msg);
                    }
                }, err => {
                    alert('系统错误，请重新刷新页面 ' + err);
                });
            },

            arraySpanMethod({ row, column, rowIndex, columnIndex }) {
                let orderItemsNum = this.orderItemsNum;
                if (columnIndex === 0 || columnIndex === 1 || columnIndex === 2 || columnIndex === 9 ||
                    columnIndex === 10 || columnIndex === 11 || columnIndex === 12 || columnIndex === 13 ||
                    columnIndex === 14 || columnIndex === 15 || columnIndex === 16 || columnIndex === 17) {
                    if(orderItemsNum[rowIndex] != null){
                        return {
                            rowspan: orderItemsNum[rowIndex],
                            colspan: 1
                        };
                    }else{
                        return {
                            rowspan: 0,
                            colspan: 0
                        };
                    }
                }
                // orderItemsNum.forEach(item => {
                //     if(row.createTime === item.createTime) {
                //         for(let i = 0; i < 17;i++) {
                //             if(columnIndex === 0 || columnIndex === 1 || columnIndex === 2 || columnIndex === 3 ||
                //                 columnIndex === 11 || columnIndex === 12 || columnIndex === 13 ||
                //                 columnIndex === 14 || columnIndex === 15 || columnIndex === 16 || columnIndex === 17) {
                //                   return {
                //                       row: item.num,
                //                       column: 1
                //                   };
                //             }
                //         }
                //     }
                // });
                //
                // for(let j = 0;j < orderItemsNum.length;j++) {
                //     for(let i = 0; i < 17;i++) {
                //         if(columnIndex === 0 || columnIndex === 1 || columnIndex === 2 || columnIndex === 3 ||
                //              columnIndex === 11 || columnIndex === 12 || columnIndex === 13 ||
                //             columnIndex === 14 || columnIndex === 15 || columnIndex === 16 || columnIndex === 17) {
                //                 if(rowIndex === this.rowIndex1) {
                //                     return {
                //                         rowspan: orderItemsNum[j],
                //                         colspan: 1
                //                     };
                //                 }else {
                //                     return {
                //                         rowspan: 0,
                //                         colspan: 0
                //                     };
                //                 }
                //         }
                //     }
                //     this.rowIndex1 += orderItemsNum[j];
                // }
                // for(let i = 0;i < 16;i++) {
                //     if(columnIndex === 0 || columnIndex === 1 || columnIndex === 2 || columnIndex === 3 ||
                //         columnIndex === 10 || columnIndex === 11 || columnIndex === 12 || columnIndex === 13 ||
                //         columnIndex === 14 || columnIndex === 15 || columnIndex === 16) {
                //         let t = 0;
                //         // if(rowIndex % 11 === 0) {
                //         //     return {
                //         //         rowspan: 11,
                //         //         colspan: 1
                //         //     };
                //         // }else {
                //         //     return {
                //         //         rowspan: 0,
                //         //         colspan: 0
                //         //     };
                //         // }
                //         orderItemsNum.forEach(item => {
                //             if(rowIndex % item === 0) {
                //                 return {
                //                     rowspan: item,
                //                     colspan: 1
                //                 };
                //             }else {
                //                 return {
                //                     rowspan: 0,
                //                     colspan: 0
                //                 };
                //             }
                //         });
                //         // if(rowIndex % 11 === 0) {
                //         //     return {
                //         //         rowspan: 11,
                //         //         colspan: 1
                //         //     };
                //         // }else {
                //         //     return {
                //         //         rowspan: 0,
                //         //         colspan: 0
                //         //     };
                //         // }
                //         // if (rowIndex % 2 === 0) {
                //         //     return {
                //         //         rowspan: 2,
                //         //         colspan: 1
                //         //     };
                //         // }else {
                //         //     return {
                //         //         rowspan: 0,
                //         //         colspan: 0
                //         //     };
                //         // }
                //     }
                // }
            },

            // 新增数据
            handleAdd() {
                this.addFormVisible = true;
                // 清空addObj
                util.emptyObj(this.addObj);
            },

            // 新增框选择部门下拉框触发
            selectAddDepartment(value) {
                let para = {
                    departmentId: value
                };
                this.addRequestors = [];
                this.$http.post('/api/admin/user/findUsersByDepartmentId',para, { emulateJSON: true}).then(res => {
                    let addRequestors = res.data;
                    addRequestors.forEach(addRequestor => {
                        let obj = {
                            id: addRequestor.id,
                            name: addRequestor.name
                        };
                        this.addRequestors.push(obj);
                    });
                }, err => {
                    alert('系统错误，请重新刷新页面   ' + err);
                });
            },

            // 新增框点击物品明细的移除按钮移除物品明细
            handleAddFormDelGoodItems(index) {
                this.addObj.buyOrderItems.splice(index, 1);
                this.checkBuyOrderItems(this.addObj.buyOrderItems);
            },

            // 点击添加框的添加物品明细按钮
            handleAddFormAddGoodItems(buyOrderItems) {
                let obj = {
                    name: '',
                    sort: '',
                    spec: '',
                    unit: '',
                    number: '',
                    price: ''
                };
                buyOrderItems.push(obj);
            },

            // 提交新增框的数据
            handleAddFormSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        if(this.err_msg === '') {
                            this.$confirm('确认提交吗？', '提示', {}).then(() => {
                                this.addLoading = true;
                                let para = this.addObj;
                                console.log(para);
                                this.$http.post('/api/admin/buyOrder/addBuyOrder', para).then(res => {
                                    let code = res.code;
                                    if(code === ERR_OK0) {
                                        alert("采购单添加成功！");
                                        this.addFormVisible = false;
                                        this.addLoading = false;
                                        let page = this.pagenation.currentPage;
                                        let size = this.pagenation.pageSize;
                                        this.getBuyOrderList(page, size);
                                    }else {
                                        alert(res.msg);
                                    }
                                }, err => {
                                    alert('系统错误，请重新刷新页面！');
                                });
                            });
                        }else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                });
            },

            // 快速入库
            quickAdd() {
                let para = [];
                let bool = true;
                this.sels.forEach(item => {
                    if(item.approvalResult !== 0) {
                        bool = false;
                    }else {
                        para.push(item.id);
                    }
                });
                if(!bool) {
                    alert('有部分或全部采购单未审核通过，请选择审核通过的采购单！');
                    return false;
                }
                this.$confirm('确认入库吗？', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    this.$http.post('/api/admin/buyOrder/quickInput', para).then(res => {
                        let code = res.data.code;
                        if(code === 0) {
                            alert('入库成功！');
                            this.listLoading = false;
                            let page = this.pagenation.currentPage;
                            let size = this.pagenation.pageSize;
                            this.getBuyOrderList(page, size);
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面   ' + err);
                    });
                });
            },

            // 判断是否显示popover
            showPopover(approvalResult) {
                let bool = false;
                if(approvalResult === 2) {
                    bool = false;
                }else {
                    bool = true;
                }
                return bool;
            },

            // 点击批准通过按钮触发
            appoval(id, approvalResult) {
                let obj = {
                    id: id,
                    approvalResult: approvalResult
                };
                let para = [];
                para.push(obj);
                // console.log(obj);
                this.$confirm('确认提交吗？', '提示', {}).then(() => {
                    this.listLoading = true;
                    this.$http.post('/api/admin/buyOrder/approve', para).then(res => {
                        let code = res.data.code;
                        if (code === 0) {
                            alert('审批成功！');
                            this.listLoading = false;
                            let page = this.pagenation.currentPage;
                            let size = this.pagenation.pageSize;
                            this.getBuyOrderList(page, size);
                        } else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面！   ' + err);
                    });
                });
            },

            // 判断是否禁用编辑按钮
            checkInApproval(approvalResult) {
                let bool = false;
                if(approvalResult === 2) {
                    bool = false;
                }else {
                    bool = true;
                }
                return bool;
            },

            // 分页点击多少条一页查询
            handleSizeChange(val) {
                this.pagenation.pageSize = val;
                let page = this.pagenation.currentPage;
                let size = this.pagenation.pageSize;
                this.listLoading = true;
                this.$http.get('/api/admin/buyOrder/findAllBuyOrders?page=' + page + '&size=' + size).then(res => {
                    this.listLoading = false;
                    let code = res.code;
                    if(code === ERR_OK0) {
                        let buyOrdersList = res.data.data.content;
                        this.buyOrdersList = getNewDataAndMap("buyOrderItems", buyOrdersList).data;
                        this.orderItemsNum = getNewDataAndMap("buyOrderItems", buyOrdersList).map;
                    }else {
                        let msg = res.msg;
                        alert(msg);
                    }
                }, err => {
                    alert('系统错误，请重新刷新页面！  '  + err);
                });
            },

            // 分页点击当前页查询
            handleCurrentChange(val) {
                let page = val - 1;
                this.pagenation.currentPage = page;
                let size = this.pagenation.pageSize;
                this.listLoading = true;
                this.$http.get('/api/admin/buyOrder/findAllBuyOrders?page=' + page + '&size=' + size).then(res => {
                    this.listLoading = false;
                    let code = res.code;
                    if(ERR_OK0 === code) {
                        let buyOrdersList = res.data.data.content;
                        this.buyOrdersList = getNewDataAndMap("buyOrderItems", buyOrdersList).data;
                        this.orderItemsNum = getNewDataAndMap("buyOrderItems", buyOrdersList).map;
                    }else {
                        let msg = res.msg;
                        alert(msg);
                    }
                }, err => {
                    alert('系统错误，请重新刷新页面！  ' + err);
                });
            },

            // 批量删除相关函数
            selsChange: function (sels) {
                this.sels = sels;
            },

            // 批量审核
            batchApproval(approvalResult){
                let approvalArr = [];
                let bool = true;
                this.sels.forEach(item => {
                    if(item.approvalResult === 2) {
                        let obj = {
                            id: item.id,
                            approvalResult: approvalResult
                        };
                        approvalArr.push(obj);
                    }else {
                        bool = false;
                    }
                });
                if(bool === false) {
                    alert('存在已审核的采购记录！');
                    return false;
                }
                let model_msg = '';
                if(approvalResult === 0) {
                    model_msg = '确认审核通过？';
                }else {
                    model_msg = '确认审核不通过？';
                }
                this.$confirm(model_msg, '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    this.$http.post('/api/admin/buyOrder/approve', approvalArr).then(res => {
                        let code = res.code;
                        if(code === ERR_OK0) {
                            alert('批量审核成功！');
                            this.listLoading = false;
                            let page = this.pagenation.currentPage;
                            let size = this.pagenation.pageSize;
                            this.getBuyOrderList(page, size);
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面   ' + err);
                    });
                });
            },

            // 批量删除
            batchRemove() {
                let para = this.sels.map(item => {
                    return {id: item.id};
                });
                this.$confirm('确认删除选中记录吗？', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    this.$http.post('/api/admin/buyOrder/deleteBuyOrders', para).then(res => {
                        // console.log(res);
                        let code = res.code;
                        if(code === ERR_OK0) {
                            alert('批量删除成功！');
                            this.listLoading = false;
                            let page = this.pagenation.currentPage;
                            let size = this.pagenation.pageSize;
                            this.getBuyOrderList(page, size);
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面   ' + err);
                    });
                });
            },

            // 检验添加和编辑框的table的数据是否存在错误
            checkBuyOrderItems(buyOrderItems) {
                var buyOrderItems = buyOrderItems;
                let  rexPrice = /^[0-9]+(.[0-9]{1,2})?$/;
                let  rexNum = /^[1-9]\d*$/;
                let bool = true;
                buyOrderItems.forEach(item => {
                    for(var i in item) {
                        // 验证单价是否保留两位以内小数
                        if(i === 'price') {
                            if(!rexPrice.test(item[i])) {
                                this.err_msg ="单价格式不正确，至多保留两位以内小数！";
                                bool = false;
                            }
                        }
                        // 验证数量是否为整数
                        if(i === 'number') {
                            if(!rexNum.test(item[i])) {
                                this.err_msg ='请在数量一列中输入整数！';
                                bool = false;
                            }
                        }

                        if(item[i] === '') {
                            this.err_msg = '物品明细数据不能为空！';
                            bool = false;
                        }
                    }
                });
                if(bool) {
                    this.err_msg = '';
                }
            },

            // 点击编辑按钮触发
            handleEdit(index,row) {
                this.editFormVisible = true;
                let para = {
                    buyOrderId: row.id
                };
                let departmentObj = {
                    departmentId: row.departmentId
                };
                this.editRequestors = [];
                this.$http.post('/api/admin/user/findUsersByDepartmentId', departmentObj, { emulateJSON: true}).then(res => {
                    let editRequestors = res.data;
                    editRequestors.forEach((requestor, index) => {
                        let obj = {
                            id: requestor.id,
                            name: requestor.name
                        };
                        this.editRequestors.push(obj);
                    });
                }, err => {
                    alert('系统错误，请重新刷新页面！  ' + err);
                });

                this.$http.post('/api/admin/buyOrder/findBuyOrderById' ,para, { emulateJSON: true}).then(res => {
                    let status = res.status;
                    if(status === 200) {
                        this.editObj = res.body;
                    }else {
                        let msg = res.msg;
                        alert(msg);
                    }
                }, err => {
                    alert('系统错误，请重新刷新页面！  ' + err);
                });
                // this.editObj = Object.assign({}, row);
            },

            // 点击编辑框的部门下拉框触发
            selectEditDepartment(value) {
                let para = {
                    departmentId: value
                };
                this.editRequestors = [];
                this.$http.post('/api/admin/user/findUsersByDepartmentId',para, { emulateJSON: true}).then(res => {
                    let editRequestors = res.data;
                    editRequestors.forEach(editRequestor => {
                        let obj = {
                            id: editRequestor.id,
                            name: editRequestor.name
                        };
                        this.editRequestors.push(obj);
                    });
                }, err => {
                    alert('系统错误，请重新刷新页面   ' + err);
                });
            },

            // 点击编辑框的表格删除行
            handleEditDelGoodItems(index) {
                this.editObj.buyOrderItems.splice(index, 1);
                this.checkBuyOrderItems(this.editObj.buyOrderItems);
            },

            // 点击编辑框的添加按钮添加行
            handleEditAddGoodItems() {
                let obj = {
                    name: '',
                    sort: '',
                    spec: '',
                    unit: '',
                    number: '',
                    price: ''
                };
                this.editObj.buyOrderItems.push(obj);
            },

            // 提交编辑后数据
            handleEditFormSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        if(this.err_msg === '') {
                            this.$confirm('确认提交吗？', '提示', {}).then(() => {
                                this.listLoading = false;
                                this.editLoading = true;
                                let para = {
                                    id: this.editObj.id,
                                    departmentId: this.editObj.departmentId,
                                    delFlag: this.editObj.delFlag,
                                    createTime: this.editObj.createTime,
                                    createAdmin: this.editObj.createAdmin,
                                    updateAdmin: this.editObj.updateAdmin,
                                    requestTime: this.editObj.requestTime,
                                    requestTotalPrice: this.editObj.requestTotalPrice,
                                    requestorId: this.editObj.requestorId,
                                    description: this.editObj.description,
                                    approvalTaskId: this.editObj.approvalTaskId,
                                    approvalResult: this.editObj.approvalResult,
                                    approvalTime: this.editObj.approvalTime,
                                    bills: this.editObj.bills,
                                    buyOrderItems: this.editObj.buyOrderItems
                                };
                                this.$http.post('/api/admin/buyOrder/updateBuyOrder', para).then(res => {
                                    this.editLoading = false;
                                    let code = res.code;
                                    if(code === ERR_OK0) {
                                        this.editFormVisible = false;
                                        alert("采购单修改成功！");
                                        this.listLoading = false;
                                        let page = this.pagenation.currentPage;
                                        let size = this.pagenation.pageSize;
                                        this.getBuyOrderList(page, size);
                                    }else {
                                        alert(res.msg);
                                    }
                                }, err => {
                                    alert('系统错误，请重新刷新页面！');
                                });
                            });
                        }else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                });
            },

            // 点击删除按钮触发
            handleDel(index, row) {
                this.$confirm('确认删除该记录吗?', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    let para = { id: row.id };
                    this.$http.post('/api/admin/buyOrder/deleteBuyOrder',para).then(res => {
                        this.listLoading = false;
                        let code = res.code;
                        if(ERR_OK0 === code) {
                            alert('删除成功！');
                            let page = this.pagenation.currentPage;
                            let size = this.pagenation.pageSize;
                            this.getBuyOrderList(page, size);
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面！   ' + err);
                    });
                });
            },

            // 筛选函数
            filterTag(value, row) {
                row.approvalResult === value;
            },

            // 给不同的审核进度加一个class背景
            tagChange(scope) {
                let approvalResult = scope.row.approvalResult;
                switch(approvalResult) {
                    case 0: {scope.row.tag = '已通过';return 'success';break;}
                    case 1: {scope.row.tag = '未通过';return 'danger';break;}
                    case 2: {scope.row.tag = '审核中';return 'primary';break;}
                    default: {scope.row.tag = '审核中';return 'primary';break;}
                }
            }
        },

        created() {
            let sortGroup = '[' + sessionStorage.sortGroup + ']';
            let allDepartments = '[' + sessionStorage.departmentsGroup + ']';
            // let allUsers = '[' + sessionStorage.usersGroup + ']';
            sortGroup = JSON.parse(sortGroup);
            allDepartments = JSON.parse(allDepartments);
            // allUsers = JSON.parse(allUsers);
            allDepartments.forEach(item => {
               let obj = {
                   id: item.id,
                   name: item.name
               };
               this.allDepartments.push(obj);
            });

            // allUsers.forEach(item => {
            //     let obj = {
            //         departmentId: item.departmentId,
            //         id: item.id,
            //         name: item.name
            //     };
            //     this.allUsers.push(obj);
            // });
            this.sortGroup = sortGroup;
            // this.$http.get('/api/admin/config/getInitData').then(res => {
            //     let code = res.data.code;
            //     if(code === 0) {
            //         sortNames = res.data.data.allSorts; // 获得种类参数
            //         let goods = res.data.data.allGoods; // 获得物品所有参数
            //
            //     }else {
            //         let msg = res.msg;
            //         alert(msg);
            //     }
            // }, err => {
            //     alert("系统错误，请重新刷新页面！  " + err);
            // });
            this.$http.get('/api/admin/buyOrder/findAllBuyOrders?page=0&size=20').then(res => {
                let code = res.code;
                if(ERR_OK0 === code) {
                    let buyOrdersList = res.data.data.content;
                    let numberOfElements = res.data.data.numberOfElements;
                    this.pagenation.totalElements = numberOfElements;
                    console.log(buyOrdersList)
                    // buyOrdersList.forEach(item => {
                    //     for(let i = 0;i < this.allDepartments.length;i++) {
                    //         if(item.departmentId === this.allDepartments[i].departmentId) {
                    //             item.departmentName = this.allDepartments[i].name;
                    //         }
                    //     }
                    // });
                    // buyOrdersList.forEach((buyOrdersListItem) => {
                    //     let obj = {};
                    //     let buyOrderItem = buyOrdersListItem.buyOrderItems;
                    //     let orderItemsObj = {};
                    //     orderItemsObj.createTime = buyOrdersListItem.createTime;
                    //     orderItemsObj.num = buyOrderItem.length;
                    //     orderItemsNum.push(orderItemsObj);
                    //     delete buyOrdersListItem.buyOrderItems;
                    //     buyOrderItem.forEach((item) => {
                    //         Object.assign(obj,buyOrdersListItem,item);
                    //         this.buyOrdersList.push(obj);
                    //     });
                    // });
                    this.buyOrdersList = getNewDataAndMap("buyOrderItems", buyOrdersList).data;
                    this.orderItemsNum = getNewDataAndMap("buyOrderItems", buyOrdersList).map;
                    // for(let i = 0;i < buyOrdersList2.length;i++) {
                    //     let createAdmin = buyOrdersList2[i].createAdmin;
                    //     let temp = 0;
                    //     let t = 0;
                    //     let arr = buyOrdersList2.slice(0, buyOrdersList2.length);
                    //     for(let j = 0;j < arr.length;j++) {
                    //         if(createAdmin === arr[j].createAdmin) {
                    //             this.buyOrdersList.push(arr[j]);
                    //             buyOrdersList2.splice(t, 1);
                    //         }else {
                    //             t++;
                    //         }
                    //     }
                    // }
                }else {
                    let msg = res.msg;
                    alert(msg);
                }
            }, err => {
                alert('系统错误，请重新刷新页面 ' + err);
            });
        }
    }

</script>

<style>
    .pagenation {
        float: right;
    }

    .el-table__body-wrapper, .el-table__body-wrapper.is-scroll-right, .el-table__body-wrapper.is-scroll-left {
        overflow-y: hidden;
    }
</style>