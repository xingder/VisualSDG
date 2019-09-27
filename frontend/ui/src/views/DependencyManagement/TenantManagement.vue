<template>
    <div>
        <h2>TenantManagement</h2>
        <a-button type="primary" style="margin: 20px" @click="showCreateTenantModal">新建租户</a-button>
        <a-table :columns="columns" :dataSource="data">
            <a slot="name" slot-scope="text" href="javascript:;">{{text}}</a>
            <span slot="customTitle"><a-icon type="smile-o" /> Tenant ID</span>
            <span slot="tenantName" slot-scope="tenantName">{{tenantName}}</span>
            <span slot="options" slot-scope="text, record">
                <a-button type="danger" @click="showDeleteTenantModal(record.tenantName)">Delete</a-button>
            </span>
        </a-table>


        <!-- 创建租户对话框-->
        <a-modal
                title="创建租户确认"
                :visible="create_tenant_confirm_visible"
                okText="确认创建"
                @ok="handleConfirmCreateTenant"
                @cancel="handleCancelCreateTenant"
                cancelText="取消"
                :maskClosable="maskClosable"
        >
            <h3>确认添加租户 {{create_tenant_name}} ？</h3>
            <a-input placeholder="新租户名称(唯一)" style="width: 60%" v-model="create_tenant_name">123</a-input>
            <a-button style="margin: 20px; width: 30%" @click="checkTenantExist">检查是否已经存在</a-button>

        </a-modal>

        <!-- 删除租户对话框-->
        <a-modal
                title="租户删除确认"
                :visible="delete_tenant_confirm_visible"
                okText="确认删除租户"
                okType="danger"
                @ok="handleConfirmDeleteTenant"
                @cancel="handleCancelDeleteTenant"
                cancelText="取消"
                :maskClosable="maskClosable"
        >
            <h3>确认添加租户 {{delete_tenant_name}} ？</h3>
        </a-modal>
    </div>
</template>

<script>
    import axios from 'axios';

    const RESULT_NO_ERROR = 0;
    const RESULT_ERROR = 1;

    const columns = [{
        dataIndex: '_id',
        key: 'id',
        slots: { title: 'customTitle' },
    }, {
        title: 'Tenant Name',
        dataIndex: 'tenantName',
        key: 'tenantName',
        scopedSlots: { customRender: 'tenantName' },
    },{
        title: 'Options',
        scopedSlots: { customRender: 'options' },
    }];

    export default {
        name: "TenantManagement",
        data(){
            return{
                columns,
                data: [],
                create_tenant_name: '',
                delete_tenant_name: '',
                create_tenant_confirm_visible: false,
                delete_tenant_confirm_visible: false,
                can_create_tenant: false,
                can_delete_tenant: false,

                maskClosable: false,
            }
        },
        mounted() {
            this.fetchData();
        },
        methods:{
            fetchData() {
                const URL_GET_TENANTS = 'http://localhost:8888/tenants';

                axios.get(URL_GET_TENANTS).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_NO_ERROR) {
                        this.data = result.data;
                    }
                }).catch((err)=>{
                    this.$message.error("URL_GET_TENANTS ERROR：" + err)
                });
            },

            // 检查租户是否已经存在
            checkTenantExist() {
                const URL_GET_TENANT = 'http://localhost:8888/tenant/' + this.create_tenant_name;

                axios.get(URL_GET_TENANT).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_ERROR) {
                        // 未查找到租户
                        this.can_create_tenant = true;
                        this.$message.success("检查通过，可以创建租户 " + this.create_tenant_name)
                    } else {
                        // 存在同名租户
                        this.$message.warn("租户：" + this.create_tenant_name + " 已存在")
                    }
                }).catch((err)=>{
                    this.$message.error("URL_GET_TENANT ERROR：" + err)
                });

            },

            // 显示租户创建对话框
            showCreateTenantModal() {
                this.can_create_tenant = false;
                this.create_tenant_confirm_visible = true;
            },

            // 取消创建租户
            handleCancelCreateTenant() {
                this.create_tenant_confirm_visible = false;
                this.can_create_tenant = '';
            },

            // 确认创建租户
            handleConfirmCreateTenant() {
                const URL_POST_TENANT = 'http://localhost:8888/tenant/' + this.create_tenant_name;

                if (this.can_create_tenant === true) {

                    axios.post(URL_POST_TENANT).then(response => {
                        const result = response.data;
                        if (result.status === 0) {
                            this.$message.success(result.msg);
                            this.create_tenant_name = '';
                            this.fetchData();
                        } else {
                            this.$message.error("创建租户失败：" + result.msg)
                        }
                    }).catch((err) => {
                        this.$message.error("URL_POST_TENANT ERROR：" + err)
                    });

                    this.create_tenant_confirm_visible = false;

                } else {
                    this.$message.error("请先执行检查步骤");
                }
            },

            // 显示租户删除对话框
            showDeleteTenantModal(tenantName) {
                this.delete_tenant_confirm_visible = true;
                this.delete_tenant_name = tenantName;
            },

            // 取消删除租户
            handleCancelDeleteTenant() {
                this.delete_tenant_confirm_visible = false;
                this.delete_tenant_name = '';
            },

            // 确认删除租户
            handleConfirmDeleteTenant() {
                const URL_DELETE_TENANT = 'http://localhost:8888/tenant/' + this.delete_tenant_name;

                axios.delete(URL_DELETE_TENANT).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_NO_ERROR) {
                        // 删除成功
                        this.$message.success(result.msg)
                        this.fetchData();
                        this.delete_tenant_name = '';
                    } else {
                        // 删除失败
                        this.$message.warn(result.msg)
                    }

                    this.delete_tenant_confirm_visible = false;
                }).catch((err)=>{
                    this.$message.error("URL_DELETE_TENANT ERROR：" + err)
                });
            },
        },

    }
</script>

<style scoped>

</style>