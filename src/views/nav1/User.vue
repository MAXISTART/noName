<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters" ref="filters">
				<el-form-item>
					<el-input v-model="filters.name" placeholder="名称"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getUsers">查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!-- 操作那边要改变一下，要有添加删除权限等功能，显示也要显示权限 -->
		<!--列表-->
		<el-table :data="users" border highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
			<el-table-column type="selection" width="55">
			</el-table-column>
			<el-table-column type="index" width="60" align="center">
			</el-table-column>
			<el-table-column label="个人信息" width="1060" align="center">
				<el-table-column prop="name" label="名称" width="140" column-key="id" sortable align="center">
				</el-table-column>
				<el-table-column prop="departmentName" label="部门名称" width="140" sortable align="center">
				</el-table-column>
				<el-table-column prop="description" label="个人描述" min-width="200" sortable align="center">
				</el-table-column>
				<el-table-column prop="phoneNumber" label="联系方式" min-width="180" sortable align="center">
				</el-table-column>
				<el-table-column prop="mailbox" label="邮箱" min-width="180" sortable align="center">
				</el-table-column>
				<el-table-column prop="qqNumber" label="QQ" min-width="180" sortable align="center">
				</el-table-column>
				<el-table-column prop="weixin" label="微信" min-width="180" sortable align="center">
				</el-table-column>


				<el-table-column label="用户权限" min-width="280" align="center">
					<template slot-scope="scope" >
						<el-row :gutter="85">
							<el-col :span="6" v-for="(permission, index) in scope.row.permissions">
							<el-popover trigger="hover" placement="top">
								<p>名称: {{ permission.name }}</p>
								<p>描述: {{ permission.description }}</p>
								<div slot="reference" >
									<el-tag type="info" size="medium"
											class="infoTag"
											closable
											:disable-transitions="false"
											@close="handleClose(scope.row.id, permission)">
										{{ permission.name }}
									</el-tag>
								</div>
							</el-popover>
							</el-col>
						</el-row>
					</template>
				</el-table-column>
			</el-table-column>

			<el-table-column label="系统属性" width="320" align="center">
				<el-table-column prop="creatorName" label="创建者" width="120" sortable align="center">
				</el-table-column>
				<el-table-column type="date" prop="createTime" label="创建时间" min-width="180" sortable align="center">
				</el-table-column>
				<el-table-column prop="updatorName" label="最后更新人" min-width="180" sortable align="center">
				</el-table-column>
				<el-table-column type="date" prop="lastmodifiedTime" label="最后更新时间" min-width="180" sortable align="center">
				</el-table-column>
			</el-table-column>

			<el-table-column label="操作" width="250" fixed="right">
				<template scope="scope">
					<el-button type="warning" size="small" @click="handleAddPermiss(scope.$index, scope.row)">添加权限</el-button>
					<el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
					<el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar">
			<el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0">批量删除</el-button>
			<el-pagination
					class="pagenation"
					@size-change="handleSizeChange"
					@current-change="handleCurrentChange"
					:current-page="pagination.currentPage"
					:page-sizes="pagination.pageSizes"
					:page-size="pagination.pageSize"
					layout="total, sizes, prev, pager, next, jumper"
					:total="pagination.total">
			</el-pagination>
		</el-col>


		<!--权限添加界面-->
		<el-dialog title="编辑" :visible.sync="pemissAddFormVisible" :close-on-click-modal="false">
			<el-form :model="pemissAddForm" label-width="120px"  :rules="permissAddFormRules" ref="pemissAddForm">
				<el-form-item label="权限" prop="sort">
					<el-select v-model="pemissAddForm.permissionName" placeholder="请选择权限">
						<!--:disabled="handleDisabled(item, scope.row.permissions)"-->
						<el-option
								v-for="item in allPermissions"
								:label="item.name"
								:value="item.name">
						</el-option>
					</el-select>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="pemissAddFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="addPermissSubmit" :loading="pemissAddLoading">提交</el-button>
			</div>
		</el-dialog>


		<!--编辑界面-->
		<el-dialog title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false">
			<el-form :model="editForm" label-width="80px" :rules="editFormRules" ref="editForm">
				<el-form-item label="名称" prop="name">
					<el-input v-model="editForm.name" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="部门" prop="departmentName">
					<el-select v-model="editForm.departmentName" placeholder="请选择">
						<el-option
								v-for="item in departments"
								:label="item.name"
								:value="item.name">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="职能描述" prop="description">
					<el-input v-model="editForm.description" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="电话号码" prop="phoneNumber">
					<el-input v-model="editForm.phoneNumber" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="邮箱" prop="mailbox" >
					<el-input v-model="editForm.mailbox" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="QQ" prop="price" >
					<el-input v-model.number="editForm.qqNumber" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="微信" prop="price" >
					<el-input v-model="editForm.weixin" auto-complete="off"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="editFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="editSubmit" :loading="editLoading">提交</el-button>
			</div>
		</el-dialog>

		<!--新增界面-->
		<el-dialog title="新增" :visible.sync="addFormVisible" :close-on-click-modal="false">
			<el-form :model="addForm" label-width="80px" :rules="editFormRules" ref="addForm">
				<el-form-item label="名称" prop="name">
					<el-input v-model="addForm.name" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="部门" prop="departmentName">
					<el-select v-model="addForm.departmentName" placeholder="请选择">
						<el-option
								v-for="item in departments"
								:label="item.name"
								:value="item.name">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="职能描述" prop="description">
					<el-input v-model="addForm.description" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="电话号码" prop="phoneNumber">
					<el-input v-model="addForm.phoneNumber" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="邮箱" prop="mailbox" >
					<el-input v-model="addForm.mailbox" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="QQ" prop="price" >
					<el-input v-model.number="addForm.qqNumber" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="微信" prop="price" >
					<el-input v-model="addForm.weixin" auto-complete="off"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="addFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="addSubmit" :loading="addLoading">提交</el-button>
			</div>
		</el-dialog>
	</section>
