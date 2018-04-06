package top.yeonon.lmmall.service;

import com.github.pagehelper.PageInfo;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.vo.ProductDetailsVo;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 16:02
 **/
public interface IProductService {

    ServerResponse<PageInfo> getProducts(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);

    ServerResponse<ProductDetailsVo> getProductDetails(Integer productId);
}