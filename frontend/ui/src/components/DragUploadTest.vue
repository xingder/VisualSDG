<template>
    <div>
        <h2>拖拽上传</h2>
        <a-upload-dragger name="file" :multiple="true" action="http://localhost:8888/service" @change="handleChange">
<!--        <a-upload-dragger name="file" :multiple="true" action="https://www.mocky.io/v2/5cc8019d300000980a055e76" @change="handleChange">-->
            <p class="ant-upload-drag-icon">
                <a-icon type="inbox" />
            </p>
            <p class="ant-upload-text">Click or drag file to this area to upload</p>
            <p class="ant-upload-hint">Support for a single or bulk upload. Strictly prohibit from uploading company data or other band files</p>
        </a-upload-dragger>
        <input type="file" :id="id" name="image" class="getImgUrl_file" @change="preview($event)">
    </div>
</template>
<script>
    export default {
        name: "DragUploadTest",
        data () {
            return {
                id: 0,
            }
        },
        methods: {
            handleChange(info) {
                const status = info.file.status;
                if (status !== 'uploading') {
                    console.log(info.file, info.fileList);
                }
                if (status === 'done') {
                    this.$message.success(`${info.file.name} 上传成功`);
                } else if (status === 'error') {
                    this.$message.error(`${info.file.name} 上传失败`);
                }
            },
            preview(event){
                let files = document.getElementById(this.id).files[0];
                var reader = new FileReader();
                let res = reader.readAsText(this.getObjectURL(files))
                console.log(res)
            },
            getObjectURL (file) {
                let url = null ;
                if (window.createObjectURL!=undefined) { // basic
                    url = window.createObjectURL(file) ;
                }else if (window.webkitURL!=undefined) { // webkit or chrome
                    url = window.webkitURL.createObjectURL(file) ;
                }else if (window.URL!=undefined) { // mozilla(firefox)
                    url = window.URL.createObjectURL(file) ;
                }
                return url ;
            },
        },
    }
</script>

<style>
    .ant-upload.ant-upload-drag {
        height: 200px;
    }
</style>