</template>

<script>
	import util from '../../common/js/util'
    import { requestApi, Enum, msgUtils, permissionMap } from '../../api/api';

	export default {
		data() {
			return {
				filters: {
					name: ''
				},
                users: [],
                allPermissions: [], //获取所有的权限信息,
                pagination: {
                    total: 0,
                    currentPage: 1,
                    pageSizes: [10, 20, 50, 100],
                    pageSize: 10
                },
				listLoading: false,
				sels: [],//列表选中列
				departments: [], // 所有部门的信息
                pemissAddFormVisible: false, //权限添加界面是否显示
                pemissAddLoading: false,
                pemissAddForm: {
				    'permissionName': '',
					'permisssionId': '',
					'userId': ''
				},
				permissAddFormRules: {
                    permissionName: [
                        { required: true, message: '请选择权限', trigger: 'change' }
                    ],
				},
				editFormVisible: false,//编辑界面是否显示
				editLoading: false,
				editFormRules: {
                    name: [
                        {required: true, message: '请输入用户名称'}
                    ],
                    description: [
                        {required: true, message: '请输入用户描述'}
                    ],
                    phoneNumber: [
                        {required: true, message: '请输入用户手机号码'},
                    ],
                    qqNumber: [
                        { type: 'integer', message: '请输入正确的格式'}
                    ],
                    mailbox: [
                        { type: 'email', message: '请输入正确的格式'}
                    ],

				},
				//编辑界面数据
            	//name sort spec unit number price
				editForm: {
                    "departmentName":'',
                    "departmentId":'',
                    "description":'',
                    "id":'',
                    "mailbox":'',
                    "name":'',
                    "phoneNumber":'',
                    "qqNumber":'',
                    "weixin":''
				},

				addFormVisible: false,//新增界面是否显示
				addLoading: false,
				//新增界面数据
                addForm: {
                    "departmentName":'',
                    "departmentId":'',
                    "description":'',
                    "id":'',
                    "mailbox":'',
                    "name":'',
                    "phoneNumber":'',
                    "qqNumber":'',
                    "weixin":''
                }

			}
		},
		methods: {
		    // 添加权限
            //显示添加权限的界面
            handleAddPermiss: function (index, row) {
                this.pemissAddFormVisible = true;
                this.pemissAddForm = {
                    'permissionName': '',
                    'permisssionId': '',
                    'userId': row.id
                };
            },
            addPermissSubmit: function () {
                this.pemissAddLoading = true;
                this.$refs.pemissAddForm.validate((valid) => {
					if(valid){
						let userToPermiss = [];
						let para = Object.assign({}, this.pemissAddForm);
						// 先根据 permisssionName 找出 permisssionId
						this.allPermissions.forEach(permission => {
							if(permission.name ===  para.permissionName){
								para.permissionId = permission.id;
							}
						});
						userToPermiss.push({permissionId: para.permissionId, userId: para.userId});
						requestApi.user.addPermissions(this, userToPermiss).then(res => {
							res = res.body;
							this.pemissAddLoading = false;
							if(res.code === Enum.SUCCESS.code){
								this.pemissAddFormVisible = false;
								this.getUsers();
							}else{
								this.pemissAddFormVisible = false;
								msgUtils.warning(this, res.msg);
							}
						}, error => {
							this.pemissAddLoading = false;
							this.pemissAddFormVisible = false;
							msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
						});
					}
				});
			},
            // 移除权限
            handleClose: function (userId, permiss) {
                this.$confirm('确认移除此用户的 '+ permiss.name +' 权限吗?', '提示', {
                    type: 'warning'
                }).then(() => {
                    this.listLoading = true;
                    let userToPermiss = [];
                    userToPermiss.push({permissionId: permiss.id, userId: userId});
                    requestApi.user.removePermissions(this, userToPermiss).then(res => {
                        res = res.body;
                        this.listLoading = false;
                        if(res.code === Enum.SUCCESS.code){
							this.getUsers();
                        }else{
                            msgUtils.warning(this, res.msg);
                        }
					}, error => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
					});
				});
                return null;
            },
			// 检测选项中的权限中有几个是已经在那一行里面了，在那一行里面的返回false
            handleDisabled: function (item, userPermissions) {
                let disabled = false;
                userPermissions.forEach(userPermission => {
                    if(item.id === userPermission.id ){
                        disabled = true;
					}
				});
				return disabled;
			},
			handleCurrentChange(val) {
                this.pagination.currentPage = val;
				this.getUsers();
			},
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getUsers();
			},
			//获取商品列表
			getUsers() {
                this.listLoading = true;
			    // 获取所有部门信息
                requestApi.department.findAll(this, 0, 1000).then(res => {
                    res = res.body;
					if(res.code === Enum.SUCCESS.code){
						this.departments = res.data.content;
					}else{
						msgUtils.warning(this, res.msg);
					}
				},err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
				});
                // 获取所有的权限信息
                requestApi.permission.findAll(this, 0, 1000).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.allPermissions = res.data.content;
                    }
                }, err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });

                let formData  = new FormData();
                formData .append('page', this.pagination.currentPage);
                formData .append('size', this.pagination.pageSize);
                formData .append('name', this.filters.name);
                requestApi.user.findByParam(this, formData).then(res => {
                    this.listLoading = false;
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.pagination.total = res.data.total;
                        this.users = res.data.data;
                    }
                }, err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
			},
			//删除
			handleDel: function (index, row) {
				this.$confirm('确认删除该记录吗?', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					//NProgress.start();
					let para = { id: row.id };
					requestApi.user.delete(this, para).then(res => {
						//NProgress.done();
                        this.listLoading = false;
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code) {
                            this.getUsers();
                        }else {
                            msgUtils.warning(this, res.msg);
                        }
					}, error => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
					});
				})
			},
			//显示编辑界面
			handleEdit: function (index, row) {
				this.editFormVisible = true;
				this.editForm = Object.assign({}, row);
			},
			//显示新增界面
			handleAdd: function () {
				this.addFormVisible = true;
				this.addForm = {
                    "departmentName":'',
                    "departmentId":'',
                    "description":'',
                    "id":'',
                    "mailbox":'',
                    "name":'',
                    "phoneNumber":'',
                    "qqNumber":'',
                    "weixin":''
                };
			},
			//编辑
			editSubmit: function () {
				this.$refs.editForm.validate((valid) => {
					if (valid) {
						this.$confirm('确认提交吗？', '提示', {}).then(() => {
							this.editLoading = true;
							//NProgress.start();
							let para = Object.assign({}, this.editForm);
                            // 先根据 departmentName 找出 departmentId
                            this.departments.forEach(department => {
                                if(department.name ===  para.departmentName){
                                    para.departmentId = department.id;
                                }
                            });
                            requestApi.user.update(this,para).then(res => {
                                this.editLoading = false;
                                res = res.body;
                                if(res.code === Enum.SUCCESS.code) {
                                    this.getUsers();
                                    this.editFormVisible = false;
                                }else {
                                    msgUtils.warning(this, res.msg);
                                }
                            },error => {
                                this.editLoading = false;
                                msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                            });
						});
					}
				});
			},
			//新增
			addSubmit: function () {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						this.$confirm('确认提交吗？', '提示', {}).then(() => {
							this.addLoading = true;
							//NProgress.start();
							let para = Object.assign({}, this.addForm);
                            // 先根据 departmentName 找出 departmentId
                            this.departments.forEach(department => {
                                if(department.name ===  para.departmentName){
                                    para.departmentId = department.id;
                                }
                            });
							requestApi.user.add(this, para).then(res => {
								this.addLoading = false;
								//NProgress.done();
                                res = res.body;
                                if(res.code === Enum.SUCCESS.code) {
                                    this.$refs['addForm'].resetFields();
                                    this.getUsers();
                                    this.addFormVisible = false;
                                }else {
                                    msgUtils.warning(this, res.msg);
                                }
							}, error => {
                                this.addLoading = false;
                                this.addFormVisible = false;
                                msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
							});
						});
					}
				});
			},
			selsChange: function (sels) {
				this.sels = sels;
			},
			//批量删除
			batchRemove: function () {
			    var els = [];
			    this.sels.forEach(item => {
			        var el = {};
			        el.id = item.id;
                    els.push(el);
				});
				this.$confirm('确认删除选中记录吗？', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					//NProgress.start();
					requestApi.user.deletes(this, els).then(res => {
                        this.listLoading = false;
                        //NProgress.done();
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code) {
                            this.getUsers();
                        }else {
                            msgUtils.warning(this, res.msg);
                        }
                    }, error => {
                        this.listLoading = false;
                        msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                    });
				})
			}
		},
		mounted() {
			this.getUsers();
		}
	}

</script>

<style scoped>
	.pagenation {
		float: right;
	}
	.infoTag {
		background-color: rgba(64,158,255,.1);
		display: inline-block;
		padding: 0 10px;
		height: 32px;
		line-height: 30px;
		font-size: 12px;
		color: #409eff;
		border-radius: 4px;
		box-sizing: border-box;
		border: 1px solid rgba(64,158,255,.2);
		white-space: nowrap;
	}
</style>