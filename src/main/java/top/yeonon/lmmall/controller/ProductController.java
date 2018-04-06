package top.yeonon.lmmall.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerResponse;

import top.yeonon.lmmall.service.IProductService;
import top.yeonon.lmmall.vo.ProductDetailsVo;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 15:35
 **/
@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {

        return productService.getProducts(keyword, categoryId, pageNum, pageSize, orderBy);
    }

    @GetMapping("{productId}")
    public ServerResponse<ProductDetailsVo> getProductDetails(@PathVariable("productId") Integer productId) {
        return productService.getProductDetails(productId);
    }
}