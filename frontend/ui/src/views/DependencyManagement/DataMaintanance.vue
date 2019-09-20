<template>
    <div class="service-list">
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
            }
        },
        mounted() {
            this.fetchData();
        }
    }
</script>


<style scoped>

</style>

