<template>
    <section>
        <!--工具条-->
        <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
            <el-form :inline="true" :model="filters">
                <el-form-item>
                    <el-select v-model="queryKeyDefault" placeholder="请选择" @change="selectQueryKey">
                        <el-option
                                v-for="item in enterWarehouseQueryKeyGroup"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="autocompleteShow">
                    <el-autocomplete
                            class="inline-input"
                            v-model="autocompleteDefault"
                            :fetch-suggestions="querySearch"
                            placeholder="请输入内容"
                            :trigger-on-focus="false"
                            @select="handleSelect"
                    ></el-autocomplete>
                </el-form-item>
                <el-form-item v-if="datePickerShow">
                    <el-date-picker
                            unlink-panels
                            format="yyyy-MM-dd"
                            value-format="yyyy-MM-dd"
                            v-model="dateDefault"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleAdd">新增</el-button>
                </el-form-item>
            </el-form>
        </el-col>

        <!--列表-->
        <el-table :data="enterWarehouseRecords" highlight-current-row v-loading="listLoading" style="width: 100%;">
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
            <el-table-column prop="totalPrice" label="入库总价" min-width="110" sortable>
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
                    <el-button size="small">编辑</el-button>
                    <el-button type="danger" size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <el-button type="danger" :disabled="this.sels.length===0">批量删除</el-button>
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
        <el-dialog title="编辑" v-model="editFormVisible" :close-on-click-modal="false">
            <el-form :model="editForm" label-width="80px" :rules="editFormRules" ref="editForm">
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="editForm.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="editForm.sex">
                        <el-radio class="radio" :label="1">男</el-radio>
                        <el-radio class="radio" :label="0">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄">
                    <el-input-number v-model="editForm.age" :min="0" :max="200"></el-input-number>
                </el-form-item>
                <el-form-item label="生日">
                    <el-date-picker type="date" placeholder="选择日期" v-model="editForm.birth"></el-date-picker>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input type="textarea" v-model="editForm.addr"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button>取消</el-button>
                <el-button type="primary" :loading="editLoading">提交</el-button>
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
                    <el-select v-model="newGoodItem.sort" placeholder="请选择">
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
    import axios from 'axios';
    import {getEnterWarehouseRecordListpage } from '../../api/api';
    import { ERR_OK0, ERR_OK1, ERR_OK2 } from '@/common/config/config';
    import { addNewThing } from '@/api/api';

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

            return {
                //新增界面数据
                newGoodItem: {},
                sortDefault: '',
                sortNames: [],
                restaurants: [],
                queryValueSelected: '',
                /* 自动补充插件参数 */
                autocompleteShow: false,
                autocompleteDefault: '',
                /* 时间插件参数 */
                dateDefault: '',
                datePickerShow: false,
                filters: {
                    name: ''
                },
                users: [],
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

                editFormVisible: false,//编辑界面是否显示
                editLoading: false,
                editFormRules: {
                    name: [
                        {required: true, message: '请输入姓名', trigger: 'blur'}
                    ]
                },
                //编辑界面数据
                editForm: {
                    id: 0,
                    name: '',
                    sex: -1,
                    age: 0,
                    birth: '',
                    addr: ''
                },

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
                }
            };
        },
        methods: {
            selectQueryKey(value) {
                if(value === 'enterTime' || value === 'updateTime' || value === 'submitTime') {
                    this.datePickerShow = true;
                    this.autocompleteShow = false;
                }else if(value === 'all') {
                    this.datePickerShow = false;
                    this.autocompleteShow = false;
                }else {
                    this.datePickerShow = false;
                    this.autocompleteShow = true;
                }
                this.queryValueSelected = value;
            },
            querySearch(queryString, cb) {
                var restaurants = this.restaurants;
                var results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
                // 调用 callback 返回建议列表的数据
                cb(results);
            },
            createFilter(queryString) {
                return (restaurant) => {
                    return (restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
                };
            },
            loadAll() {
                let enterWarehouseQueryKeyGroup = this.enterWarehouseQueryKeyGroup;
                let queryValueSelected = this.queryValueSelected;
                for(var i in enterWarehouseQueryKeyGroup) {
                    if(enterWarehouseQueryKeyGroup[i].value === queryValueSelected) {
                        break;
                    }
                }
                return [
                    { "value": "三全鲜食（北新泾店）", "address": "长宁区新渔路144号" },
                    { "value": "Hot honey 首尔炸鸡（仙霞路）", "address": "上海市长宁区淞虹路661号" },
                    { "value": "新旺角茶餐厅", "address": "上海市普陀区真北路988号创邑金沙谷6号楼113" },
                    { "value": "泷千家(天山西路店)", "address": "天山西路438号" },
                    { "value": "胖仙女纸杯蛋糕（上海凌空店）", "address": "上海市长宁区金钟路968号1幢18号楼一层商铺18-101" },
                    { "value": "贡茶", "address": "上海市长宁区金钟路633号" },
                    { "value": "豪大大香鸡排超级奶爸", "address": "上海市嘉定区曹安公路曹安路1685号" },
                    { "value": "茶芝兰（奶茶，手抓饼）", "address": "上海市普陀区同普路1435号" },
                    { "value": "十二泷町", "address": "上海市北翟路1444弄81号B幢-107" },
                    { "value": "星移浓缩咖啡", "address": "上海市嘉定区新郁路817号" },
                    { "value": "阿姨奶茶/豪大大", "address": "嘉定区曹安路1611号" },
                    { "value": "新麦甜四季甜品炸鸡", "address": "嘉定区曹安公路2383弄55号" },
                    { "value": "Monica摩托主题咖啡店", "address": "嘉定区江桥镇曹安公路2409号1F，2383弄62号1F" },
                    { "value": "浮生若茶（凌空soho店）", "address": "上海长宁区金钟路968号9号楼地下一层" },
                    { "value": "NONO JUICE  鲜榨果汁", "address": "上海市长宁区天山西路119号" },
                    { "value": "CoCo都可(北新泾店）", "address": "上海市长宁区仙霞西路" },
                    { "value": "快乐柠檬（神州智慧店）", "address": "上海市长宁区天山西路567号1层R117号店铺" },
                    { "value": "Merci Paul cafe", "address": "上海市普陀区光复西路丹巴路28弄6号楼819" },
                    { "value": "猫山王（西郊百联店）", "address": "上海市长宁区仙霞西路88号第一层G05-F01-1-306" },
                    { "value": "枪会山", "address": "上海市普陀区棕榈路" },
                    { "value": "纵食", "address": "元丰天山花园(东门) 双流路267号" },
                    { "value": "钱记", "address": "上海市长宁区天山西路" },
                    { "value": "壹杯加", "address": "上海市长宁区通协路" },
                    { "value": "唦哇嘀咖", "address": "上海市长宁区新泾镇金钟路999号2幢（B幢）第01层第1-02A单元" },
                    { "value": "爱茜茜里(西郊百联)", "address": "长宁区仙霞西路88号1305室" },
                    { "value": "爱茜茜里(近铁广场)", "address": "上海市普陀区真北路818号近铁城市广场北区地下二楼N-B2-O2-C商铺" },
                    { "value": "鲜果榨汁（金沙江路和美广店）", "address": "普陀区金沙江路2239号金沙和美广场B1-10-6" },
                    { "value": "开心丽果（缤谷店）", "address": "上海市长宁区威宁路天山路341号" },
                    { "value": "超级鸡车（丰庄路店）", "address": "上海市嘉定区丰庄路240号" },
                    { "value": "妙生活果园（北新泾店）", "address": "长宁区新渔路144号" },
                    { "value": "香宜度麻辣香锅", "address": "长宁区淞虹路148号" },
                    { "value": "凡仔汉堡（老真北路店）", "address": "上海市普陀区老真北路160号" },
                    { "value": "港式小铺", "address": "上海市长宁区金钟路968号15楼15-105室" },
                    { "value": "蜀香源麻辣香锅（剑河路店）", "address": "剑河路443-1" },
                    { "value": "北京饺子馆", "address": "长宁区北新泾街道天山西路490-1号" },
                    { "value": "饭典*新简餐（凌空SOHO店）", "address": "上海市长宁区金钟路968号9号楼地下一层9-83室" },
                    { "value": "焦耳·川式快餐（金钟路店）", "address": "上海市金钟路633号地下一层甲部" },
                    { "value": "动力鸡车", "address": "长宁区仙霞西路299弄3号101B" },
                    { "value": "浏阳蒸菜", "address": "天山西路430号" },
                    { "value": "四海游龙（天山西路店）", "address": "上海市长宁区天山西路" },
                    { "value": "樱花食堂（凌空店）", "address": "上海市长宁区金钟路968号15楼15-105室" },
                    { "value": "壹分米客家传统调制米粉(天山店)", "address": "天山西路428号" },
                    { "value": "福荣祥烧腊（平溪路店）", "address": "上海市长宁区协和路福泉路255弄57-73号" },
                    { "value": "速记黄焖鸡米饭", "address": "上海市长宁区北新泾街道金钟路180号1层01号摊位" },
                    { "value": "红辣椒麻辣烫", "address": "上海市长宁区天山西路492号" },
                    { "value": "(小杨生煎)西郊百联餐厅", "address": "长宁区仙霞西路88号百联2楼" },
                    { "value": "阳阳麻辣烫", "address": "天山西路389号" },
                    { "value": "南拳妈妈龙虾盖浇饭", "address": "普陀区金沙江路1699号鑫乐惠美食广场A13" }
                ];
            },
            handleSelect(item) {
                console.log(item);
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
            chooseKey(value) {
                if(value === 'all') {
                    this.dateShow = false;
                    this.inputShow = false;
                }else if(value === 'enterTime' || value === 'updateTime' || value === 'submitTime') {
                    this.dateShow = true;
                    this.inputShow = false;
                }else {
                    this.inputShow = true;
                    this.dateShow = false;
                }
            },

            handleSizeChange(val) {
                this.pagenation.pageSize = val;
                let currentPage = this.pagenation.currentPage;
                let pageSize = this.pagenation.pageSize;
                let queryKey = this.queryValueSelected;
                let params = {tableId: 'enterWarehouseRecords', currentPage: currentPage, pageSize: pageSize, queryKey: queryKey};
                getEnterWarehouseRecordListpage(params).then(res => {
                    let enterWarehouseRecords = res.data.enterWarehouseRecordsList;
                    this.enterWarehouseRecords = enterWarehouseRecords;
                });
            },

            handleCurrentChange(val) {
                this.pagenation.currentPage = val;
                let currentPage = this.pagenation.currentPage;
                let pageSize = this.pagenation.pageSize;
                let queryKey = this.queryValueSelected;
                let params = {tableId: 'enterWarehouseRecords', currentPage: currentPage, pageSize: pageSize, queryKey: queryKey};
                getEnterWarehouseRecordListpage(params).then(res => {
                    let enterWarehouseRecords = res.data.enterWarehouseRecordsList;
                    this.enterWarehouseRecords = enterWarehouseRecords;
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
                this.editForm = Object.assign({}, row);
            },
            //删除
            // handleDel: function (index, row) {
            //     this.$confirm('确认删除该记录吗?', '提示', {
            //         type: 'warning'
            //     }).then(() => {
            //         this.listLoading = true;
            //         //NProgress.start();
            //         let para = { id: row.id };
            //         removeUser(para).then((res) => {
            //             this.listLoading = false;
            //             //NProgress.done();
            //             this.$message({
            //                 message: '删除成功',
            //                 type: 'success'
            //             });
            //             this.getUsers();
            //         });
            //     }).catch(() => {
            //
            //     });
            // },


            // //编辑
            // editSubmit: function () {
            //     this.$refs.editForm.validate((valid) => {
            //         if (valid) {
            //             this.$confirm('确认提交吗？', '提示', {}).then(() => {
            //                 this.editLoading = true;
            //                 //NProgress.start();
            //                 let para = Object.assign({}, this.editForm);
            //                 para.birth = (!para.birth || para.birth == '') ? '' : util.formatDate.format(new Date(para.birth), 'yyyy-MM-dd');
            //                 editUser(para).then((res) => {
            //                     this.editLoading = false;
            //                     //NProgress.done();
            //                     this.$message({
            //                         message: '提交成功',
            //                         type: 'success'
            //                     });
            //                     this.$refs['editForm'].resetFields();
            //                     this.editFormVisible = false;
            //                     this.getUsers();
            //                 });
            //             });
            //         }
            //     });
            // },
            //新增
            // addSubmit: function () {
            //     this.$refs.addForm.validate((valid) => {
            //         if (valid) {
            //             this.$confirm('确认提交吗？', '提示', {}).then(() => {
            //                 this.addLoading = true;
            //                 //NProgress.start();
            //                 let para = Object.assign({}, this.addForm);
            //                 para.birth = (!para.birth || para.birth == '') ? '' : util.formatDate.format(new Date(para.birth), 'yyyy-MM-dd');
            //                 addUser(para).then((res) => {
            //                     this.addLoading = false;
            //                     //NProgress.done();
            //                     this.$message({
            //                         message: '提交成功',
            //                         type: 'success'
            //                     });
            //                     this.$refs['addForm'].resetFields();
            //                     this.addFormVisible = false;
            //                     this.getUsers();
            //                 });
            //             });
            //         }
            //     });
            // },
            // selsChange: function (sels) {
            //     this.sels = sels;
            // },
            //批量删除
            // batchRemove: function () {
            //     var ids = this.sels.map(item => item.id).toString();
            //     this.$confirm('确认删除选中记录吗？', '提示', {
            //         type: 'warning'
            //     }).then(() => {
            //         this.listLoading = true;
            //         //NProgress.start();
            //         let para = { ids: ids };
            //         batchRemoveUser(para).then((res) => {
            //             this.listLoading = false;
            //             //NProgress.done();
            //             this.$message({
            //                 message: '删除成功',
            //                 type: 'success'
            //             });
            //             this.getUsers();
            //         });
            //     }).catch(() => {
            //
            //     });
            // }
        },
        mounted() {
            this.restaurants = this.loadAll();
            let tableId = 'enterWarehouseRecords';
            let currentPage = 1;
            let pageSize = 20;
            let queryKey = this.queryKey;
            let params = {
                page: 0
            };

            // /admin/config/getInitData
            this.$http.get('/api/admin/config/getInitData').then(res => {
                let sortNames = res.data.data.allSorts;
                for(let i in sortNames) {
                    let sortObj = {value: sortNames[i].id, label: sortNames[i].name};
                    this.sortNames.push(sortObj);
                }
            }, err => {
                alert("系统错误，请重新刷新页面！  " + err);
            });
            //  /admin/good/findAllGoodItems
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