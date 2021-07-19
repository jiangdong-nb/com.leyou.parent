package com.leyou.upload.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @author jiangdong
 * @date 2021/7/19 8:17
 */
@Service
public class UploadService {

    //添加类型白名单
    private static final List<String> CONTENT_TYPE= Arrays.asList("image/gif","image/jpeg");

    private static final Logger LOGGER= LoggerFactory.getLogger(UploadService.class);


    /**
     * 图片上传
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {

        String originFileName= file.getOriginalFilename();

        //校验文件类型
        String fileType=file.getContentType();
        if(!CONTENT_TYPE.contains(fileType)){
            LOGGER.info("文件类型不合法：{}",originFileName);
            return null;
        }
        //校验文件内容
        try {
            BufferedImage bufferedImage=ImageIO.read(file.getInputStream());
            if(bufferedImage==null){
                LOGGER.info("文件内容不合法:{}",originFileName);
                return null;
            }
            //保存到文件的服务器
            file.transferTo(new File("E:\\11-mall_leyou\\images\\"+originFileName));
            //返回url进行回显
            return "http://image.leyou.com/"+originFileName;
        } catch (IOException e) {
            LOGGER.info("服务器内部错误:{}",originFileName);
            e.printStackTrace();
        }
        return null;
    }
}
