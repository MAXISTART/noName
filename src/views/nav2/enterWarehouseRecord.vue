<template>
    <section>
        <!--工具条-->
        <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
            <el-form :inline="true" :rules="queryRules" :model="queryObj">
                <!--<el-form-item>-->
                    <!--<el-select v-model="queryKeyDefault" placeholder="请选择">-->
                        <!--<el-option-->
                                <!--v-for="item in enterWarehouseQueryKeyGroup"-->
                                <!--:key="item.value"-->
                                <!--:label="item.label"-->
                                <!--:value="item.value">-->
                        <!--</el-option>-->
                    <!--</el-select>-->
                <!--</el-form-item>-->
                <el-form-item prop="name">
                    <el-autocomplete
                            class="inline-input"
                            v-model="queryObj.name"
                            :fetch-suggestions="querySearch"
                            placeholder="请输入物品名称"
                            :trigger-on-focus="false"
                            @select="handleNameSelect"
                    ></el-autocomplete>
                </el-form-item>
                <el-form-item>
                    <el-col :span="11">
                        <el-form-item prop="price_start">
                            <el-input v-model="queryObj.price_start" placeholder="请输入最小单价" style="width: 100%;"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col class="line" :span="1" style="margin-left: 2px;">-</el-col>
                    <el-col :span="11">
                        <el-form-item prop="price_end">
                            <el-input v-model="queryObj.price_end" placeholder="请输入最大单价" style="width: 100%;"></el-input>
                        </el-form-item>
                    </el-col>
                </el-form-item>
                <el-form-item prop="sort">
                    <el-select v-model="queryObj.sort" filterable placeholder="请选择" @change="handleSortSelect">
                        <el-option
                                v-for="item in sortNames"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <!--<el-form-item v-if="datePickerShow">-->
                    <!--<el-date-picker-->
                            <!--unlink-panels-->
                            <!--format="yyyy-MM-dd"-->
                            <!--value-format="yyyy-MM-dd"-->
                            <!--v-model="dateDefault"-->
                            <!--type="daterange"-->
                            <!--range-separator="至"-->
                            <!--start-placeholder="开始日期"-->
                            <!--end-placeholder="结束日期">-->
                    <!--</el-date-picker>-->
                <!--</el-form-item>-->
                <el-form-item>
                    <el-button type="primary" @click="handleQuery">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleAdd">新增</el-button>
                </el-form-item>
            </el-form>
        </el-col>

        <!--列表-->
        <el-table :data="enterWarehouseRecords" highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
            <el-table-column type="selection" width="55" column-key="id" fixed="left">
            </el-table-column>
            <el-table-column type="index" width="60" fixed="left">
            </el-table-column>
            <el-table-column label="入库明细" width="600">
                <el-table-column prop="name" label="名称" width="100" column-key="id"></el-table-column>
                <el-table-column prop="sort" label="种类" width="100"></el-table-column>
                <el-table-column prop="spec" label="规格" width="100"></el-table-column>
                <el-table-column prop="unit" label="单位" width="100"></el-table-column>
                <el-table-column prop="number" label="数量" width="100" sortable></el-table-column>
                <el-table-column prop="price" label="单价" width="100" sortable></el-table-column>
            </el-table-column>
            <el-table-column prop="createAdmin" label=" 创建者" width="100">
            </el-table-column>
            <el-table-column type="date" prop="createTime" label="创建时间" width="160" sortable>
            </el-table-column>
            <el-table-column type="date" prop="updateTime" label="更新时间" min-width="160" sortable>
            </el-table-column>
            <el-table-column prop="updateAdmin" label="提交人" min-width="130">
            </el-table-column>
            <el-table-column label="操作" width="170">
                <template slot-scope="scope">
                    <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <el-button type="danger" :disabled="this.sels.length === 0" @click="batchRemove">批量删除</el-button>
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
        <el-dialog title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false">
            <el-form :model="editGoodItem" label-width="80px" :rules="editFormRules" ref="editForm">
                <el-form-item label="物品名称" prop="name">
                    <el-input v-model="editGoodItem.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="入库数量" prop="number">
                    <el-input v-model="editGoodItem.number" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="价格" prop="price">
                    <el-input v-model="editGoodItem.price" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="种类" prop="sort">
                    <el-select v-model="editGoodItem.sort" placeholder="请选择">
                        <el-option
                                v-for="item in sortNames"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="规格" prop="spec">
                    <el-input v-model="editGoodItem.spec" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="单位" prop="unit">
                    <el-input v-model="editGoodItem.unit" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button>取消</el-button>
                <el-button type="primary" :loading="editLoading" @click="submitEditForm('editForm')">提交</el-button>
            </div>
        </el-dialog>

        <!--新增界面-->
        <el-dialog title="新增" :visible.sync="addFormVisible" :close-on-click-modal="false">
            <el-form :model="newGoodItem" label-width="80px" :rules="addFormRules" ref="addForm">
                <el-form-item label="物品名称" prop="name">
                    <el-input v-model="newGoodItem.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="入库数量" prop="number">
                    <el-input v-model="newGoodItem.number" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="价格" prop="price">
                    <el-input v-model="newGoodItem.price" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="种类" prop="sort">
                    <el-select v-model="newGoodItem.sort" placeholder="请选择" @change="addFormSortSelected">
                        <el-option
                                v-for="item in sortNames"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="规格" prop="spec">
                    <el-input v-model="newGoodItem.spec" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="单位" prop="unit">
                    <el-input v-model="newGoodItem.unit" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button>取消</el-button>
                <el-button type="primary" :loading="addLoading" @click="submitAddForm('addForm')">提交</el-button>
            </div>
        </el-dialog>
    </section>
