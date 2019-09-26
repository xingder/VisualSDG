<template>
  <div class="home">
    <a-layout id="components-layout-demo-top-side">
      <a-layout-header class="header">
        <div class="logo">VisualSDG</div>
        <a-menu
                theme="dark"
                mode="horizontal"
                :defaultSelectedKeys="['1']"
                :style="{ lineHeight: '64px' }"
        >
          <a-menu-item key="1"><router-link to="/">Home</router-link></a-menu-item>

        </a-menu>
      </a-layout-header>
      <a-layout-content style="padding: 0 50px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>VisualSDG</a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout style="padding: 24px 0; background: #fff">
          <a-layout-sider width="200" style="background: #fff">
            <a-menu
                    mode="inline"
                    :defaultSelectedKeys="['1']"
                    :defaultOpenKeys="['sub1']"
                    style="height: 100%"
            >
              <a-sub-menu key="sub1">
                <span slot="title"><a-icon type="laptop" />服务管理</span>
                <a-menu-item><router-link to="/DependencyGraph">依赖拓扑</router-link></a-menu-item>
                <a-menu-item><router-link to="/ServicesRegistry">服务中心</router-link></a-menu-item>
              </a-sub-menu>
              <a-sub-menu key="sub2">
                <span slot="title"><a-icon type="laptop" />部署管理</span>
                <a-menu-item><router-link to="/ServicesDeployment">服务部署</router-link></a-menu-item>
                <a-menu-item><router-link to="/TenantDeploy">租户方案</router-link></a-menu-item>
                <a-menu-item><router-link to="/ServiceVersionChange">服务变更</router-link></a-menu-item>
                <a-menu-item><router-link to="/DeployManagement">部署管理</router-link></a-menu-item>
              </a-sub-menu>

              <a-sub-menu>
                <span slot="title"><a-icon type="notification" />设置/选项</span>
                <a-menu-item key="1">可视化选项</a-menu-item>
                <a-menu-item><router-link to="/DragUploadTest">上传测试</router-link></a-menu-item>
              </a-sub-menu>
            </a-menu>
          </a-layout-sider>
          <a-layout-content id="content-area" :style="{ padding: '0 24px', minHeight: '200px' }">
            <router-view></router-view>
          </a-layout-content>
        </a-layout>
      </a-layout-content>
      <a-layout-footer style="text-align: center">
        VisualSDG ©2019 Created by YINGCHI_Joey
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script>

export default {
  name: 'home',
  components: {
  },
  mounted() {
    // this.$router.replace({path: "/dependencyGraph"});
  }
}
</script>

<style>
    .home {
        position: fixed;
        width: 100%;
        height: 100%;
        background-color: rgba(255, 255, 255, .2);
    }

    #components-layout-demo-top-side {
        height: 100%;
    }

    #components-layout-demo-top-side .logo {
        width: 120px;
        height: 31px;
        text-align: center;
        font-size: 20px;
        font-weight: bolder;
        float: left;
        color: #ffffff;
    }

    .ant-layout .ant-layout-has-sider {
        height: 800px;
        overflow: scroll;
    }

  #content-area {
  }

</style>