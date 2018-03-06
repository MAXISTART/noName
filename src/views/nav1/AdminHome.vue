<template>
	<section>
		<!--工具条-->
		<div class="content">
			<h3 >总体功能</h3>
			<ul >
				<li >
					<strong >采购：</strong>
					先申请采购，然后审批通过后入库。
				</li>
				<li >
					<strong >申领：</strong>
					先申请申领，然后审批通过后出库。
				</li>
				<li >
					<strong >审批功能：</strong>
					待完善（将结合钉钉）
				</li>
			</ul>
			<h3 >表单说明</h3>
			<ul >
				<li >
					<strong >基础信息表：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
					里面的表是最基础的表，先填写该表，其中的权限表默认有两个权限了，可以不理。
					</div>
				</li>
				<li >
					<strong >库存相关：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
						采购表 和 申领表 的数据都是要经过管理员 审批 的（当前你就是管理员），而且审批后还得进行 出库 或者 入库。
						<br>
						库存表 是不能直接操作的，它相当于一个统计表，统计当前各种物品的库存。
						<br>
						库存操作表 是用来直接操作 库存 的，同时也是用来记录对 库存 的操作，并且是需要 审批 的，比如你现在要销毁掉10个办公用品，可以申请 类型 为 出库 的库存操作，
						管理员 审批 通过后并且执行 出库，系统会自动帮你操作库存。
						<br>
						当前你就是管理员
					</div>
				</li>
				<li >
					<strong >统计：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
					图像显示一些统计信息，其中的 库存统计 中有两个指标，一个是 库存，一个是 库存总价，可以选择只看一个指标(单击即可)
					</div>
				</li>
			</ul>

			<h3 >使用说明</h3>
			<ul >
				<li >
					<strong >初次使用：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
						先填写 部门表，部门表的负责人可以先不用填（因为此时人员还未录入）
						<br>
						接着是填人员表，默认系统有个人员，那就是admin，是超级管理员，无需注册的。人员填写的时候需要指定他们的部门
						<br>
						人员填好后可以回到部门表里面更新部门信息，指定部门负责人（这个在之后的审批功能中会比较重要，）
						<br>
						还可以对人员添加或者删除权限，当前系统默认有两个权限，非必要时请不要删除这两个权限，一个是user，一个是admin，分别是用户权限和管理员权限
						<br>
						可以根据权限来分配哪些人当管理员，管理员才能登陆该系统。（管理员也可以拥有用户权限，不冲突）
					</div>
				</li>
				<li >
					<strong >采购：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
						在填写采购表先确认 用品表 中是否存在要 采购的物品 的信息，如果不存在，就先去 用品表 中填写。
						<strong>要使用 用品表 就必须先 添加物品种类（在 用品表 中添加）。</strong>
						<br>
						比如现在要采购一条规格为3cm的网线，用品表中已经存在该用品，那么就 新建 采购单， 然后填写时要注意，物品明细那里先填写物品名称，比如填一个"网字"，系统就会帮你匹配出'网线(3cm)'的选项，帮你自动填充其余空格（数据从用品表中来）。
						<br>
						上面的物品明细的数据虽然是来自物品表，但是因为各种原因（比如临时降价），所以当前价格可能和之前物品输入的价格不符，可以修改物品表价格，也可以直接在此处修改价格。
						<br>
						无论是管理员还是用户，提交的采购表默认都是未审核的，所以需要管理员自己审核这些采购单，无论审核结果如何，一旦审核过就不能再编辑这个采购单，审核通过后也是不会自动入库的，需要管理员进行 快速入库 的操作。
					</div>
				</li>
				<li >
					<strong >申领：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
					和采购表是类似的，不过 申领表 有个 归还数量，之后有归还功能后会开启这个属性的作用
					</div>
				</li>
				<li >
					<strong >库存操作：</strong>
					<div style="margin-left: 60px;margin-top: 10px;line-height: 30px">
						管理员新建库存操作单的时候默认都是审核通过的，无需审核，而用户的需要审核。
					</div>
				</li>
			</ul>

		</div>

	</section>
</template>

<script>
	export default {
		data() {
			return {
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