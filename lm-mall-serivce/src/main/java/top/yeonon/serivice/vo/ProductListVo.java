package top.yeonon.serivice.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author yeonon
 * @date 2018/4/5 0005 16:08
 **/
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductListVo implements Serializable {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subTitle;
    private String mainImage;
    private BigDecimal price;
    private Integer stock;

    private String imageHost;
}
