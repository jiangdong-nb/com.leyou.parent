package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


/**
 * @author jiangdong
 * @date 2021/8/13 14:26
 */
@Service
public class GoodsHtmlService {
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private GoodsService goodsService;

    /**
     * 為了提高服務器的相應速度將靜態頁面放到本地，用nginx代理提升並發的速度
     * @param spuId
     */
    public void createHtml(Long spuId){

        //初始化運行上下文
        Context context=new Context();
        //設置數據模型
        context.setVariables(this.goodsService.loadData(spuId));
        PrintWriter printWriter = null;

        try {
            //把靜態文件生成到服務器本地
            File file = new File("E:\\11-mall_leyou\\tools\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter=new PrintWriter(file);
            this.templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(printWriter!=null){
                printWriter.close();
            }
        }
    }
}
