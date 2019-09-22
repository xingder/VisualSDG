<template>
    <div class="graphGenerate">

        <div id="left">
            <DependencyGraph  ref="graph"/>
        </div>

        <div id="right">
            <h2>依赖生成</h2>
            <p>服务端检索到已存在 {{cascaders.length}} 个服务</p>
            <p>当前已选择 {{selectedServices.length}} 个服务</p>


            <div v-for="_,index in cascaders" class="service-cascaders">
                服务 {{index+1}}
                <a-cascader class="service-cascader" :options="cascaders[index]" @change="onChange" :placeholder="cascaders[index][0].label" :allowClear="false" />
            </div>

            <div style="text-align: center; margin: 30px">
                <a-button type="primary" style="width: 100px; margin: 10px" @click="submitSelectedService">提交/生成</a-button>

                <a-button type="primary" style="width: 100px; margin: 10px" @click="resetSelect">重置</a-button>
            </div>
        </div>

    </div>


</template>


<script>
    import axios from 'axios';
    import DependencyGraph from '../../components/echarts/DependencyGraph.vue';

    export default {
        name: "GraphGenerate",
        components: {
            DependencyGraph,
        },
        data() {
            return {
                cascaders: [],
                selectedServices: [], // 选择的服务及版本列表
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

            submitSelectedService() {

                // if (this.selectedServices.length !== this.cascaders.length) {
                //     // 确保全部服务均选择
                //     alert("请确保为每个服务选择了版本");
                //     this.cascaders = [];
                //     this.fetchData();
                //     return;
                // }

                const URL_POST_SELECTED_SERVICE = 'http://localhost:8888/selectedService';

                axios.post(URL_POST_SELECTED_SERVICE, this.selectedServices).then(response => {
                    this.$refs.graph.fetchDataAndDrawGraph(); // 选择服务改变后 DependencyGraph 组件依赖图数据重新获取
                    this.selectedServices = [];
                    this.cascaders = [];
                    this.fetchData();
                }).catch((err) => {
                    console.log("无法绘制 nodes 数据: " + err)
                });

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
    .service-cascaders {
        text-align: center;
    }

    .service-cascader {

        margin: 10px;
        width: 300px;
    }

    #left {
        width: 60%;
    }

    #right {
        position: fixed;
        right: 80px;
        top: 200px;

    }

</style>