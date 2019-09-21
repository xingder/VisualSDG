<template>
    <div class="service-list">

        <a-button type="primary" style="margin: 20px" @click="showDrawer">上传依赖配置清单</a-button>

        <a-table :columns="columns" :dataSource="data">
            <a slot="name" slot-scope="text" href="javascript:;">{{text}}</a>
            <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
            <span slot="endpoints" slot-scope="endpoints"><a-tag v-for="endpoint in endpoints" color="blue">{{endpoint}}</a-tag></span>
            <span slot="dependencies" slot-scope="dependencies"><a-tag v-for="dependency in dependencies" color="red">{{dependency}}</a-tag></span>
            <span slot="options" slot-scope="text, record">
<!--              <a href="javascript:;">Invite 一 {{record.name}}</a>-->
              <a href="javascript:;">Option</a>
              <a-divider type="vertical" />
            </span>
        </a-table>

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
        slots: { title: 'customTitle' },
        sortOrder: 'descend',
    }, {
        title: 'Version',
        dataIndex: 'version',
        scopedSlots: { customRender: 'version' },
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
        name: "DataMaintanance",
        data() {
            return {
                data: [],
                drawer_visible: false,
                columns,
            }
        },
        methods: {
            fetchData() {
                const URL_GET_SERVICE = 'http://localhost:8888/service';

                axios.get(URL_GET_SERVICE).then(response => {
                    this.data = response.data;
                    // console.log(response)
                }).catch((err)=>{
                    console.log("无法绘制 nodes 数据: " + err)
                });
            },
            showDrawer() {
                this.drawer_visible = true
            },
            onClose() {
                this.drawer_visible = false
            },
            handleChange(info) {
                const status = info.file.status;
                if (status !== 'uploading') {
                    console.log(info.file, info.fileList);
                }
                if (status === 'done') {
                    this.$message.success(`${info.file.name} 上传成功`);
                } else if (status === 'error') {
                    this.$message.error(`${info.file.name} 上传失败`);
                }
            },
        },
        mounted() {
            this.fetchData();
        }
    }
</script>


<style scoped>

</style>

