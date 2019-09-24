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
        {path: '/FormTest', component: () => import('./components/FormTest.vue')},
        {path: '/DragUploadTest', component: () => import('./components/DragUploadTest.vue')},
        {path: '/DependencyGraph', component: () => import('./components/echarts/DependencyGraph.vue')},
        {path: '/ServicesRegistry', component: () => import('./views/DependencyManagement/ServicesRegistry.vue')},
        {path: '/ServicesDeployment', component: () => import('./views/DependencyManagement/ServicesDeployment.vue')},
        {path: '/ServiceVersionChange', component: () => import('./views/DependencyManagement/ServiceVersionChange.vue')},
        {path: '/DeployManagement', component: () => import('./views/DependencyManagement/DeployManagement.vue')},
      ]
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('./views/About.vue')
    },
  ]
})
