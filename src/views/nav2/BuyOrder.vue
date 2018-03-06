<template>
    <div>
        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <el-button type="primary" size="midium" @click="handleFilterAdd">按条件查询</el-button>
            <el-button type="primary" size="midium" @click="handleAdd">新增</el-button>
            <el-button type="primary" size="midium" :disabled="this.sels.length === 0" @click="quickInput">快速入库</el-button>
        </el-col>
        <!--:rules="filterFormRules" -->
<!--        <el-dialog title="添加查询条件" :visible.sync="filterVisible" :close-on-click-modal="false">
            <custom-form name="filter"
                         @on-submit="onFilterSumbit"
                         :items="filterForm"
                         :data="filters"
                         >
            </custom-form>
        </el-dialog>-->

        <el-dialog title="添加查询条件" :visible.sync="filterVisible" :close-on-click-modal="false" @close="onFilterClose">
            <el-form :model="filters" label-width="150px" ref="addForm" :rules="filterRules">
                <el-form-item label="所属部门" prop="departmentId">
                    <el-select v-model="filters.departmentId" placeholder="请选择" @change="selectFilterDepartment">
                        <el-option
                                v-for="item in allDepartments"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="负责人" prop="requestorId">
                    <el-select v-model="filters.requestorId" placeholder="请选择">
                        <el-option
                                v-for="item in filterRequestors"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="申请时间">
                    <el-col :span="11">
                        <el-date-picker
                                :editable="false"
                                v-model="filters.requestTime_start"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                type="datetime"
                                style="width: 100%;"
                                :picker-options="timeOptions">
                        </el-date-picker>
                    </el-col>
                    <el-col class="line" :span="2">——</el-col>
                    <el-col :span="11">
                        <el-date-picker
                                :editable="false"
                                v-model="filters.requestTime_end"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                type="datetime"
                                style="width: 100%;"
                                :picker-options="timeOptions">
                        </el-date-picker>
                    </el-col>
                </el-form-item>


                <el-form-item label="申请总价">
                    <el-col :span="11">
                        <el-input v-model.number="filters.price_start" placeholder="请输入价格范围"></el-input>
                    </el-col>
                    <el-col class="line" :span="2">——</el-col>
                    <el-col :span="11">
                        <el-input v-model.number="filters.price_end" placeholder="请输入价格范围"></el-input>
                    </el-col>
                </el-form-item>

                <el-row>
                    <el-form-item align="right">
                        <el-button type="primary"  @click="refreshData()">刷新数据</el-button>
                        <el-button type="primary"  @click="handleResetFilter()">重置</el-button>
                    </el-form-item>
                </el-row>
            </el-form>
        </el-dialog>

        <el-table :data="buyOrders" ref="buyOrders" v-loading="listLoading" :span-method="arraySpanMethod"
                  @selection-change="selsChange"  @sort-change="sortAll" style="width: 100%;">
            <el-table-column type="selection" width="55">
            </el-table-column>
            <el-table-column type="index" width="60" align="center">
            </el-table-column>
            <el-table-column label="采购单明细" align="center" width="800">
                <el-table-column label="名称" prop="buyOrderItems_name" min-width="120"  align="center"></el-table-column>
                <el-table-column label="种类" prop="buyOrderItems_sort" min-width="120"  align="center"></el-table-column>
                <el-table-column label="规格" prop="buyOrderItems_spec" min-width="120"  align="center"></el-table-column>
                <el-table-column label="单位" prop="buyOrderItems_unit" min-width="120"  align="center"></el-table-column>
                <el-table-column label="数量" prop="buyOrderItems_number" min-width="120"  align="center"></el-table-column>
                <el-table-column label="实际单价" prop="buyOrderItems_price" min-width="120"  align="center"></el-table-column>
            </el-table-column>

            <el-table-column label="采购单信息"  align="center" width="1060">
                <el-table-column label="审核进度" prop="approvalResult"
                                 :filters="[{text: '已通过', value: '1'}, {text: '审核中', value: 2}, {text: '已驳回', value: 0}]"
                                 :filter-method="filterTag"
                                 min-width="200"
                                 align="center">
                    <template slot-scope="scope">
                        <el-popover trigger="hover" placement="top" :disabled="showPopover(scope.row.approvalResult)">
                            <el-button type="primary" round size="small" @click="approve(scope.row.id, 1)">批准通过</el-button>
                            <el-button type="danger" round size="small" @click="approve(scope.row.id, 0)">不通过</el-button>
                            <div slot="reference">
                                <el-tag :type="tagChange(scope)">{{scope.row.tag}}</el-tag>
                            </div>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column label="是否已经入库" prop="hasBeenInput" :formatter="formatInputFlag" min-width="140" sortable="custom" align="center"></el-table-column>
                <el-table-column label="申请部门" prop="departmentName" min-width="140" sortable="custom" align="center"></el-table-column>
                <el-table-column label="申请人" prop="requestorName" min-width="140" sortable="custom" align="center"></el-table-column>
                <el-table-column label="申请时间" prop="requestTime" min-width="200" sortable="custom" align="center"></el-table-column>
                <el-table-column label="申请总价" prop="requestTotalPrice" min-width="140" sortable="custom"  align="center"></el-table-column>
                <el-table-column label="申请原因" prop="description" min-width="200" sortable="custom" align="center"></el-table-column>
                <el-table-column label="发票地址" prop="bills" min-width="140" sortable="custom" align="center"></el-table-column>
            </el-table-column>

            <el-table-column label="系统属性" width="320" align="center">
                <el-table-column prop="creatorName" label="创建者" width="120" sortable="custom" align="center">
                </el-table-column>
                <el-table-column type="date" prop="createTime" label="创建时间" min-width="180" sortable="custom" align="center">
                </el-table-column>
                <el-table-column prop="updatorName" label="最后更新人" min-width="180" sortable="custom" align="center">
                </el-table-column>
                <el-table-column type="date" prop="lastmodifiedTime" label="最后更新时间" min-width="180" sortable="custom" align="center">
                </el-table-column>
            </el-table-column>

            <el-table-column label="操作" width="150" fixed="right">
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
                    <el-button size="mini" type="primary" @click="batchApprove(1)">批准通过</el-button>
                    <el-button type="danger" size="mini" style="float: right;" @click="batchApprove(0)">驳回</el-button>
            </el-popover>
            <el-button type="primary" :disabled="this.sels.length === 0" v-popover:batchApprovalPopover>批量审核</el-button>
            <el-pagination
                    class="pagination"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="pagination.currentPage"
                    :page-sizes="pagination.pageSizes"
                    :page-size="pagination.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="pagination.total">
            </el-pagination>
        </el-col>


        <!--编辑界面-->
        <el-dialog fullscreen title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false" @close="clearValidate('editForm')">
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
                    <el-select v-model="editObj.requestorId" placeholder="请选择" >
                        <el-option
                                v-for="item in editRequestors"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id">
                        </el-option>
                    </el-select>
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

                    <el-table :data="editObj.buyOrderItems"
                              show-summary
                              :summary-method="getSummaries"
                              border
                              height="350px" max-height="999999px"
                              highlight-current-row
                              v-loading="listLoading"
                              @selection-change="selsChange"
                              style="width: 100%;">

                        <el-table-column label="名称" prop="name" align="center">
                            <template slot-scope="scope">
                                <el-autocomplete
                                        v-model="scope.row.name"
                                        :fetch-suggestions="querySearchAsync"
                                        placeholder="请输入名称"
                                        @select="handleSelect(scope.row, $event)"
                                ></el-autocomplete>
                            </template>
                        </el-table-column>
                        <el-table-column label="数量" prop="number" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.number" placeholder="请输入数量"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="规格" prop="spec" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.spec" placeholder="请先输入名称"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单价" prop="price" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.price" placeholder="请先输入名称"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单位" prop="unit" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.unit" placeholder="请先输入名称"></el-input>
                            </template>
                        </el-table-column>

                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="50"  align="center">
                            <template slot-scope="scope">
                                <el-button type="danger" size="small" icon="el-icon-minus" @click="handleRemoveBuyOrderItem(scope.$index, editObj.buyOrderItems)">
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>

                    <el-form-item style="margin-top: 10px;">
                        <el-button type="primary" size="medium" icon="el-icon-plus" @click="handleAddBuyOrderItem(editObj.buyOrderItems)"></el-button>
                    </el-form-item>

                </el-form-item>

                <el-form-item label="申请描述" prop="description">
                    <el-input type="textarea" :rows="4" v-model="editObj.description" autosize></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" :loading="editLoading" @click="handleEditFormSubmit('editForm')">提交</el-button>
            </div>
        </el-dialog>


        <!--新增界面-->
        <el-dialog title="新增" fullscreen :visible.sync="addFormVisible" :close-on-click-modal="false" @close="clearValidate('addForm')">
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

                    <el-table :data="addObj.buyOrderItems"
                              show-summary
                              :summary-method="getSummaries"
                              border
                              height="350px" max-height="999999px"
                              highlight-current-row
                              v-loading="listLoading"
                              @selection-change="selsChange"
                              style="width: 100%;">

                        <el-table-column label="名称" prop="name" align="center">
                            <template slot-scope="scope">
                                <el-autocomplete
                                        v-model="scope.row.name"
                                        :fetch-suggestions="querySearchAsync"
                                        placeholder="请输入名称"
                                        @select="handleSelect(scope.row, $event)"
                                ></el-autocomplete>
                            </template>
                        </el-table-column>
                        <el-table-column label="数量" prop="number" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.number" placeholder="请输入数量"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="规格" prop="spec" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.spec" placeholder="请先输入名称"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单价" prop="price" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.price" placeholder="请先输入名称"></el-input>
                            </template>
                        </el-table-column>
                        <el-table-column label="单位" prop="unit" align="center">
                            <template slot-scope="scope">
                                <el-input v-model="scope.row.unit" placeholder="请先输入名称"></el-input>
                            </template>
                        </el-table-column>

                        <el-table-column
                                fixed="right"
                                label="操作"
                                width="50"  align="center">
                                <template slot-scope="scope">
                                    <el-button type="danger" size="small" icon="el-icon-minus" @click="handleRemoveBuyOrderItem(scope.$index, addObj.buyOrderItems)">
                                    </el-button>
                            </template>
                        </el-table-column>
                    </el-table>

                    <el-form-item style="margin-top: 10px;">
                        <el-button type="primary" size="medium" icon="el-icon-plus" @click="handleAddBuyOrderItem(addObj.buyOrderItems)"></el-button>
                    </el-form-item>

                </el-form-item>

                <el-form-item label="申请描述" prop="description">
                    <el-input type="textarea" :rows="4" v-model="addObj.description" autosize></el-input>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button type="primary" :loading="addLoading" @click="handleAddFormSubmit('addForm')">提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import util from '../../common/js/util'
    import { requestApi, Enum, msgUtils } from '../../api/api';
    import { getNewDataAndMap } from '../../api/tableRowAndColumn';
    import CustomForm from '../../components/custom-form/index.vue';


    export default {
        data() {

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


            return {
                state2: '',
                visible2: false,
                // 页面整体加载状态
                listLoading: false,
                // 所有部门名称与id
                allDepartments: [],
                // 所有user的id和名称
                allUsers: [],
                // 采购列表显示
                buyOrders: [],
                dataMap: [],
                rowIndex1: 0,
                // 单个审核时提交审核的页面加载状态
                approvalLoading: false,
                // 批量删除
                sels: [],
                // 点击批量审核按钮时的页面加载状态
                batchApprovalLoading: false,
                // 分页
                pagination: {
                    total: 0,
                    currentPage: 1,
                    pageSizes: [10, 20, 50, 100],
                    pageSize: 10
                },
                selectRowLoading: false, //控制表单明细的加载
                allLikeNames: [] , //表单明细中的所有搜索到的input的名称
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
                allSorts: [], // 种类选择框
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
                errMsg: false,
                errMsg1: false,
                // filter相关的
                filterVisible: false,
                filterRequestors: [],
                filters: {
                    price_start: '',
                    price_end: '',
                    requestorId: '',
                    departmentId: '',
                    requestTime_start: '',
                    requestTime_end: ''
                },
                filterRules: {
                    price_start: [
                        { type:'number', min: 0,  message: '要求大于等于0的数字', trigger: 'blur' }
                    ],
                    price_end: [
                        { type:'number', min: 0,  message: '要求大于等于0的数字', trigger: 'blur' }
                    ],
                    requestTime_start: [
                        { type: 'date', message: '日期格式错误', trigger: 'change' }
                    ],
                    requestTime_end: [
                        { type: 'date', message: '日期格式错误', trigger: 'change' }
                    ]
                },

                // 新增界面
                addFormVisible: false,
                addLoading: false,
                canAdd: true,
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
                    requestTime: [
                        {required: true, message: '请输入申请时间', trigger: 'blur'}
                    ]
                }
            };
        },

        components: {
            CustomForm
        },

        methods: {
            // 排序
            sortAll(sort) {
                var propName = sort.prop;
                if(propName === 'creatorName'){
                    propName = 'createBy';
                }
                if(propName === 'updatorName'){
                    propName = 'lastmodifiedBy';
                }
                // 把所有name的都转为Id
                propName = propName.replace('Name','Id');
                this.filters.property = propName;
                if(sort.order === 'ascending'){
                    this.filters.direction = 'ASC';
                }
                if(sort.order === 'descending'){
                    this.filters.direction = 'DESC';
                }
                this.getBuyOrders();
            },

            // 设置 是否入库 标志
            formatInputFlag(row, column) {
                if(row.hasBeenInput === 1){
                    return '是';
                }else{
                    return '否'
                }
            },
            // filter的
            onFilterClose() {
                this.getBuyOrders();
            },
            refreshData() {
                this.getBuyOrders();
                this.filterVisible = false;
            },
            handleResetFilter() {
                this.filters = {
                    price_start: '',
                    price_end: '',
                    requestorId: '',
                    departmentId: '',
                    requestTime_start: '',
                    requestTime_end: ''
                }
            },
            handleFilterAdd() {
                this.filterVisible = true;
            },
            selectFilterDepartment(val) {
                // 这个方法必须传formData
                let formData  = new FormData();
                formData .append('departmentId', val);
                requestApi.user.findByDepartment(this, formData).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code) {
                        this.filterRequestors = res.data;
                    }else {
                        msgUtils.warning(this, res.msg);
                    }
                }, error=>{
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
            },
            checkAddBuyOrderItems() {
                var buyOrderItems = this.addObj.buyOrderItems;
                let  rexPrice = /^[0-9]+(.[0-9]{1,2})?$/;
                //let  rexNum = /^[1-9]\d*$/;
                let  rexNum =  /^\d+$/;
                this.canAdd = true;
                if(buyOrderItems.length === 0){
                    // 明细不能为空
                    this.canAdd = false;
                    msgUtils.warning(this, '明细不能为空');
                    return;
                }
                buyOrderItems.forEach(item => {
                    // 先验证名字
                    if(item.name === '' ){
                        msgUtils.warning(this, '请先选择物品名称');
                        this.canAdd = false;
                        return;
                    }
                    // 验证单价是否保留两位以内小数
                    if(!rexPrice.test(item.price)) {
                        msgUtils.warning(this, '有个单价格式不正确，至多保留两位以内小数！');
                        this.canAdd = false;
                        return;
                    }
                    // 验证数量是否为整数
                    if(!rexNum.test(item.number)) {
                        msgUtils.warning(this, '请在数量一列中输入整数！');
                        this.canAdd = false;
                        return;
                    }
                    for(var i in item){
                        if(item[i] === '') {
                            msgUtils.warning(this, '物品明细数据不能留空！');
                            this.canAdd = false;
                            return;
                        }
                    }
                });
            },

            getSummaries(param) {
                const { columns, data } = param;
                const sums = [];
                columns.forEach((column, index) => {
                    if (index === 0) {
                        sums[index] = '汇总';
                        return;
                    }else if(column.property === 'number'){
                        let total_number = 0;
                        data.forEach(item => {
                            total_number = total_number + Number(item[column.property]);
                        });
                        sums[index] = '总数量为: ' + total_number;
                    }else if(column.property === 'price'){
                        let total_price = 0;
                        data.forEach(item => {
                            total_price = total_price + Number(item['number'])*Number(item[column.property]);
                        });
                        sums[index] = '申请总价为: ' + total_price.toFixed(2) + ' 元';
                    }else{
                        sums[index] = '';
                    }
                });
                return sums;
            },

            handleSelect(buyOrderItem, selected_item) {
                buyOrderItem.goodId = selected_item.id;
                buyOrderItem.name = selected_item.name;
                buyOrderItem.sort = selected_item.sort;
                buyOrderItem.spec = selected_item.spec;
                buyOrderItem.unit = selected_item.unit;
                buyOrderItem.price = selected_item.price;
            },
            // 远程搜索用户名称
            querySearchAsync(queryString, cb) {
                if (queryString !== '') {
                    this.loading = true;
                    // 获取所有user的信息
                    requestApi.good.findByName(this, queryString).then(res => {
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code){
                            //this.allLikeNames = res.data;
                            // 要先填充value属性
                            let goods = res.data;
                            goods.forEach(good => {
                                good.value = good.name + "(" + good.spec + ")";
                            });
                            cb(goods);
                        }else{
                            msgUtils.warning(this, res.msg);
                        }
                    },err => {
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                    });
                }
            },
            handleCurrentChange(val) {
                this.pagination.currentPage = val;
                this.getBuyOrders();
            },
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getBuyOrders();
            },
            getBuyOrders() {
                this.listLoading = true;
                // 获取所有部门信息
                requestApi.department.findAll(this, 0, 1000).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.allDepartments = res.data.content;
                    }else{
                        msgUtils.warning(this, res.msg);
                    }
                },err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
                // 获取所有物品种类
                requestApi.good.findAllSorts(this).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.allSorts = res.data;
                    }else{
                        msgUtils.warning(this, Enum.GET_SORTS_FAIL.msg);
                    }
                },err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
                // 动态查询采购表
                let formData  = new FormData();
                formData .append('page', this.pagination.currentPage);
                formData .append('size', this.pagination.pageSize);
                formData .append('price_start', this.filters.price_start);
                formData .append('price_end', this.filters.price_end);
                formData .append('departmentId', this.filters.departmentId);
                formData .append('requestorId', this.filters.requestorId);
                formData .append('requestTime_start', this.filters.requestTime_start);
                formData .append('requestTime_end', this.filters.requestTime_end);
                if(this.filters.property && this.filters.direction){
                    formData .append('property', this.filters.property);
                    formData .append('direction', this.filters.direction);
                }
                requestApi.buyOrder.findByParam(this, formData).then(res => {
                    this.listLoading = false;
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.pagination.total = res.data.total;
                        let rs = getNewDataAndMap("buyOrderItems",res.data.data);
                        this.buyOrders = rs["data"];
                        this.dataMap = rs["map"];
                    }
                }, err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
            },

            // 清除验证提示
            clearValidate(formName) {
                this.$refs[formName].clearValidate();
                this.canAdd = false;
                // 清空选项
                this.addRequestors  = [];
            },

            arraySpanMethod({ row, column, rowIndex, columnIndex }) {
                let map = this.dataMap;
                if (columnIndex < 2 || columnIndex > 7) {
                    if(map[rowIndex] != null){
                        return {
                            rowspan: map[rowIndex],
                            colspan: 1
                        };
                    }else{
                        return {
                            rowspan: 0,
                            colspan: 0
                        };
                    }
                }
            },

            // 新增数据
            handleAdd() {
                this.addFormVisible = true;
                // 清空addObj
                util.emptyObj(this.addObj);
            },

            // 新增框选择部门下拉框触发
            selectAddDepartment(value) {
                this.addRequestors = [];
                // 这个方法必须传formData
                let formData  = new FormData();
                formData .append('departmentId', value);
                requestApi.user.findByDepartment(this, formData).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code) {
                        this.addRequestors = res.data;
                    }else {
                        msgUtils.warning(this, res.msg);
                    }
                }, error=>{
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
            },

            // 新增框或者编辑框点击物品明细的移除按钮移除物品明细
            handleRemoveBuyOrderItem(index, buyOrderItems) {
                buyOrderItems.splice(index, 1);
                //this.checkBuyOrderItems(this.addObj.buyOrderItems);
            },

            // 点击添加框或者编辑框的添加物品明细按钮
            handleAddBuyOrderItem(buyOrderItems) {
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
                        this.checkAddBuyOrderItems();
                        if(this.canAdd) {
                            this.$confirm('确认提交吗？', '提示', {}).then(() => {
                                this.addLoading = true;
                                let para = this.addObj;
                                requestApi.buyOrder.add(this, para).then(res => {
                                    res = res.body;
                                    this.addFormVisible = false;
                                    this.addLoading = false;
                                    if(res.code === Enum.SUCCESS.code){
                                        this.getBuyOrders();
                                    }else{
                                        msgUtils.warning(res.msg);
                                    }
                                }, err => {
                                    this.addFormVisible = false;
                                    this.addLoading = false;
                                    msgUtils.error(Enum.SYSTEM_ERROR.msg);
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
            quickInput() {
                let para = [];
                let bool = true;
                this.sels.forEach(item => {
                    if(item.approvalResult !== 1) {
                        bool = false;
                    }else {
                        para.push(item.id);
                    }
                });
                if(!bool) {
                    msgUtils.warning(this, '有部分或全部采购单未审核通过，请选择审核通过的采购单！');
                    return false;
                }
                this.$confirm('确认入库吗？', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    requestApi.buyOrder.quickInput(this, para).then(res => {
                        this.listLoading = false;
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code){
                            msgUtils.success(this, "入库成功！");
                            this.getBuyOrders();
                        }else{
                            msgUtils.warning(res.msg);
                        }
                    },error => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
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
            approve(id, approvalResult) {
                let obj = {
                    id: id,
                    approvalResult: approvalResult
                };
                let para = [];
                para.push(obj);
                this.$confirm('确认提交吗？', '提示', {}).then(() => {
                    this.listLoading = true;
                    requestApi.buyOrder.approve(this, para).then(res => {
                        this.listLoading = false;
                        res = res.body;
                        if (res.code === Enum.SUCCESS.code) {
                            this.getBuyOrders();
                        } else {
                            msgUtils.warning(this, res.msg);
                        }
                    }, error => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                    });
                });
            },

            // 判断是否禁用编辑按钮(如果已经是已经被审核的，也就是approveResult不是2的就不能进行编辑)
            checkInApproval(approvalResult) {
                let bool = false;
                if(approvalResult === 2) {
                    bool = false;
                }else {
                    bool = true;
                }
                return bool;
            },

            // 批量选择相关函数
            selsChange: function (sels) {
                this.sels = sels;
            },

            // 批量审核
            batchApprove(approvalResult){
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
                    msgUtils.warning(this, '存在已审核的纪录！');
                    return false;
                }
                let model_msg = '';
                if(approvalResult === 1) {
                    model_msg = '确认审核通过？';
                }else {
                    model_msg = '确认审核不通过？';
                }
                this.$confirm(model_msg, '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    requestApi.buyOrder.approve(this, approvalArr).then(res => {
                        this.listLoading = false;
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code) {
                            this.getBuyOrders();
                        }else {
                            msgUtils.warning(this, res.msg);
                        }
                    }, err => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                    });
                });
            },

            // 批量删除
            batchRemove() {
                let para = this.sels.map(item => {
                    return {id: item.id};
                });
                this.$confirm('确认删除该记录吗?', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    requestApi.buyOrder.deletes(this, para).then(res => {
                        this.listLoading = false;
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code) {
                            this.getBuyOrders();
                        }else {
                            msgUtils.warning(this, res.msg);
                        }
                    }, err => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                    });
                });
            },

            // 点击编辑按钮触发
            handleEdit(index,row) {
                this.listLoading = true;

                this.editRequestors = [];
                // 这个方法必须传formData
                let formData_0  = new FormData();
                formData_0 .append('departmentId', row.departmentId);
                requestApi.user.findByDepartment(this, formData_0).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code) {
                        this.editRequestors = res.data;
                    }else {
                        msgUtils.warning(this, res.msg);
                    }
                }, error=>{
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });

                let formData = new FormData();
                formData.append('buyOrderId', row.id);
                requestApi.buyOrder.findById(this, formData).then(res => {
                    this.listLoading = false;
                    res = res.body;
                    this.editObj = res;
                }, error=>{
                    this.listLoading = false;
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
                this.listLoading = false;
                this.editFormVisible = true;
            },

            // 点击编辑框的部门下拉框触发
            selectEditDepartment(value) {
                this.editRequestors = [];
                // 这个方法必须传formData
                let formData  = new FormData();
                formData .append('departmentId', value);
                requestApi.user.findByDepartment(this, formData).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code) {
                        this.addRequestors = res.data;
                    }else {
                        msgUtils.warning(this, res.msg);
                    }
                }, error=>{
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
            },


            // 提交编辑后数据
            handleEditFormSubmit(formName) {

                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.checkAddBuyOrderItems();
                        if(this.canAdd) {
                            this.$confirm('确认提交吗？', '提示', {}).then(() => {
                                this.editLoading = true;
                                let para = this.editObj;
                                requestApi.buyOrder.update(this, para).then(res => {
                                    res = res.body;
                                    this.editFormVisible = false;
                                    this.editLoading = false;
                                    if(res.code === Enum.SUCCESS.code){
                                        this.getBuyOrders();
                                    }else{
                                        msgUtils.warning(res.msg);
                                    }
                                }, err => {
                                    this.editFormVisible = false;
                                    this.editLoading = false;
                                    msgUtils.error(Enum.SYSTEM_ERROR.msg);
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
                    requestApi.buyOrder.delete(this, para).then(res => {
                            this.listLoading = false;
                            res = res.body;
                            if(res.code === Enum.SUCCESS.code) {
                                this.getBuyOrders();
                            }else {
                                msgUtils.warning(this, res.msg);
                            }
                        }, err => {
                            this.listLoading = false;
                            msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
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
                    case 0: {scope.row.tag = '已驳回';return 'danger';break;}
                    case 1: {scope.row.tag = '已通过';return 'success';break;}
                    case 2: {scope.row.tag = '审核中';return 'primary';break;}
                    default: {scope.row.tag = '审核中';return 'primary';break;}
                }
            }
        },

        mounted() {
            this.getBuyOrders();
        }
    }

</script>

<style>
    .pagination {
        float: right;
    }
    .line {
        text-align: center;
    }
    .el-table__body-wrapper, .el-table__body-wrapper.is-scroll-right, .el-table__body-wrapper.is-scroll-left {
        overflow-y: hidden;
    }
</style>