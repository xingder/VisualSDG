<template>
    <div class="DependencyManagement">
        <div id="DMGraph">
            <DependencyGraph ref="graph"/>
        </div>

        <!-- 版本变更确认框-->
        <a-modal
                title="版本变更"
                v-model="version_change_confirm_visible"
                @ok="handleVersionChangeConfirm"
                okText="确认"
                cancelText="取消"
        >


            <div v-for="service in allServices">
                <div v-if="service.serviceName === currentSelectedService.serviceName">
                    {{service.serviceName}} - {{service.version}}
                    <span v-if="service.version !== currentSelectedService.version"><a-button style="margin:15px" @click="handleVersionChangeCheck(service.serviceName, service.version)">变更到此版本</a-button></span>
                    <span v-else style="margin-left: 20px"> 当前版本 </span>
                </div>
            </div>

        </a-modal>

        <div id="right-management-box">
            <h2>当前部署列表</h2>
            <div v-for="service in currentRuningServices">
                服务名：<span style="color: #42b983; font-weight: bolder">{{service.serviceName}}</span>  | 版本：{{service.version}} <a-button @click="showVersionChangeConfirm(service.serviceName,service.version)" style="margin: 3px"> 版本变更</a-button>
            </div>


        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import DependencyGraph from '../../components/echarts/DependencyGraph.vue';

    export default {
        name: "DependencyManagement",
        components: {
            DependencyGraph,
        },
        data() {
            return {
                currentRuningServices: [], // 当前运行的服务列表
                allServices: [], // 全部服务信息
                version_change_confirm_visible: false,
                currentSelectedService: {
                    serviceName: '',
                    version: '',
                },
                value: 1,
                radioStyle: {
                    display: 'block',
                    height: '30px',
                    lineHeight: '30px',
                },
            }
        },
        methods: {
            fetchData() {
                const URL_GET_SELECTED_SERVICES = 'http://localhost:8888/selectedService';
                const URL_GET_ALL_SERVICES = 'http://localhost:8888/service';

                axios.get(URL_GET_SELECTED_SERVICES).then(response => {
                    // console.log(response.data);
                    this.currentRuningServices = response.data;
                }).catch((err) => {
                    console.log("无法获取 selectedService 数据: " + err)
                });

                axios.get(URL_GET_ALL_SERVICES).then(response => {
                    // console.log(response.data);
                    this.allServices = response.data;
                }).catch((err) => {
                    console.log("无法获取 allService 数据: " + err)
                });
            },

            showVersionChangeConfirm(currentService, currentVersion) {
                this.currentSelectedService.serviceName = currentService;
                this.currentSelectedService.version = currentVersion;

                this.version_change_confirm_visible = true;
            },

            // 版本变更可行性检查
            handleVersionChangeCheck(serviceName, toVersion) {

                const URL_GET_SERVICE_VERSION_CHANGE_CHECK = 'http://localhost:8888/serviceVersionChangeCheck';

                axios.get(URL_GET_SERVICE_VERSION_CHANGE_CHECK, {params:{serviceName: serviceName,toVersion:toVersion}}).then(response => {
                    let ableToChange = response.data.ableToChange;
                    let reason = response.data.reason;

                    if (ableToChange === "yes") {
                        this.$message.success("经依赖性检查，可以进行版本变更");
                    } else {
                        this.$message.error("经依赖性检查，无法进行版本变更，原因：" + reason);
                    }
                }).catch((err) => {
                    console.log("无法获取 serviceVersionChangeCheck 结果：" + err)
                });


                // this.version_change_confirm_visible = false;
            },

            // 版本变更确认
            handleVersionChangeConfirm() {
                this.version_change_confirm_visible = false;
            },

        },


        mounted() {
            this.fetchData();
        }
    }
</script>

<style>
    #DMGraph {
        width: 60%;
    }

    #right-management-box {
        position: fixed;
        right: 80px;
        top: 200px;
    }
</style>