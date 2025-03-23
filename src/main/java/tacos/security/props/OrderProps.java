package tacos.security.props;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 订单相关配置属性
 * 从application.properties中读取taco.orders前缀的配置
 */
@Data
@Validated
@ConfigurationProperties(prefix = "taco.orders")
public class OrderProps {

    /**
     * 每页显示的订单数量
     * 取值范围: 5-25
     */
    @Min(value = 5, message = "must be between 5 and 25")
    @Max(value = 25, message = "must be between 5 and 25")
    private Integer pageSize = 20;
}


