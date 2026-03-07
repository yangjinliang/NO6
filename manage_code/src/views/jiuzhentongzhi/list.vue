<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							医生账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.yishengzhanghao" placeholder="医生账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							通知状态：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.tongzhizhuangtai" placeholder="请选择通知状态" clearable>
								<el-option label="待发送" value="0"></el-option>
								<el-option label="发送中" value="1"></el-option>
								<el-option label="发送成功" value="2"></el-option>
								<el-option label="发送失败" value="3"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="add_btn" type="success" @click="addClick" v-if="btnAuth('jiuzhentongzhi','新增')">
						<i class="iconfont icon-xinzeng1"></i>
						新增
					</el-button>
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('jiuzhentongzhi','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
					<el-button class="retry_btn" type="warning" @click="retryBatchClick" v-if="btnAuth('jiuzhentongzhi','重试')">
						<i class="iconfont icon-shuaxin"></i>
						批量重试失败通知
					</el-button>
					<el-button class="stats_btn" type="info" @click="statisticsClick" v-if="btnAuth('jiuzhentongzhi','统计')">
						<i class="iconfont icon-tongji"></i>
						通知统计
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('jiuzhentongzhi','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibianhao"
					label="通知编号">
					<template #default="scope">
						{{scope.row.tongzhibianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yishengzhanghao"
					label="医生账号">
					<template #default="scope">
						{{scope.row.yishengzhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="dianhua"
					label="电话">
					<template #default="scope">
						{{scope.row.dianhua}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jiuzhenshijian"
					label="就诊时间">
					<template #default="scope">
						{{scope.row.jiuzhenshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhishijian"
					label="通知时间">
					<template #default="scope">
						{{scope.row.tongzhishijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shouji"
					label="手机">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibeizhu"
					label="通知备注">
					<template #default="scope">
						{{scope.row.tongzhibeizhu}}
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhizhuangtai"
					label="通知状态">
					<template #default="scope">
						<el-tag :type="getStatusType(scope.row.tongzhizhuangtai)">
							{{getStatusText(scope.row.tongzhizhuangtai)}}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="120"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jieshouzhuangtai"
					label="接收状态">
					<template #default="scope">
						<el-tag :type="scope.row.jieshouzhuangtai=='1'?'success':'info'">
							{{scope.row.jieshouzhuangtai=='1'?'已接收':'未接收'}}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="100"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="retryCount"
					label="重试次数">
					<template #default="scope">
						{{scope.row.retryCount || 0}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="failReason"
					label="失败原因">
					<template #default="scope">
						<el-tooltip v-if="scope.row.failReason" :content="scope.row.failReason" placement="top">
							<span class="fail-reason">{{scope.row.failReason}}</span>
						</el-tooltip>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="300" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('jiuzhentongzhi','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="edit_btn" type="primary" @click="editClick(scope.row.id)" v-if=" btnAuth('jiuzhentongzhi','修改')">
							<i class="iconfont icon-xiugai5"></i>
							修改						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('jiuzhentongzhi','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除						</el-button>
						<el-button class="retry_btn" v-if="btnAuth('jiuzhentongzhi','重试') && (scope.row.tongzhizhuangtai=='3' || scope.row.tongzhizhuangtai=='0')" 
							type="warning" @click="retryClick(scope.row.id)">
							<i class="iconfont icon-shuaxin"></i>
							重试
						</el-button>
						<el-button class="cross_btn" v-if="btnAuth('jiuzhentongzhi','签到')" type="success" @click="jiuzhenqiandaoCrossAddOrUpdateHandler(scope.row,'cross','','','','')">
							<i class="iconfont icon-dingdan3"></i>
							签到
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
                v-model:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="false"
				:style='{}'
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
		<jiuzhenqiandaoFormModel ref="jiuzhenqiandaoFormModelRef" @formModelChange="formModelChange"></jiuzhenqiandaoFormModel>
		
		<!-- 统计弹窗 -->
		<el-dialog v-model="statisticsVisible" title="通知统计信息" width="500px">
			<el-descriptions :column="1" border>
				<el-descriptions-item label="总通知数">{{statistics.totalCount}}</el-descriptions-item>
				<el-descriptions-item label="待发送">
					<el-tag type="info">{{statistics.pendingCount}}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="发送成功">
					<el-tag type="success">{{statistics.successCount}}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="发送失败">
					<el-tag type="danger">{{statistics.failedCount}}</el-tag>
				</el-descriptions-item>
				<el-descriptions-item label="已接收">
					<el-tag type="success">{{statistics.receivedCount}}</el-tag>
				</el-descriptions-item>
			</el-descriptions>
		</el-dialog>
	</div>
</template>
<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	//基础信息

	const tableName = 'jiuzhentongzhi'
	const formName = '就诊通知'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			//table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.yishengzhanghao&&searchQuery.value.yishengzhanghao!=''){
			params['yishengzhanghao'] = '%' + searchQuery.value.yishengzhanghao + '%'
		}
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.tongzhizhuangtai&&searchQuery.value.tongzhizhuangtai!=''){
			params['tongzhizhuangtai'] = searchQuery.value.tongzhizhuangtai
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//删
	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const editClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'edit')
			return
		}
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
		}
	}

	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	// 表单
	// 预览文件
	const preClick = (file) =>{
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		window.open(context?.$config.url + file)
	}
	// 下载文件
	const download = (file) => {
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		let arr = file.replace(new RegExp('file/', "g"), "")
		axios.get((location.href.split(context?.$config.name).length>1 ? location.href.split(context?.$config.name)[0] :'') + context?.$config.name + '/file/download?fileName=' + arr, {
			headers: {
				token: context?.$toolUtil.storageGet('Token')
			},
			responseType: "blob"
		}).then(({
			data
		}) => {
			const binaryData = [];
			binaryData.push(data);
			const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
				type: 'application/pdf;chartset=UTF-8'
			}))
			const a = document.createElement('a')
			a.href = objectUrl
			a.download = arr
			// a.click()
			// 下面这个写法兼容火狐
			a.dispatchEvent(new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
				view: window
			}))
			window.URL.revokeObjectURL(data)
		})
	}
	import jiuzhenqiandaoFormModel from '@/views/jiuzhenqiandao/formModel'
	const jiuzhenqiandaoFormModelRef = ref(null)
    const jiuzhenqiandaoCrossAddOrUpdateHandler = (row,type,crossOptAudit,crossOptPay,statusColumnName,tips,statusColumnValue) => {
		if(statusColumnName!=''&&!statusColumnName.startsWith("[")) {
			var obj = row
			for (var o in obj){
				if(o==statusColumnName && obj[o]==statusColumnValue){
					context?.$toolUtil.message(tips,'error')
					return;
				}
			}
		}
		nextTick(()=>{
			jiuzhenqiandaoFormModelRef.value.init(row.id,'cross','签到',row,'jiuzhentongzhi',statusColumnName,tips,statusColumnValue)
		})
    }
	
	// 获取状态标签类型
	const getStatusType = (status) => {
		switch(status) {
			case '0': return 'info'      // 待发送
			case '1': return 'warning'   // 发送中
			case '2': return 'success'   // 发送成功
			case '3': return 'danger'    // 发送失败
			default: return 'info'
		}
	}
	
	// 获取状态文本
	const getStatusText = (status) => {
		switch(status) {
			case '0': return '待发送'
			case '1': return '发送中'
			case '2': return '发送成功'
			case '3': return '发送失败'
			default: return '未知'
		}
	}
	
	// 重试单条通知
	const retryClick = (id) => {
		ElMessageBox.confirm('确定要重试发送该通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retry/${id}`,
				method: 'post'
			}).then(res => {
				if(res.data.code==0){
					ElMessage.success('重试发送成功')
					getList()
				}else{
					ElMessage.error(res.data.msg || '重试发送失败')
				}
			})
		}).catch(() => {})
	}
	
	// 批量重试
	const retryBatchClick = () => {
		ElMessageBox.confirm('确定要批量重试所有失败的通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/retryBatch`,
				method: 'post'
			}).then(res => {
				if(res.data.code==0){
					ElMessage.success(res.data.msg || '批量重试完成')
					getList()
				}else{
					ElMessage.error(res.data.msg || '批量重试失败')
				}
			})
		}).catch(() => {})
	}
	
	// 统计
	const statisticsVisible = ref(false)
	const statistics = ref({
		totalCount: 0,
		pendingCount: 0,
		successCount: 0,
		failedCount: 0,
		receivedCount: 0
	})
	const statisticsClick = () => {
		context.$http({
			url: `${tableName}/statistics`,
			method: 'get'
		}).then(res => {
			if(res.data.code==0){
				statistics.value = res.data.data
				statisticsVisible.value = true
			}
		})
	}
	
	//初始化
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>

	// 操作盒子
	.list_search_view {
		// 搜索盒子
		.search_form {
			// 子盒子
			.search_view {
				// 搜索label
				.search_label {
				}
				// 搜索item
				.search_box {
					// 输入框
					:deep(.search_inp) {
					}
				}
			}
			// 搜索按钮盒子
			.search_btn_view {
				// 搜索按钮
				.search_btn {
				}
				// 搜索按钮-悬浮
				.search_btn:hover {
				}
			}
		}
		//头部按钮盒子
		.btn_view {
			// 其他
			:deep(.el-button--default){
			}
			// 其他-悬浮
			:deep(.el-button--default:hover){
			}
			// 新增
			:deep(.el-button--success){
			}
			// 新增-悬浮
			:deep(.el-button--success:hover){
			}
			// 删除
			:deep(.el-button--danger){
			}
			// 删除-悬浮
			:deep(.el-button--danger:hover){
			}
			// 统计
			:deep(.el-button--warning){
			}
			// 统计-悬浮
			:deep(.el-button--warning:hover){
			}
		}
	}
	// 表格样式
	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
			tbody {
				tr {
					td {
						.cell {
							// 编辑
							.el-button--primary {
							}
							// 编辑-悬浮
							.el-button--primary:hover {
							}
							// 详情
							.el-button--info {
							}
							// 详情-悬浮
							.el-button--info:hover {
							}
							// 删除
							.el-button--danger {
							}
							// 删除-悬浮
							.el-button--danger:hover {
							}
							// 跨表
							.el-button--success {
							}
							// 跨表-悬浮
							.el-button--success:hover {
							}
							// 操作
							.el-button--warning {
							}
							// 操作-悬浮
							.el-button--warning:hover {
							}
						}
					}
				}
				tr:hover {
					td {
					}
				}
			}
		}
	}
	// 分页器
	.el-pagination {
		// 总页码
		:deep(.el-pagination__total) {
		}
		// 上一页
		:deep(.btn-prev) {
		}
		// 下一页
		:deep(.btn-next) {
		}
		// 上一页禁用
		:deep(.btn-prev:disabled) {
		}
		// 下一页禁用
		:deep(.btn-next:disabled) {
		}
		// 页码
		:deep(.el-pager) {
			// 数字
			.number {
			}
			// 数字悬浮
			.number:hover {
			}
			// 选中
			.number.is-active {
			}
		}
		// sizes
		:deep(.el-pagination__sizes) {
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			.el-select {
			}
		}
		// 跳页
		:deep(.el-pagination__jump) {
			// 输入框
			.el-input {
			}
		}
	}
	
	// 失败原因省略显示
	.fail-reason {
		display: inline-block;
		max-width: 150px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
</style>
