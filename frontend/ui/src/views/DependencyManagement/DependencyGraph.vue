<template>
    <div id="DependencyGraph">
        <SDG graphNname="DependencyGraphSDG"></SDG>
    </div>
</template>

<script>
    import SDG from "../../components/echarts/SDG";
    import Axios from 'axios';

    const RESULT_NO_ERROR = 0;
    const RESULT_ERROR = 1;

    export default {
        name: "DependencyGraph",
        components:{
            SDG,
        },
        data() {
            return {
            };
        },
        mounted() {
            this.fetchData();
        },
        methods:{
            fetchData() {

            }
        }
    }
</script>

<style scoped>

</style>