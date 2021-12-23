package api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author jiangdong
 * @date 2021/7/20 8:25
 */

@RequestMapping("spec")
public interface SpecificationApi {


    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
     @GetMapping("params")
     public List<SpecParam> queryParamByGid(@RequestParam(value = "gid",required = false) Long gid,
                                                            @RequestParam(value = "cid",required = false) Long cid,
                                                            @RequestParam(value = "generic",required = false)Boolean generic,
                                                            @RequestParam(value = "searching",required = false)Boolean searching);
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParams(@PathVariable("cid") Long cid);

}
