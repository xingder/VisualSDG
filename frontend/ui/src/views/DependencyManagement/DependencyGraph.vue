<template>
    <div id="dependencyGraph"></div>
</template>

<script>
    import axios from 'axios';

    export default {
        name: "DependencyGraph",
        components: {
        },
        data() {
            return {
                chart: {},
                categories: [],
                nodes: [],
                links: [],
            }
        },
        methods: {
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
                let dependencyGraph = this.$echarts.init(document.getElementById('dependencyGraph'));

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
                        text: 'Dependency Graph',
                        subtext: 'Default layout',
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
            }
        },
        mounted() {
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
#dependencyGraph {
    /*width: 100%;*/
    height: 700px;
}
</style>