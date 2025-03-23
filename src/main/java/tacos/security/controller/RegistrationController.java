package tacos.security.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tacos.security.form.RegistrationForm;
import tacos.security.repository.UserRepository;

/**
 * 用户注册控制器
 * 处理用户注册相关的请求
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数注入依赖
     */
    public RegistrationController(
            UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 显示注册表单
     * GET /register
     */
    @GetMapping
    public String registerForm(Model model) {
        // 如果模型中没有注册表单，创建一个新的
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "registration";  // 返回视图名称，对应 templates/registration.html
    }

    /**
     * 处理注册表单提交
     * POST /register
     */
    @PostMapping
    public String processRegistration(
            @Valid @ModelAttribute("registrationForm") RegistrationForm form,
            BindingResult bindingResult,
            Model model) {

        // 检查密码确认是否匹配
        if (!form.getPassword().equals(form.getConfirm())) {
            bindingResult.addError(new FieldError("registrationForm",
                    "confirm", form.getConfirm(), false, null, null,
                    "密码和确认密码不匹配"));
        }

        // 检查用户名是否已存在
        if (userRepo.existsByUsername(form.getUsername())) {
            bindingResult.addError(new FieldError("registrationForm",
                    "username", form.getUsername(), false, null, null,
                    "用户名已存在，请选择其他用户名"));
        }

        // 如果有验证错误，返回表单
        if (bindingResult.hasErrors()) {
            return "registration";  // 直接返回视图，不使用重定向
        }

        // 保存用户
        userRepo.save(form.toUser(passwordEncoder));

        // 添加成功消息，然后直接返回登录页面
        model.addAttribute("successMessage", "注册成功，请登录");
        return "login";  // 直接返回登录页面，不使用重定向
    }
}
