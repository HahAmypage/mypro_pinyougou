package cn.itcast.core.controller.upload;

import cn.core.itcast.utils.fdfs.FastDFSClient;
import cn.itcast.core.pojo.entity.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    /**
     * 上传图片
     * @param file
     * @return
     */
    @RequestMapping("/uploadFile.do")
    public Result upload(@RequestBody MultipartFile file){
       try {
           //将附件上传到FastDFS上
           String conf = "classpath:fastDFS/fdfs_client.conf";
           FastDFSClient fastDFSClient = new FastDFSClient(conf);
           //附件扩展名
           String fileName = file.getOriginalFilename();
           String extName = FilenameUtils.getExtension(fileName);
           String path = fastDFSClient.uploadFile(file.getBytes(),extName,null);
           String url = FILE_SERVER_URL+path;
           return  new Result(true,url);
       }catch (Exception e){
           e.printStackTrace();
           return new Result(false,"上传失败");
       }
    }

}
