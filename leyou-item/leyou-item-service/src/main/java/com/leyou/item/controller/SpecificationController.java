package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author jiangdong
 * @date 2021/7/20 8:25
 */
@Controller
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询参数组
     * @param cid
     * @return
     */
     @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGruopByCid(@Param("cid")Long cid){
         List<SpecGroup> groups=this.specificationService.queryGroupsByCid(cid);
         if(CollectionUtils.isEmpty(groups)){
             return ResponseEntity.notFound().build();
         }
         return ResponseEntity.ok(groups);
     }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
     @GetMapping("params")
     public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam(value = "gid",required = false) Long gid,
                                                            @RequestParam(value = "cid",required = false) Long cid,
                                                            @RequestParam(value = "generic",required = false)Boolean generic,
                                                            @RequestParam(value = "searching",required = false)Boolean searching){
        List<SpecParam> params=this.specificationService.queryParamsByFGid(gid,cid,generic,searching);
        if(CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
     }
}
