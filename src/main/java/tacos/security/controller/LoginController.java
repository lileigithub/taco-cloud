package tacos.security.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录控制器
 * 处理用户登录相关的请求
 */
@Controller
public class LoginController {

    /**
     * 处理登录页面请求
     * 如果用户已经登录，则重定向到首页
     * GET /login
     */
    @GetMapping("/login")
    public String loginPage() {
        // 获取当前用户认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 检查用户是否已经认证（排除匿名用户）
        if (authentication != null && authentication.isAuthenticated() 
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            // 已登录用户重定向到首页
            return "redirect:/";
        }
        
        // 未登录用户显示登录页面
        return "login";
    }
} 