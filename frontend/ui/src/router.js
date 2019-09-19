import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      redirect: '/dependencyGraph',
    },
    {
      path: '/home',
      name: 'home',
      component: Home,
      children: [
        {path: '/formTest', component: () => import('./components/FormTest.vue')},
        {path: '/dragUploadTest', component: () => import('./components/DragUploadTest.vue')},
        {path: '/dependencyGraph', component: () => import('./components/echarts/DependencyGraph.vue')},
        {path: '/dataMaintanance', component: () => import('./views/DependencyManagement/DataMaintanance.vue')},
      ]
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('./views/About.vue')
    },
  ]
})
