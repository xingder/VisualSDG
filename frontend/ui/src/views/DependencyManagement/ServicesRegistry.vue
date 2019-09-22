<template>
    <div class="service-list">

        <a-button type="primary" style="margin: 20px" @click="showDrawer">上传依赖配置清单</a-button>
        <a-button type="danger" style="margin: 20px" @click="showDeleteAllConfirm">删除所有服务</a-button>

        <a-table :columns="columns" :dataSource="data">
            <a slot="name" slot-scope="text" href="javascript:;">{{text}}</a>
            <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
            <span slot="versions" slot-scope="versions"><a-tag v-for="version in versions" color="blue">{{version}}</a-tag></span>
            <span slot="endpoints" slot-scope="endpoints"><a-tag v-for="endpoint in endpoints" color="blue">{{endpoint}}</a-tag></span>
            <span slot="dependencies" slot-scope="dependencies"><a-tag v-for="dependency in dependencies" color="red">{{dependency}}</a-tag></span>
            <span slot="options" slot-scope="text, record">
<!--              <a href="javascript:;">Invite 一 {{record.name}}</a>-->
                <a-button type="danger" @click="showDeleteConfirm(record.service, record.versions)">Delete</a-button>
<!--              <a-divider type="vertical" />-->
            </span>
        </a-table>

        <!-- 列表服务删除确认-->
        <a-modal
                title="删除服务"
                :visible="delete_confirm_visible"
                okText="确认删除"
                okType="danger"
                @ok="handleConfirmDelete"
                :confirmLoading="confirmDeleteLoading"
                @cancel="handleCancelDelete"
                cancelText="取消"
        >
            <p>您确定要删除 {{deleting_service.serviceName}} 的 {{deleting_service.versions || "所有"}} 版本？此操作将有可能破坏已经生成的服务依赖关系图！</p>
        </a-modal>

        <!-- 全部删除确认-->
        <a-modal
                title="删除全部服务"
                :visible="delete_all_confirm_visible"
                okText="确认删除全部服务"
                okType="danger"
                @ok="handleConfirmDeleteAll"
                cancelText="取消"
        >
            <p>您确定要删除全部服务</p>
        </a-modal>

        <a-drawer
                title="上传依赖配置清单文件"
                placement="right"
                :width="500"
                :closable="false"
                @close="onClose"
                :visible="drawer_visible"
        >
            <a-upload-dragger name="file" :multiple="true" action="http://localhost:8888/service" @change="handleChange">
                <p class="ant-upload-drag-icon">
                    <a-icon type="inbox" />
                </p>
                <p class="ant-upload-text">点击此处或拖拽依赖配置文件到此处上传</p>
                <p class="ant-upload-hint">支持单个文件、多个文件上传</p>
            </a-upload-dragger>
        </a-drawer>
    </div>
</template>
<script>
    import axios from 'axios';

    const columns = [{
        dataIndex: 'service',
        key: 'service',
        slots: { title: 'customTitle' },
    }, {
        title: 'Versions',
        dataIndex: 'versions',
        key: 'versions',
        scopedSlots: { customRender: 'versions' },
    }, {
        title: 'Endpoints',
        dataIndex: 'endpoints',
        scopedSlots: { customRender: 'endpoints' },
    }, {
        title: 'Dependencies',
        dataIndex: 'dependencies',
        scopedSlots: { customRender: 'dependencies' },
    }, {
        title: 'Options',
        scopedSlots: { customRender: 'options' },
    }];

    export default {
        name: "ServicesRegistry",
        data() {
            return {
                data: [],
                drawer_visible: false,
                delete_confirm_visible: false,
                delete_all_confirm_visible: false,
                confirmDeleteLoading: false,
                deleting_service: {
                    serviceName: '',
                    versions: [],
                },
                columns,
            }
        },
        methods: {
            fetchData() {
                const URL_GET_SERVICE = 'http://localhost:8888/serviceTable';

                axios.get(URL_GET_SERVICE).then(response => {
                    this.data = response.data;
                    // console.log(response)
                }).catch((err)=>{
                    console.log("无法绘制 nodes 数据: " + err)
                });
            },

            // 显示服务上传 Drawer
            showDrawer() {
                this.drawer_visible = true
            },

            // 关闭服务上传 Drawer
            onClose() {
                this.drawer_visible = false
            },

            // 服务依赖配置清单上传监听
            handleChange(info) {
                const status = info.file.status;
                if (status !== 'uploading') {
                    console.log(info.file, info.fileList);
                }
                if (status === 'done') {
                    this.$message.success(`服务依赖配置清单上传成功`);
                    this.fetchData();
                } else if (status === 'error') {
                    this.$message.error(`${info.file.name} 上传失败`);
                }
            },

            // 显示删除确认框
            showDeleteConfirm(serviceName, versions) {
                console.log("deleting: " + serviceName + " " + versions);
                this.deleting_service.serviceName = serviceName;
                this.deleting_service.versions = versions;

                console.log("deleting: " + serviceName + " " + versions);
                this.delete_confirm_visible = true;
            },

            // 显示删除全部确认
            showDeleteAllConfirm() {
                this.delete_all_confirm_visible = true;
            },

            // 确认删除服务
            handleConfirmDelete() {

                const URL_DELETE_SERVICE = 'http://localhost:8888/service';

                for (var i = 0; i < this.deleting_service.versions.length; i++) {
                    axios.delete(URL_DELETE_SERVICE, {params: {serviceName: this.deleting_service.serviceName,
                            versions: this.deleting_service.versions[i]}}).then(response => {
                        console.log(response)
                        if (response.data === true) {
                            this.$message.success('删除成功');
                            this.fetchData();
                        } else {
                            console.log("删除失败: response.data " + response.data);
                            this.$message.error("删除失败，未找到指定删除的服务");
                        }
                    }).catch((err)=>{
                        console.log("删除失败: " + err)
                        this.$message.error('删除失败' + err);
                    });
                }
                this.deleting_service.serviceName = null;
                this.deleting_service.versions = null;
                this.delete_confirm_visible = false; // 关闭
            },

            // 确认删除全部服务
            handleConfirmDeleteAll() {
                const URL_DELETE_ALL_SERVICE = 'http://localhost:8888/services';

                axios.delete(URL_DELETE_ALL_SERVICE).then(response => {
                    this.$message.success('删除成功');
                    this.fetchData();
                }).catch((err)=>{
                    console.log("删除失败: " + err)
                    this.$message.error('删除失败' + err);
                });

                this.delete_all_confirm_visible = false;
            },

            // 取消删除服务
            handleCancelDelete() {
                this.deleting_service.serviceName = null;
                this.deleting_service.versions = null;
                this.delete_confirm_visible = false; // 关闭
            },

        },

        // 挂载时钩子
        mounted() {
            this.fetchData();
        }
    }
</script>


<style scoped>

</style>

