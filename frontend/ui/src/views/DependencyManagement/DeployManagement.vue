<template>
    <div class="DeployManagement">
        <div id="DMGraph">
            <DependencyGraph ref="graph"/>
        </div>
        <div id="right">
            <h2>部署顺序</h2>
            <a-button style="margin: 30px" @click="fetchDeploySequences" type="primary">生成部署顺序</a-button>
            <a-timeline>
                <div v-for="deploy in deploy_list">
                    <a-timeline-item>{{deploy.serviceName}} [{{deploy.version}}]</a-timeline-item>
                </div>
            </a-timeline>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import DependencyGraph from '../../components/echarts/SDG.vue';

    export default {
        name: "DeployManagement",
        components: {
            DependencyGraph,
        },
        data() {
            return {
                deploy_list: [],
            }
        },
        methods: {
            fetchDeploySequences() {
                const URL_GET_DEPLOY_LIST = 'http://localhost:8888/deployList';
                axios.get(URL_GET_DEPLOY_LIST).then(response => {
                    this.deploy_list = response.data;
                }).catch((err) => {
                    console.log("无法获取 deployList 数据: " + err)
                    this.$message.error("无法获取 deployList 数据: " + err);
                });
            },
        },
        mounted() {
        },
    }
</script>

<style>
    #DMGraph {
        float: left;
        width: 60%;
    }

    #right {
        float: right;

    }
</style>