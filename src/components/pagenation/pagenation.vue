<template>
    <div class="block">
        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pagenation.currentPage"
                :page-sizes="pagenation.pageSizes"
                :page-size="pagenation.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="pagenation.total">
        </el-pagination>
    </div>
</template>

<script type="text/ecmascript-6">
    export default {
        props: {
            pagenation: Object
        },
        methods: {
            handleSizeChange(val) {
                console.log(`每页 ${val} 条`);
            },
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
            }
        },
        data() {
            return {
                currentPage4: 5,
                currentPage2: 5,
                currentPage3: 5,
                currentPage1: 1
            };
        }
    }
</script>

<style lang="less">
    .block {
        float: right;
    }
</style>