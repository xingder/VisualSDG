<template>
    <div>
        <div id="dependencyGraph"></div>
    </div>
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

                // 数据处理
                this.nodes.forEach(function (node) {
                    node.symbolSize = node.value;
                    node.label = {
                        normal: {
                            show: node.symbolSize > 1
                        }
                    };
                    // node.category = node.attributes.modularity_class;
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
                                gravity: 0.02, // 中心引力
                                repulsion: 100,
                                edgeLength: [50, 200],
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
                                position: 'right',
                                formatter: '{b}'
                            },
                            lineStyle: {
                                color: 'source',
                                curveness: 0.3
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
    width: 100%;
    height: 700px;
}
</style>