</template>

<script>
    import util from '../../common/js/util'
    //import NProgress from 'nprogress'
    import {getEnterWarehouseRecordListpage } from '../../api/api';
    import { ERR_OK0, ERR_OK1, ERR_OK2 } from '@/common/config/config';
    import { addNewThing } from '@/api/api';
    import axios from 'axios';

    export default {
        data() {
            // 验证数量
            var checkTotal = (rule, value, callback) => {
                // if(!value) {
                //     return callback(new Error('请输入数量！'));
                // }
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

            var checkPriceQuery = (rule, value, callback) => {
                let  rex = /^[0-9]+(.[0-9]{1,2})?$/;
                console.log(value);
                setTimeout(() => {
                    if(!rex.test(value)) {
                        callback("单价格式不正确！");
                    }else {
                        callback();
                    }
                }, 1000);
            };

            return {
                // 初始化获取到的信息
                goods:[],
                // 查询字段相关参数
                queryObj: {
                    page: 0,
                    name: '',
                    price_start: '',
                    price_end: '',
                    sort: ''
                }, // 条件查询时post出去的值
                // 物品查询检查
                queryRules: {
                    price_start: [
                        { validator: checkPriceQuery, trigger: 'blur' },
                    ],
                    price_end: [
                        { validator: checkPriceQuery, trigger: 'blur' },
                    ]
                },
                //新增界面数据
                newGoodItem: {},
                // 编辑界面相关数据
                editGoodItem: {},
                editFormVisible: false,//编辑界面是否显示
                editLoading: false,
                editFormRules: {
                    name: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ],
                    number: [
                        { validator: checkTotal, trigger: 'blur' }
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
                sortNames: [],
                queryValueSelected: '',
                // 查询入库记录条件
                dateShow: false,
                inputShow: false,
                queryKeyDefault: '',
                enterWarehouseQueryKeyGroup: [],
                // 入库记录
                enterWarehouseRecords: [],
                pagenation: {
                    totalElements: 0,
                    currentPage: 1,
                    pageSizes: [10, 20, 50, 100],
                    pageSize: 20
                },
                listLoading: false,
                sels: [],//列表选中列

                // 新增界面的参数
                addFormVisible: false,//新增界面是否显示
                addLoading: false,
                // 物品添加检查
                addFormRules: {
                    name: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
                    ],
                    number: [
                        { validator: checkTotal, trigger: 'blur' }
                    ],
                    price: [
                        { validator: checkUnitPrice, trigger: 'blur' },
                        {required: true, message: '请输入单价', trigger: 'blur'}
                    ],
                    sort: [
                        {required: true, message: '请输入物品种类', trigger: 'change' }
                    ],
                    spec: [
                        {required: true, message: '请输入物品规格', trigger: 'blur'}
                    ],
                    unit: [
                        {required: true, message: '请输入物品单位', trigger: 'blur'}
                    ]
                }
            };
        },
        methods: {
            // selectQueryKey(value) {
            //     if(value === 'name') {
            //         this.sortQueryShow = false;
            //         this.priceQueryShow = false;
            //         this.nameQueryShow = true;
            //     }else if(value === 'all') {
            //         this.nameQueryShow = false;
            //         this.sortQueryShow = false;
            //         this.priceQueryShow = false;
            //     }else if(value === 'sort') {
            //         this.nameQueryShow = false;
            //         this.priceQueryShow = false;
            //         this.sortQueryShow = true;
            //     }else if(value === 'price') {
            //         this.nameQueryShow = false;
            //         this.sortQueryShow = false;
            //         this.priceQueryShow = true;
            //     }else {
            //         this.nameQueryShow = false;
            //         this.sortQueryShow = false;
            //         this.priceQueryShow = false;
            //     }
            //     this.queryValueSelected = value;
            // },

            // 物品名称input查询函数
            querySearch(queryString, cb) {
                let goods = this.goods;
                let results = queryString ? goods.filter(this.createFilter(queryString)) : goods;
                console.log(results);
                // 调用 callback 返回建议列表的数据
                cb(results);
            },
            createFilter(queryString) {
                return (good) => {
                    return (good.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
                };
            },
            // 名字模糊搜索查询点击后触发
            handleNameSelect(item) {
                this.queryObj.name = item.value;
            },
            // 种类选择后触发
            handleSortSelect(item) {
                this.queryObj.querySort = item;
            },
            // 点击查询按钮触发
            handleQuery(e) {
              e.preventDefault();
              let queryObj = this.queryObj;
              this.$http.post('/api/admin/good/findGoodItemsBySearchParams', queryObj, { emulateJSON: true}).then(res => {
                    let code = res.code;
                    if(code === ERR_OK0) {
                        let goods = res.data.data;
                        this.enterWarehouseRecords = goods;
                        this.pagenation.totalElements = goods.length;
                    }
              }, err => {
                  console.log(err);
              });
            },
            // //性别显示转换
            // formatSex: function (row, column) {
            //     return row.sex == 1 ? '男' : row.sex == 0 ? '女' : '未知';
            // },
            // handleCurrentChange(val) {
            //     this.page = val;
            //     this.getUsers();
            // },
            // 下拉框选择查询字段
            handleSizeChange(val) {
                this.pagenation.pageSize = val;
                let page = this.pagenation.currentPage;
                let size = this.pagenation.pageSize;
                let queryObj = this.queryObj;
                let params = { page: page,
                    size: size,
                    name: queryObj.name,
                    price_start: queryObj.price_start,
                    price_end: queryObj.price_end,
                    sort: queryObj.sort
                };
                this.$http.post('/api/admin/good/findGoodItemsBySearchParams', params, { emulateJSON: true}).then(res => {
                    let code = res.code;
                    if(code === ERR_OK0) {
                        this.enterWarehouseRecords = res.data.data;
                    }else {
                        let msg = res.msg;
                        alert(msg);
                    }
                }, err => {
                    alert('系统错误，请重新刷新页面！  '  + err);
                });
            },

            handleCurrentChange(val) {
                let page = val;
                let size = this.pagenation.pageSize;
                let queryObj = this.queryObj;
                let params = { page: page,
                    size: size,
                    name: queryObj.name,
                    price_start: queryObj.price_start,
                    price_end: queryObj.price_end,
                    sort: queryObj.sort
                };
                this.$http.post('/api/admin/good/findGoodItemsBySearchParams', params, { emulateJSON: true}).then(res => {
                    let code = res.code;
                    if(ERR_OK0 === code) {
                        this.enterWarehouseRecords = res.data.data;
                    }else {
                        let msg = res.msg;
                        alert(msg);
                    }
                }, err => {
                        alert('系统错误，请重新刷新页面！  ' + err);
                });
            },

            //显示新增界面
            handleAdd() {
                this.addFormVisible = true;
                this.addForm = {
                    name: '',
                    number: 0,
                    price: 0,
                    sort: '',
                    spec: '',
                    unit: ''
                };
            },

            addFormSortSelected(item) {
                this.newGoodItem.sort =item;
            },

            // 提交新增加物品
            submitAddForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.$confirm('确认提交吗？', '提示', {}).then(() => {
                            this.addLoading = true;
                            let para = this.newGoodItem;
                            this.$http.post('/api/admin/good/addGoodItem', para).then(res => {
                                let code = res.code;
                                if(code === ERR_OK0) {
                                    alert("物品添加成功！");
                                    setTimeout(() => {
                                        history.go(0);
                                    }, 200);
                                }else {
                                    alert(res.msg);
                                }
                            }, err => {
                                alert('系统错误，请重新刷新页面！');
                            });
                        });
                    } else {
                        return false;
                    }
                });
            },

            //显示编辑界面
            handleEdit: function (index, row) {
                this.editFormVisible = true;
                this.editGoodItem = Object.assign({}, row);
            },

            //编辑界面提交
            submitEditForm: function () {
                this.$refs.editForm.validate((valid) => {
                    if (valid) {
                        this.$confirm('确认提交吗？', '提示', {}).then(() => {
                            this.editLoading = true;
                            //NProgress.start();
                            let para = {};
                            para.id = this.editGoodItem.id;
                            para.name = this.editGoodItem.name;
                            para.sort = this.editGoodItem.sort;
                            para.unit = this.editGoodItem.unit;
                            para.price = this.editGoodItem.price;
                            para.spec = this.editGoodItem.spec;
                            para.number = this.editGoodItem.number;
                            console.log(para);
                            this.$http.post('/api/admin/good/updateGoodItem',para).then(res => {
                                let code = res.code;
                                if(ERR_OK0 === code) {
                                    alert('编辑成功！');
                                    setTimeout(() => {
                                        history.go(0);
                                    }, 200);
                                }else {
                                    let msg = res.msg;
                                    alert(msg);
                                }
                            }, err => {
                                alert('系统错误，请重新刷新页面！   ' + err);
                            });
                        });
                    }
                });
            },

            // 删除提交
            handleDel: function (index, row) {
                this.$confirm('确认删除该记录吗?', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    let para = { id: row.id };
                    this.$http.post('/api/admin/good/deleteGoodItem',para).then(res => {
                        let code = res.code;
                        if(ERR_OK0 === code) {
                            alert('删除成功！');
                            setTimeout(() => {
                                history.go(0);
                            }, 200);
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面！   ' + err);
                    });
                    // removeUser(para).then((res) => {
                    //     this.listLoading = false;
                    //     //NProgress.done();
                    //     this.$message({
                    //         message: '删除成功',
                    //         type: 'success'
                    //     });
                    //     this.getUsers();
                    // });
                });
            },
            selsChange: function (sels) {
                this.sels = sels;
            },
            //批量删除
            batchRemove: function () {
                let para = this.sels.map(item => {
                    return {id: item.id};
                });
                this.$confirm('确认删除选中记录吗？', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    this.$http.post('/api/admin/good/deleteGoodItems', para).then(res => {
                        this.listLoading = false;
                        console.log(res);
                        let code = res.code;
                        if(code === ERR_OK0) {
                            alert('删除成功！');
                            history.go(0);
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {
                        alert('系统错误，请重新刷新页面   ' + err);
                    });
                });
            }
        },
        mounted() {
            let tableId = 'enterWarehouseRecords';
            let currentPage = 1;
            let pageSize = 20;
            let queryKey = this.queryKey;
            let params = {
                page: 0
            };

            // /admin/config/getInitData
            this.$http.get('/api/admin/config/getInitData').then(res => {
                let sortNames = res.data.data.allSorts; // 获得种类参数
                let goods = res.data.data.allGoods; // 获得物品所有参数
                for(let i in goods) {
                    let obj = {};
                    obj.id = goods[i].id;
                    obj.value = goods[i].name;
                    this.goods.push(obj);
                }
                for(let i in sortNames) {
                    let sortObj = {value: sortNames[i].name, label: sortNames[i].name};
                    this.sortNames.push(sortObj);
                }
            }, err => {
                alert("系统错误，请重新刷新页面！  " + err);
            });

            //  /admin/good/findAllGoodItems获取表格的东西
            this.$http.get('/api/admin/good/findAllGoodItems?page=0').then(res => {
                let code = res.code;
                let totalElements = res.data.data.totalElements;
                this.pagenation.totalElements = totalElements;
                console.log(totalElements);
                if(code === ERR_OK0) {
                    let goods = res.data.data.content;
                    this.enterWarehouseRecords = goods;
                }
                if(res.code === 0) {
                    let enterWarehouseRecords = res.data;

                }
            }, err => {
                console.log(err);
            });

            // 按条件查询的字段
            this.enterWarehouseQueryKeyGroup = [
                {
                    value: 'name',
                    label: '物品名称'
                },
                {
                    value: 'price',
                    label: '单价'
                },
                {
                    value: 'sort',
                    label: '种类'
                }
            ];
            // getEnterWarehouseRecordListpage(params).then(res => {
            //     console.log(res.data);
            //     //this.enterWarehouseQueryKeyGroup = res.data.enterWarehouseKey;
            //     //this.queryKeyDefault = res.data.enterWarehouseKey[0].value;
            //     //let total = res.data.total;
            //     let enterWarehouseRecords = res.data;
            //     this.enterWarehouseRecords = enterWarehouseRecords;
            //     //this.pagenation.total = total;
            // }).catch(function(err){
            //     console.log(err);
            // });
        }
    }

</script>

<style scoped>
    .pagenation {
        float: right;
    }
</style>