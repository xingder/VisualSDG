<template>
    <div class="ServiceVersionChange">
        <div id="DMGraph">
            <DependencyGraph ref="graph"/>
        </div>

        <div id="right-management-box">
            <h2>服务变更</h2>
            <div v-for="(currentService,index) in currentSelectedServices" style="margin: 20px">
                服务名：<span style="color: #42b983; font-weight: bolder">{{currentService.serviceName}}</span>
                <a-divider type="vertical" />版本：{{currentService.version}} <a-divider type="vertical" />

                <span v-if="selectedServicesMutiversionFlags[index]">
                    <a-button @click="showVersionChangeConfirm(currentService.serviceName,currentService.version)" style="margin-left: 5px; font-size: 10px;" size="small" >版本变更</a-button>
                </span>
                <span v-else>
                    <a-button style="margin-left: 5px; font-size: 10px" size="small" disabled>唯一版本</a-button>
                </span>

            </div>
            <a-divider type="horizontal"/>
            <div>
                <div style="font-weight: bold">待变更服务：</div>
                    <div v-if="showChangeService === true && serviceChangeCheckedPassed === true">
                        <a-tag style="margin: 20px" v-show="showChangeService === true && serviceChangeCheckedPassed === true"> {{markVersionChangeService.serviceName}} ==> {{markVersionChangeService.version}}</a-tag>
                    </div>
                    <div v-else>
                        <div style="margin: 20px">当前无需要变更的服务</div>
                    </div>
            </div>


            <span v-if="showChangeService === true && serviceChangeCheckedPassed === true">
                <a-button type="primary" style="margin-left: 30px" @click="executeSelectedServicesChange">执行变更</a-button>
            </span>
            <span v-else>
                <a-button type="primary" style="margin-left: 30px" @click="executeSelectedServicesChange" disabled>执行变更</a-button>
            </span>

            <a-button style="margin-left: 10px" @click="()=>{this.showChangeService = false}">重置变更</a-button>


        </div>

        <!-- 版本变更确认框-->
        <a-modal
                title="版本变更"
                v-model="version_change_confirm_visible"
                @ok="handleVersionChangeConfirm"
                okText="确认"
                cancelText="取消"
        >
            <p v-if="markVersionChangeService.serviceName !== null" style="color: #00b93a; font-weight: bold">
                依赖检测通过，是否将服务 {{this.markVersionChangeService.serviceName}} [{{this.markVersionChangeService.version}}] 添加至待变更列表?
            </p>

            <div v-for="service in allServices">
                <div v-if="service.serviceName === currentSelectedService.serviceName">
                    {{service.serviceName}} - {{service.version}}
                    <span v-if="service.version !== currentSelectedService.version"><a-button style="margin:15px" @click="handleVersionChangeCheck(service.serviceName, service.version)">变更到此版本</a-button></span>
                    <span v-else style="margin-left: 20px"> 当前版本 </span>
                </div>
            </div>

        </a-modal>



    </div>
</template>

<script>
    import axios from 'axios';
    import DependencyGraph from '../../components/echarts/DependencyGraph.vue';

    export default {
        name: "ServiceVersionChange",
        components: {
            DependencyGraph,
        },
        data() {
            return {
                allServices: [],
                currentSelectedServices: [], // 当前选中的服务列表

                markVersionChangeService: { // 当前检测通过的服务
                    serviceName: '',
                    version: '',
                },

                versionChangeServicesList: [], // 所有需要变更的服务列表

                version_change_confirm_visible: false,
                currentSelectedService: {
                    serviceName: '',
                    version: '',
                },
                showChangeService: false,
                serviceChangeCheckedPassed: false,
                selectedServicesMutiversionFlags: [],
            }
        },
        methods: {
            fetchData() {
                const URL_GET_SELECTED_SERVICES = 'http://localhost:8888/selectedService';
                const URL_GET_ALL_SERVICES = 'http://localhost:8888/service';
                const URL_GET_CHECK_MULTIVERSION = 'http://localhost:8888/selectedServicesMutiversionFlags';

                axios.get(URL_GET_SELECTED_SERVICES).then(response => {
                    // console.log(response.data);
                    this.currentSelectedServices = response.data;
                }).catch((err) => {
                    console.log("无法获取 selectedService 数据: " + err)
                });

                axios.get(URL_GET_ALL_SERVICES).then(response => {
                    // console.log(response.data);
                    this.allServices = response.data;
                }).catch((err) => {
                    console.log("无法获取 allService 数据: " + err)
                });

                axios.get(URL_GET_CHECK_MULTIVERSION).then(response => {
                    // console.log(response.data)
                    this.selectedServicesMutiversionFlags = response.data;
                }).catch((err) => {
                    console.log("无法获取 selectedServicesMutiversionFlags 数据: " + err)
                });
            },

            showVersionChangeConfirm(currentService, currentVersion) {
                this.currentSelectedService.serviceName = currentService;
                this.currentSelectedService.version = currentVersion;

                this.markVersionChangeService.serviceName = null;
                this.markVersionChangeService.version = null;

                this.version_change_confirm_visible = true;
                this.serviceChangeCheckedPassed = false;
                this.showChangeService = false;
            },

            // 版本变更可行性检查
            handleVersionChangeCheck(serviceName, toVersion) {

                const URL_GET_SERVICE_VERSION_CHANGE_CHECK = 'http://localhost:8888/serviceVersionChangeCheck';

                axios.get(URL_GET_SERVICE_VERSION_CHANGE_CHECK, {params:{serviceName: serviceName,toVersion:toVersion}}).then(response => {
                    let ableToChange = response.data.ableToChange;
                    let reason = response.data.reason;

                    if (ableToChange === "yes") {
                        this.$message.success("经依赖性检查，可以进行版本变更");
                        this.markVersionChangeService.serviceName = serviceName;
                        this.markVersionChangeService.version = toVersion;
                        this.serviceChangeCheckedPassed = true;

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
                this.showChangeService = true;
            },

            executeSelectedServicesChange() {
                const URL_GET_VERSION_CHANGED_SELECTED_SERVICE = 'http://localhost:8888/SelectedServices/VersionChanged';

                axios.get(URL_GET_VERSION_CHANGED_SELECTED_SERVICE, {params:{serviceName: this.markVersionChangeService.serviceName,toVersion: this.markVersionChangeService.version}}).then(response => {
                    // console.log(response.data);
                    this.$refs.graph.fetchDataAndDrawGraph();
                    this.fetchData();
                    this.showChangeService = false;
                    this.serviceChangeCheckedPassed = false;
                }).catch((err) => {
                    console.log("执行 executeSelectedServicesChange 响应错误：" + err)
                });


            }

        },

        mounted() {
            this.fetchData();
        }
    }
</script>

<style>
    #DMGraph {
        float: left;
        width: 60%;
    }

    #right-management-box {
        float: right;
    }
</style>