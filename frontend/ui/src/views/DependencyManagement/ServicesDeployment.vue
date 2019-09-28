<template>
    <div class="ServicesDeployment">

        <div id="left">
            <SDG graphNname="ServicesDeploymentSDG" :tenants="tenants" ref="graph" @currentTenantChanged="onCurrentTenantChanged"/>
        </div>

        <div id="right">
            <h2>服务部署</h2>
            <p>服务端检索到已存在 {{cascaders.length}} 个服务</p>
            <p>当前已选择 {{selectedServices.length}} 个服务</p>

            <!-- 下拉选择 -->
            <div v-for="cascader in cascaders" style="margin: 15px" class="service-cascaders">
                服务 {{cascader[0].label}}
                <a-divider type="vertical"></a-divider>
                <a-select  style="width: 150px;"  @dropdownVisibleChange="onDropdownVisivleChange(cascader[0].label)" @change="onChange">
                    <a-select-option v-for="child in cascader[0].children"  :value="child.value" >{{child.label}}</a-select-option>
                </a-select>
            </div>

            <a-divider type="horizontal"></a-divider>

<!--            <div>-->
<!--                部署租户选择：-->
<!--                <a-select  style="width: 150px;"  @change="onTenantSelectorChange" notFoundContent="当前无租户">-->
<!--                    <a-select-option v-for="tenant in tenants"  :value="tenant.tenantName" >{{tenant.tenantName}}</a-select-option>-->
<!--                </a-select>-->
<!--            </div>-->

            <div style="text-align: center; margin: 30px">
                <span v-if="selectedServices.length !== 0 && selecting_tenant !== ''"><a-button type="primary" style="width: 100px; margin: 10px" @click="submitSelectedService">提交方案</a-button></span>
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
    import SDG from '../../components/echarts/SDG.vue';

    const RESULT_NO_ERROR = 0;
    const RESULT_ERROR = 1;

    export default {
        name: "ServicesDeployment",
        components: {
            SDG,
        },
        data() {
            return {
                cascaders: [],
                selectedServices: [], // 选择的服务及版本列表
                showDeploySequencesConfirm: false,
                deploy_list: [],
                tenants: [],
                tenant: {
                    tenantName: '',
                    deployedServiceList: '',
                },
                selecting_service: '',
                selecting_version: '',
                selecting_tenant: '',
            };
        },

        methods: {
            // 获取数据
            fetchData() {
                const URL_GET_CASCADERS = 'http://localhost:8888/cascaders';
                const URL_GET_TENANTS = 'http://localhost:8888/tenants';

                // 获取全部租户数据
                axios.get(URL_GET_TENANTS).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_NO_ERROR) {
                        this.tenants = result.data;
                    }
                }).catch((err)=>{
                    this.$message.error("URL_GET_TENANTS ERROR：" + err)
                });

                // 获取下拉列表数据
                axios.get(URL_GET_CASCADERS).then(response => {
                    this.cascaders = response.data;
                    // console.log(response)
                }).catch((err) => {
                    console.log("无法绘制 nodes 数据: " + err)
                });
            },

            // 重置列表
            resetSelect() {
                this.selectedServices = [];
                this.cascaders = [];
                this.fetchData();
                console.log(this.selecting_tenant)
            },

            // 清除部署
            clearDeploy() {

                this.selectedServices = []; // 提交空选择列表

                this.tenant.tenantName = this.selecting_tenant;
                this.tenant.deployedServiceList = this.selectedServices;

                const URL_POST_TENANT = 'http://localhost:8888/tenant';


                axios.post(URL_POST_TENANT, this.tenant).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_NO_ERROR) {
                        this.$message.success("租户 " + this.tenant.tenantName + " 清除部署成功");

                        this.$refs.graph.currentTenant = this.tenant.tenantName;
                        this.$refs.graph.fetchDataAndDrawGraph(); // 选择服务改变后 DependencyGraph 组件依赖图数据重新获取
                        this.resetSelect();
                    }
                }).catch((err) => {
                    this.$message.error("无法清楚部署 ERR: " + err);
                });

            },

            // 打开部署确认对话框，提交已选择服务，用于生成部署序列
            submitSelectedService() {
                this.showDeploySequencesConfirm = true;

                const URL_POST_SELECTED_SERVICE = 'http://localhost:8888/selectedService';

                // 无租户模式下，直接上传 SelectedServices 临时储存
                axios.post(URL_POST_SELECTED_SERVICE, this.selectedServices).then(response => {
                    console.log(response)
                    this.fetchDeploySequences(); // 上传后获取部署顺序
                }).catch((err) => {
                    console.log("无法上传 selectedServices 数据: " + err)
                });

            },

            // 获取生成的部署序列
            fetchDeploySequences() {
                const URL_GET_DEPLOY_LIST = 'http://localhost:8888/deployList';
                axios.get(URL_GET_DEPLOY_LIST).then(response => {
                    this.deploy_list = response.data;
                    // console.log(this.deploy_list)
                }).catch((err) => {
                    console.log("无法获取 deployList 数据: " + err)
                    this.$message.error("无法获取 deployList 数据: " + err);
                });
            },

            handleDeploySequencesConfirm() {


                this.tenant.tenantName = this.selecting_tenant;
                this.tenant.deployedServiceList = this.selectedServices;

                const URL_POST_TENANT = 'http://localhost:8888/tenant';


                // // 无租户模式下，直接上传 SelectedServices
                axios.post(URL_POST_TENANT, this.tenant).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_NO_ERROR) {
                        this.fetchDeploySequences(); // 上传后获取部署顺序
                        this.$message.success(result.msg);

                        this.showDeploySequencesConfirm = false;
                        this.deploy_list = [];
                        this.$refs.graph.fetchDataAndDrawGraph(); // 选择服务改变后 DependencyGraph 组件依赖图数据重新获取
                        this.resetSelect();
                    }
                }).catch((err) => {
                    this.showDeploySequencesConfirm = false;
                    this.$message.error("无法获取 URL_POST_TENANT ERR: " + err);
                });

            },

            // 打开下拉列表回调
            onDropdownVisivleChange(selecting_service) {
                // console.log(selecting_service)
                this.selecting_service = selecting_service; // 打开下拉列表时记录当前对应的服务
            },

            // 列表选择完成的回调
            onChange(selecting_version) {
                // console.log(this.selecting_service + " " + selecting_version);

                let flag = 0; // 重复标记
                for (var i in this.selectedServices) {
                    if (this.selectedServices[i].serviceName === this.selecting_service) {
                        flag = 1;
                        break;
                    }
                }

                if (flag === 0) {
                    this.selectedServices.push({serviceName: this.selecting_service, version: selecting_version})
                }

                // console.log(this.selectedServices);

            },

            onCurrentTenantChanged(value) {
                // console.log(value);
                this.selecting_tenant = value; // 接收 SDG 子组件传的值：当前选择的租户
            }

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
        text-align: left;
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