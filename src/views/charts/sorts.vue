<template>
    <div>
        <!--工具条-->
        <el-col :span="24" class="toolbar">
            <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
        </el-col>
        <el-table :data="sortList" ref="sortListTable" v-loading="listLoading"
                  @selection-change="selsChange" style="width: 100%;">
            <el-table-column label="创建人" prop="createAdmin"></el-table-column>
            <el-table-column label="创建时间" prop="createTime"></el-table-column>
            <el-table-column label="种类名称" prop="name"></el-table-column>
            <el-table-column label="更新人" prop="updateAdmin"></el-table-column>
            <el-table-column label="更新时间" prop="updateTime"></el-table-column>
            <el-table-column label="操作" width="170">
                <template slot-scope="scope">
                    <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 编辑界面 -->
        <el-dialog title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false" @close="closeEditDialog('editForm')">
            <el-form :model="editObj" :rules="editFormRules" ref="editForm">
                <el-form-item label="种类名称" prop="name" label-width="80px">
                    <el-input v-model="editObj.name" placeholder="请输入种类名称" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="handleEditFormSubmit('editForm')">提交</el-button>
            </div>
        </el-dialog>

        <!-- 新增界面 -->
        <el-dialog title="新增" :visible.sync="addFormVisible" :close-on-click-modal="false" @close="closeAddDialog('addForm')">
            <el-form :model="addObj" :rules="addFormRules" ref="addForm">
                <el-form-item label="种类名称" prop="name" label-width="80px">
                    <el-input v-model="addObj.name" placeholder="请输入种类名称" auto-complete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="handleAddFormSubmit('addForm')">提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script type="text/ecmascript-6">
    import { ERR_OK0, ERR_OK1, ERR_OK2, SYS_ERROR_MSG } from '@/common/config/config';
    import util from '@/common/js/util';
    export  default {
        data() {
            return {
                // 表格加载状态
                listLoading: false,
                // 新增框的参数
                addFormVisible: false,
                    // 新增框的检查规则
                addObj: {
                    name: ''
                },
                addFormRules: {
                    name: [
                        {required: true, message: '请输入种类名称', trigger: 'blur'}
                    ]
                },
                // 列表显示
                sortList: [],

                // 编辑框数据
                editFormVisible: false,
                editObj: {
                    id: '',
                    name: ''
                },
                // 验证编辑框的数据
                editFormRules: {
                    name: [
                        {required: true, message: '请输入种类名称', trigger: 'blur'}
                    ]
                },
                // 被选中的数据组成的数组
                sels: []
            };
        },

        methods: {
            // 获取所有种类信息
            getAllSorts() {
                this.$http.get('/api/admin/good/findAllSorts').then(res => {
                    let code = res.data.code;
                    if(code === ERR_OK0) {
                        let sortList = res.data.data;
                        this.sortList = sortList;
                        let sortGroup = [];
                        for(let i in sortList) {
                            let sortObj = {value: sortList[i].name, label: sortList[i].name};
                            sortObj = JSON.stringify(sortObj);
                            sortGroup.push(sortObj);
                        }
                        sessionStorage.sortGroup = sortGroup;
                    }
                }, err => {
                    alert(SYS_ERROR_MSG + err);
                });
            },

            // 批量选择表格数据
            selsChange(sels) {
                this.sels = sels;
            },

            // 新增种类框clearValidate
            handleAdd() {
                this.addFormVisible = true;
                util.emptyObj(this.addObj);
            },

            // 关闭新增框触发
            closeAddDialog(formName){
                this.$refs[formName].clearValidate();
            },

            // 提交新增种类数据
            handleAddFormSubmit(formName) {
                this.$refs[formName].validate(valid => {
                   if(valid) {
                       this.$confirm('确认提交吗？', '提示', {}).then(() => {
                           this.listLoading = true;
                           let para = this.addObj;

                           this.$http.post('/api/admin/good/addSortItem', para).then(res => {
                               let code = res.data.code;
                               if(code === ERR_OK0) {
                                   alert('种类添加成功！');
                                   this.listLoading = false;
                                   this.addFormVisible = false;
                                   this.getAllSorts();
                               }else {
                                   let msg = res.msg;
                                   alert(msg);
                               }
                           }, err => {
                                    alert(SYS_ERROR_MSG + err);
                           });
                       });
                   }else {
                       return false;
                   }
                });
            },

            // 编辑数据相关参数函数 /admin/good/updateSortItem
            // 关闭编辑框触发
            closeEditDialog(formName){
                this.$refs[formName].clearValidate();
            },

            // 点击编辑按钮触发
            handleEdit(index, row) {
                this.editObj.id = row.id;
                this.editObj.name = row.name;
                this.editFormVisible = true;
            },

            // 提交编辑框数据
            handleEditFormSubmit(formName) {
                this.$refs[formName].validate(valid => {
                    if(valid) {
                        this.$confirm('确认提交吗？', '提示', {}).then(() => {
                            this.listLoading = true;
                            let para = this.editObj;
                            this.$http.post('/api/admin/good/updateSortItem', para).then(res => {
                                let code = res.data.code;
                                if(code === ERR_OK0) {
                                    this.editFormVisible = false;
                                    alert('编辑成功！');
                                    this.listLoading = false;
                                    this.getAllSorts();
                                }else {
                                    let msg = res.msg;
                                    alert(msg);
                                }
                            }, err => {
                                alert(SYS_ERROR_MSG + err);
                            });
                        });
                    }else {
                         return false;
                    }
                });
            },

            // 删除表格数据
            handleDel(index, row) {
                this.$confirm('确认删除吗？', '提示', {}).then(() => {
                    this.listLoading = true;
                    let para = {
                        id: row.id
                    };
                    this.$http.post('/api/admin/good/deleteSortItem', para).then(res => {
                        let code = res.data.code;
                        if(code === ERR_OK0) {
                            alert('删除成功！');
                            this.listLoading = false;
                            this.getAllSorts();
                        }else {
                            let msg = res.msg;
                            alert(msg);
                        }
                    }, err => {

                    });
                });
            }
        },

        mounted() {
            this.getAllSorts();
        }
    };
</script>

<style lang="less">

</style>