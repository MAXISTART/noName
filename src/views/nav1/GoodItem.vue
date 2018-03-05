<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters" ref="filters">
				<el-form-item>
					<el-input v-model="filters.name" placeholder="名称"></el-input>
				</el-form-item>
				<el-form-item>
					<el-input v-model.number="filters.price_start" placeholder="请输入最小单价">
					</el-input>
				</el-form-item>
				<el-form-item>
					<el-input v-model.number="filters.price_end" placeholder="请输入最大单价">
					</el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getGoodItems">查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="handleAdd">新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>


		<!--列表-->
		<el-table :data="goodItems" border highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
			<el-table-column type="selection" width="55">
			</el-table-column>
			<el-table-column type="index" width="60" align="center">
			</el-table-column>
			<el-table-column label="物品明细" width="720" align="center">
				<el-table-column prop="name" label="名称" width="140" column-key="id" sortable align="center">
				</el-table-column>
				<el-table-column prop="sort" label="种类" width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="spec" label="规格" width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="unit" label="单位" width="100" sortable align="center">
				</el-table-column>
				<el-table-column prop="number" label="数量" min-width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="price" label="单价" min-width="120" sortable align="center">
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

			<el-table-column label="操作" width="150" fixed="right">
				<template scope="scope">
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

		<!--编辑界面-->
		<el-dialog title="编辑" :visible.sync="editFormVisible" :close-on-click-modal="false">
			<el-form :model="editForm" label-width="80px" :rules="editFormRules" ref="editForm">
				<el-form-item label="名称" prop="name">
					<el-input v-model="editForm.name" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="种类" prop="sort">
					<el-select v-model="editForm.sort" placeholder="请选择">
						<el-option
								v-for="item in sortNames"
								:label="item.name"
								:value="item.name">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="规格" prop="name">
					<el-input v-model="editForm.spec" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="单位" prop="unit">
					<el-input v-model="editForm.unit" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="数量" prop="number" :rules="numberRules">
					<el-input v-model.number="editForm.number" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="单价" prop="price" :rules="priceRules">
					<el-input v-model.number="editForm.price" auto-complete="off"></el-input>
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
				<el-form-item label="种类" prop="sort">
					<el-select v-model="addForm.sort" placeholder="请选择">
						<el-option
								v-for="item in sortNames"
								:label="item.name"
								:value="item.name">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="规格" prop="name">
					<el-input v-model="addForm.spec" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="单位" prop="unit">
					<el-input v-model="addForm.unit" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="数量" prop="number" :rules="numberRules">
					<el-input v-model.number="addForm.number" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="单价" prop="price" :rules="priceRules">
					<el-input v-model.number="addForm.price" auto-complete="off"></el-input>
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
    import { requestApi, Enum, msgUtils } from '../../api/api';

	export default {
		data() {
			return {
				filters: {
					name: '',
					price_start: '',
					price_end: ''
				},
				goodItems: [],
                pagination: {
                    total: 0,
                    currentPage: 1,
                    pageSizes: [10, 20, 50, 100],
                    pageSize: 10
                },
				listLoading: false,
				sels: [],//列表选中列
				sortNames: [], // 所有种类的名称
				editFormVisible: false,//编辑界面是否显示
				editLoading: false,
				editFormRules: {
                    name: [
                        {required: true, message: '请输入物品名称', trigger: 'blur'}
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
				},
                numberRules: [
                    { required: true, message: '请输入数量'},
                    { type: 'integer', min:0, message: '请输入大于0的整数'}
				],
				priceRules: [
                    { required: true, message: '请输入单价'},
                    { type: 'number', min:0, message: '请输入大于0的数字'}
				],
                searchPrice: [
                    { type: 'number', min:0, message: '请输入大于0的数字'}
                ],
				//编辑界面数据
            	//name sort spec unit number price
				editForm: {
                    name: '',
                    sort: '',
                    spec: '',
                    unit: '',
                    number: 0,
                    price: 0
				},

				addFormVisible: false,//新增界面是否显示
				addLoading: false,
				//新增界面数据
                addForm: {
                    name: '',
                    sort: '',
                    spec: '',
                    unit: '',
                    number: 0,
                    price: 0
                }

			}
		},
		methods: {
			handleCurrentChange(val) {
                this.pagination.currentPage = val;
				this.getGoodItems();
			},
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getGoodItems();
			},
			//获取商品列表
			getGoodItems() {
			    // 获取所有物品种类
				requestApi.good.findAllSorts(this).then(res => {
                    res = res.body;
                    // 如果登录过期了
                    if(res.code === Enum.NOT_LOGIN.code){
                        msgUtils.warning(this, Enum.NOT_LOGIN.msg);
                        this.$router.push({ path: '/login' });
                    }else{
                        if(res.code === Enum.SUCCESS.code){
                            this.sortNames = res.data;
                        }else{
                            msgUtils.warning(this, Enum.GET_SORTS_FAIL.msg);
                        }
					}
				},err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
				});
				this.listLoading = true;
                let formData  = new FormData();
                formData .append('page', this.pagination.currentPage);
                formData .append('size', this.pagination.pageSize);
                formData .append('price_start', this.filters.price_start);
                formData .append('price_end', this.filters.price_end);
                formData .append('name', this.filters.name);
                requestApi.good.findByParam(this, formData).then(res => {
                    this.listLoading = false;
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        this.pagination.total = res.data.total;
                        this.goodItems = res.data.data;
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
					requestApi.good.delete(this, para).then(res => {
						//NProgress.done();
                        this.listLoading = false;
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code) {
                            this.getGoodItems();
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
                    name: '',
                    sort: '',
                    spec: '',
                    unit: '',
                    number: 0,
                    price: 0
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
                            requestApi.good.update(this,para).then(res => {
                                this.editLoading = false;
                                res = res.body;
                                if(res.code === Enum.SUCCESS.code) {
                                    this.getGoodItems();
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
							requestApi.good.addGoodItem(this, para).then(res => {
								this.addLoading = false;
								//NProgress.done();
                                res = res.body;
                                if(res.code === Enum.SUCCESS.code) {
                                    this.$refs['addForm'].resetFields();
                                    this.getGoodItems();
                                    this.addFormVisible = false;
                                }else {
                                    msgUtils.warning(this, res.msg);
                                }
							}, error => {
                                this.addLoading = false;
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
					requestApi.good.deletes(this, els).then(res => {
                        this.listLoading = false;
                        //NProgress.done();
                        res = res.body;
                        if(res.code === Enum.SUCCESS.code) {
                            this.getGoodItems();
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
			this.getGoodItems();
		}
	}

</script>

<style scoped>
	.pagenation {
		float: right;
	}
</style>