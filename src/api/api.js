import axios from 'axios';

let base = '';

export const requestLogin = params => { return axios.post(`${base}/login`, params).then(res => res.data); };

export const getUserList = params => { return axios.get(`${base}/user/list`, { params: params }); };

export const getUserListPage = params => { return axios.get(`${base}/user/listpage`, { params: params }); };

export const removeUser = params => { return axios.get(`${base}/user/remove`, { params: params }); };

export const batchRemoveUser = params => { return axios.get(`${base}/user/batchremove`, { params: params }); };

export const editUser = params => { return axios.get(`${base}/user/edit`, { params: params }); };

export const addNewThing = params => { return axios.post(`/api/admin/good/addGoodItem`, { params: params }); };

export const getWarehouseManager = params => { return axios.get(`${base}/warehouseManager/list`, { params: params }); };

// 获取仓库有的东西
export const getWarehouseThings = params => { return axios.get(`${base}/warehouse/things`, { params: params }); };

// 获取入库记录
export const getEnterWarehouseRecord = params => { return axios.get(`${base}/warehouse/enterWarehouseRecord`, { params: params }); };

// 获取入库记录(分页)
export const getEnterWarehouseRecordListpage = params => { return axios.get(`/api/schoolStore/admin/storeOperation/findAllStoreOperations`, { params: params }); };