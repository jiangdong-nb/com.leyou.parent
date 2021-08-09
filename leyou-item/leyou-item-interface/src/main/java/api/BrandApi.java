package api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jiangdong
 * @date 2021/8/9 19:15
 */

@RequestMapping("brand")

public interface BrandApi {

    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id")Long id);
}
