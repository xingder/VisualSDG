import Vue from 'vue'
import App from './App.vue'
import router from './router'
// 引入Ant Design Vue
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
// 引入 echarts
import echarts from 'echarts'
import * as d3 from "d3";

Vue.prototype.$d3 = d3;
Vue.prototype.$echarts = echarts

Vue.use(Antd);

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
