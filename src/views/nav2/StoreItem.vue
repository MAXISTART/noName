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
					<el-button type="primary" v-on:click="getStoreItems">查询</el-button>
				</el-form-item>
			</el-form>
		</el-col>


		<!--列表-->
		<el-table :data="storeItems" border highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
			<el-table-column type="selection" width="55">
			</el-table-column>
			<el-table-column type="index" width="60" align="center">
			</el-table-column>
			<el-table-column label="库存信息" width="720" align="center">
				<el-table-column prop="goodItem_name" label="名称" width="140" column-key="id" sortable align="center">
				</el-table-column>
				<el-table-column prop="goodItem_spec" label="规格" width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="lockNumber" label="当前被请求申领的数量" min-width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="number" label="当前库存数量" min-width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="goodItem_price" label="单价" min-width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="totalPrice" label="入库总价" min-width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="inputTime" label="入库时间" min-width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="goodItem_sort" label="种类" width="120" sortable align="center">
				</el-table-column>
				<el-table-column prop="goodItem_unit" label="单位" width="100" sortable align="center">
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

		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar">
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

	</section>
</template>

<script>
	import util from '../../common/js/util'
    import { requestApi, Enum, msgUtils } from '../../api/api';
    import { getNewData } from '../../api/tableRowAndColumn';

	export default {
		data() {
			return {
				filters: {
					name: '',
					price_start: '',
					price_end: ''
				},
                storeItems: [],
                pagination: {
                    total: 0,
                    currentPage: 1,
                    pageSizes: [10, 20, 50, 100],
                    pageSize: 10
                },
				listLoading: false,
				sels: []//列表选中列
			}
		},
		methods: {
            selsChange: function (sels) {
                this.sels = sels;
            },
            handleCurrentChange(val) {
                this.pagination.currentPage = val;
                this.getStoreItems();
            },
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getStoreItems();
            },
            //获取商品列表
            getStoreItems() {

                this.listLoading = true;
                let formData = new FormData();
                formData.append('page', this.pagination.currentPage);
                formData.append('size', this.pagination.pageSize);
                formData.append('price_start', this.filters.price_start);
                formData.append('price_end', this.filters.price_end);
                formData.append('name', this.filters.name);
                requestApi.store.findByParam(this, formData).then(res => {
                    this.listLoading = false;
                    res = res.body;
                    if (res.code === Enum.SUCCESS.code) {
                        this.pagination.total = res.data.total;
                        this.storeItems = getNewData('goodItem', res.data.data);
                    }
                }, err => {
                    msgUtils.error(this, Enum.SYSTEM_ERROR.msg);
                });
            }
        },
		mounted() {
			this.getStoreItems();
		}
	}

</script>

<style scoped>
	.pagenation {
		float: right;
	}
</style>