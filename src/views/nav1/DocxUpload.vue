<template>
	<section>
		<!--工具条-->
		<div class="content">
			<h3 >请上传简道云的文件，尽量不要重复</h3>
			<el-upload
					class="upload-demo"
					ref="upload"
					action=""
					with-credentials
					multiple
					:on-preview="handlePreview"
					:on-remove="handleRemove"
					:file-list="fileList"
					:on-error = "onError"
					:before-upload = "beforeUpload"
					:http-request="upload"
					:auto-upload="false">
				<el-button slot="trigger" size="small" type="primary">选取文件</el-button>
				<el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">解析文件</el-button>
				<el-button style="margin-left: 10px;" size="small" type="success" @click="testExcel">临时测试文件</el-button>
			</el-upload>

		</div>

	</section>
</template>

<script>
    import util from '../../common/js/util'
    import { requestApi, Enum, msgUtils } from '../../api/api';

    export default {
        data() {
            return {
                fileList: [],
				uploadTo: requestApi.config.uploadExcels()
            };
        },
        methods: {
            submitUpload() {
                this.$refs.upload.submit();
            },
            upload(item) {
                let formData = new FormData();
                formData.append('file', item.file);
                this.$http.post(requestApi.config.uploadExcels(), formData, {
                    credentials: true,
                    crossOrigin:true,
                    // 设置请求头
                    headers: {
                        'Content-Type': 'multipart/form-data;boundary=###############', //请求头，一定要设置边界
                    }
                }).then(function () {
                    console.log('回调成功');
                    // 请求成功回调
                }, function () {
                    console.log('回调失败');
                    // 请求失败回调
                });
			},
            testExcel() {
                // 该方法是在测试阶段使用
                requestApi.config.testExcel(this).then(res => {
                    res = res.body;
                    if(res.code === Enum.SUCCESS.code){
                        alert("测试没问题");
                    }else{
                        alert(res.msg);
                    }
                }, err => {
                    alert(Enum.SYSTEM_ERROR.msg);
                });

            },
            onError(err, file, fileList) {
                // 上传失败
                msgUtils.error(this, Enum.UPLOAD_ERR);
			},
            beforeUpload(file){
                // 上传前对文件的大小的判断
				const extension = file.name.split('.')[1] === 'xls'
				const extension2 = file.name.split('.')[1] === 'xlsx'
				const isLt2M = file.size / 1024 / 1024 < 100
				if (!extension && !extension2 ) {
					msgUtils.error(this, Enum.UPLOAD_FORMAT_ERR);
				}
				if (!isLt2M) {
                    msgUtils.error(this, Enum.UPLOAD_SIZE_ERR);
				}
				return extension || extension2  && isLt2M
			},
            handleRemove(file, fileList) {
                console.log(file, fileList);
            },
            handlePreview(file) {

                this.fileList.push({name: '随便', url:''});
                console.log(file);
            }
        }
    }
</script>

<style scoped>
	.content{
		margin-top: 50px;
		margin-left: 20px;
		font-family: Helvetica Neue,Helvetica,PingFang SC,Hiragino Sans GB,Microsoft YaHei,SimSun,sans-serif;
	}
	.content h3 {
		font-size: 22px;
		font-weight: 400;
		margin: 0 0 30px;
		color: #1f2d3d;

	}
	.content ul {
		margin-bottom: 50px;
		padding-left: 0;
	}
	.content li {
		font-size: 14px;
		margin-bottom: 10px;
		color: #99a9bf;
		list-style: none;
	}
	.content li strong {
		color: #5e6d82;
		font-weight: 400;
	}
	.content li:before {
		content: "";
		display: inline-block;
		width: 4px;
		height: 4px;
		border-radius: 50%;
		vertical-align: middle;
		background-color: #5e6d82;
		margin-right: 5px;
	}
</style>