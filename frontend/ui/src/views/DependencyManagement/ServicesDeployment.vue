<template>
    <div class="ServicesDeployment">

        <div id="left">
            <DependencyGraph  ref="graph"/>
        </div>

        <div id="right">
            <h2>服务部署</h2>
            <p>服务端检索到已存在 {{cascaders.length}} 个服务</p>
            <p>当前已选择 {{selectedServices.length}} 个服务</p>


            <div v-for="_,index in cascaders" class="service-cascaders">
                服务 {{index+1}}
                <a-cascader class="service-cascader" :options="cascaders[index]" @change="onChange" :placeholder="cascaders[index][0].label" :allowClear="false" />
            </div>

            <div style="text-align: center; margin: 30px">
                <span v-if="selectedServices.length !== 0"><a-button type="primary" style="width: 100px; margin: 10px" @click="submitSelectedService">提交方案</a-button></span>
                <span v-else><a-button type="primary" style="width: 100px; margin: 10px" @click="submitSelectedService" disabled>提交方案</a-button></span>
                <a-button style="width: 60px; margin: 10px" @click="resetSelect">重置</a-button>
                <a-button type="danger" style="width: 100px; margin: 10px" @click="clearDeploy">清除部署</a-button>
            </div>
        </div>


        <!-- 服务部署序列确认对话框 -->
        <a-modal
                title="服务部署序列确认"
                v-model="showDeploySequencesConfirm"
                @ok="handleDeploySequencesConfirm"
                okText="确认部署"
                cancelText="取消部署"
        >

            <h2>部署顺序</h2>
            <a-divider type="horizontal"></a-divider>
            <a-timeline>
                <div v-if="deploy_list[0].serviceName !== undefined" v-for="deploy in deploy_list">
                    <a-timeline-item>{{deploy.serviceName}} [{{deploy.version}}]</a-timeline-item>
                </div>
                <div v-else>
                    <a-spin />
                </div>
            </a-timeline>

        </a-modal>

    </div>


</template>


<script>
    import axios from 'axios';
    import DependencyGraph from './DependencyGraph.vue';

    export default {
        name: "ServicesDeployment",
        components: {
            DependencyGraph,
        },
        data() {
            return {
                cascaders: [],
                selectedServices: [], // 选择的服务及版本列表
                showDeploySequencesConfirm: false,
                deploy_list: [],
            }
        },
        methods: {
            fetchData() {
                // 获取数据
                const URL_GET_CASCADERS = 'http://localhost:8888/cascaders';

                axios.get(URL_GET_CASCADERS).then(response => {
                    this.cascaders = response.data;
                    // console.log(response)
                }).catch((err) => {
                    console.log("无法绘制 nodes 数据: " + err)
                });
            },

            resetSelect() {
                this.selectedServices = [];
                this.cascaders = [];
                this.fetchData();
            },

            clearDeploy() {
                this.selectedServices = [];

                const URL_POST_SELECTED_SERVICE = 'http://localhost:8888/selectedService';

                axios.post(URL_POST_SELECTED_SERVICE, this.selectedServices).then(response => {
                    console.log(response)
                    this.$refs.graph.fetchDataAndDrawGraph();
                    this.cascaders = [];
                    this.fetchData();
                }).catch((err) => {
                    console.log("无法上传 selectedServices 数据: " + err)
                });

            },

            submitSelectedService() {
                this.showDeploySequencesConfirm = true;
                const URL_POST_SELECTED_SERVICE = 'http://localhost:8888/selectedService';

                axios.post(URL_POST_SELECTED_SERVICE, this.selectedServices).then(response => {
                    console.log(response)
                    this.fetchDeploySequences(); // 上传后获取部署顺序

                }).catch((err) => {
                    console.log("无法上传 selectedServices 数据: " + err)
                });

            },

            fetchDeploySequences() {
                const URL_GET_DEPLOY_LIST = 'http://localhost:8888/deployList';
                axios.get(URL_GET_DEPLOY_LIST).then(response => {
                    this.deploy_list = response.data;
                    console.log(this.deploy_list)
                }).catch((err) => {
                    console.log("无法获取 deployList 数据: " + err)
                    this.$message.error("无法获取 deployList 数据: " + err);
                });
            },

            handleDeploySequencesConfirm() {
                this.$refs.graph.fetchDataAndDrawGraph(); // 选择服务改变后 DependencyGraph 组件依赖图数据重新获取
                this.fetchData();

                this.showDeploySequencesConfirm = false;
                this.deploy_list = [];
            },

            onChange(value) {
                if (value[0] === undefined) {
                    return;
                }

                // 级联选择器选定时，列表中添加相应的选择信息，并且去重
                let flag = 0; // 重复标记
                for (var i = 0; i < this.selectedServices.length; i++) {
                    if (value[0] === this.selectedServices[i].serviceName) {
                        flag = 1;
                        this.selectedServices[i].version = value[1];
                        // console.log("同名服务，选择了新版本");
                        break;
                    }
                }

                if (flag !== 1) {
                    this.selectedServices.push({serviceName: value[0],version: value[1]});
                    flag = 0;
                }
                // console.log(this.selectedServices);

            },

        },
        mounted() {
            this.fetchData();
        }
    }
</script>

<style>
    .ServicesDeployment {
        height: 100%;
        overflow: hidden;

    }
    .service-cascaders {
        text-align: center;
    }

    .service-cascader {

        margin: 10px;
        width: 300px;
    }

    #left {
        float: left;
        width: 60%;
    }

    #right {
        float: right;
    }

</style>