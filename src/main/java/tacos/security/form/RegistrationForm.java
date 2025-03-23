package tacos.security.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;
import tacos.security.entity.User;

/**
 * 用户注册表单
 * 包含验证注解
 */
@Data
public class RegistrationForm {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度必须至少6个字符")
    private String password;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirm;
    
    @NotBlank(message = "姓名不能为空")
    private String fullname;
    private String street;
    private String city;
    private String state;
    
    @Pattern(regexp = "^\\d{6}$", message = "邮编必须是6位数字")
    private String zip;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String phone;

    /**
     * 转换为用户实体
     */
    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(null, username, passwordEncoder.encode(password),
                fullname, street, city, state, zip, phone);
    }
}
