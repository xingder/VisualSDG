package io.yingchi.visualsdgmongodb.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FileObjectUtil {

    private static Logger logger = LoggerFactory.getLogger(FileObjectUtil.class);

    /**
     * 上传的 YAML 文件转为 JSON 字符串
     * @param request
     * @return
     * @throws IOException
     */
    public static String getJsonStringFromYamlFileRequest(HttpServletRequest request) throws IOException {
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");

        Yaml yaml = new Yaml();
        Object load = yaml.load(file.getInputStream());

        String jsonString = JSON.toJSONString(load);

        logger.info("从请求的文件中提取到 JSON String: " + jsonString);

        return jsonString;
    }
}
