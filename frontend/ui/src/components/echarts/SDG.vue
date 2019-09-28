<template>
    <div>
        <div class="selector" style="margin: 15px 0">
            租户视图：
            <a-select v-if="tenants[0].tenantName !== ''" :defaultValue="tenants[0].tenantName" style="width: 150px;"  @change="onSDGTenantSelectorChange" notFoundContent="当前无租户">
                <a-select-option v-for="tenant in this.tenants"  :value="tenant.tenantName">{{tenant.tenantName}}</a-select-option>
            </a-select>
        </div>
        <div id="graphName" class="SDG"></div>
    </div>

</template>

<script>
    import axios from 'axios';

    const RESULT_NO_ERROR = 0;
    const RESULT_ERROR = 1;

    export default {
        name: "SDG",
        components: {
        },
        props: [
            "graphName",
        ],
        data() {
            return {
                currentTenant: '',
                tenants: [
                    {
                        tenantName: '',
                    }
                ],
                chart: {},
                categories: [],
                nodes: [],
                links: [],
            }
        },
        watch:{
            currentTenant() {
                console.log("tenants changed");
                this.fetchDataAndDrawGraph();
            }
        },
        methods: {
            initTenants() {
                const URL_GET_TENANTS = 'http://localhost:8888/tenants';

                // 获取全部租户数据
                axios.get(URL_GET_TENANTS).then(response => {
                    const result = response.data;
                    if (result.status === RESULT_NO_ERROR) {
                        this.tenants = result.data;
                        this.currentTenant = this.tenants[0].tenantName;
                        this.drawGraph();
                    }
                }).catch((err)=>{
                    this.$message.error("URL_GET_TENANTS ERROR：" + err)
                });
            },
            fetchDataAndDrawGraph() {
                const URL_GET_NODES = 'http://localhost:8888/nodes';
                const URL_GET_LINKS = 'http://localhost:8888/links';

                axios.get(URL_GET_NODES).then(response => {
                    this.nodes = response.data;
                    this.drawGraph();
                    // console.log(this.nodes)
                }).catch((err)=>{
                    this.$notification.open({
                        message: '依赖图生成失败',
                        description: "失败原因：" + err + " 请确认是否强制删除了当前服务依赖图中的服务节点",
                    });
                    console.log("无法绘制 nodes 数据: " + err)
                });

                axios.get(URL_GET_LINKS).then(response => {
                    this.links = response.data;
                    this.drawGraph();
                    // console.log(response.data)
                }).catch((err)=>{
                    console.log("无法绘制 links 数据: " + err)
                });
            },
            drawGraph(){
                // 基于准备好的dom，初始化echarts实例
                let dependencyGraph = this.$echarts.init(document.getElementById("graphName"));

                // nodes 数据处理
                this.nodes.forEach(function (node) {
                    node.symbolSize = node.value;
                    if (node.value > 15) {
                        node.symbol = 'rect';
                        node.label = {
                            normal: {
                                show: true,
                                fontSize: 20,
                                fontWeight: 'bold',
                                position: 'bottom',
                            }
                        };
                    } else {
                        node.label = {
                            normal: {
                                show: false,
                                fontSize: 15,
                                fontWeight: 'bold',
                                position: 'bottom',
                            }
                        };
                    }

                });

                // links 数据处理
                this.links.forEach(function (link) {
                    link.label={
                        show: false,
                    }
                    if (link.value < 10) {
                        // Invoke
                        link.lineStyle = {
                            type: 'dotted',
                            color: 'orange',
                            width: 3,
                        }
                    } else {}

                });
                // 绘制图表
                dependencyGraph.setOption({
                    title: {
                        text: '当前租户: ' + this.currentTenant,
                        subtext: 'Service Dependency Graph',
                        top: 'top',
                        left: 'left'
                    },
                    tooltip: {},
                    series : [
                        {
                            name: 'Dependency Graph',
                            type: 'graph',
                            layout: 'force',
                            force: {
                                gravity: 0.001, // 中心引力
                                repulsion: 200,
                                edgeLength: [20, 150],
                            },
                            nodes: this.nodes,
                            links: this.links,
                            categories: this.categories,
                            roam: true,
                            focusNodeAdjacency: true,
                            draggable: true,
                            itemStyle: {
                                normal: {
                                    borderColor: '#fff',
                                    borderWidth: 1,
                                    shadowBlur: 10,
                                    shadowColor: 'rgba(0, 0, 0, 0.3)'
                                }
                            },
                            label: {
                                position: 'bottom',
                                formatter: '{b}',
                                // fontSize: 20,
                                // fontWeight: 'bold',
                            },
                            lineStyle: {
                                color: 'source',
                                curveness: 0
                            },
                            emphasis: {
                                lineStyle: {
                                    width: 10
                                }
                            }
                        }
                    ]
                });

                window.onresize = function () {
                    dependencyGraph.resize();
                }

                this.chart = dependencyGraph;
            },
            onSDGTenantSelectorChange(value) {
                // this.$message.info(value);
                this.currentTenant = value;
            },
        },
        mounted() {
            this.initTenants();
            this.drawGraph();
            this.fetchDataAndDrawGraph();
        },
        beforeDestroy() {
            if (this.chart) {
                this.chart.dispose();
            }
        }
    }
</script>

<style>
.SDG {
    /*width: 100%;*/
    height: 700px;
}
</style>