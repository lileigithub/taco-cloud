package tacos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tacos.security.entity.User;
import tacos.security.repository.UserRepository;

/**
 * Spring Security 的基本配置类
 * 负责配置应用的安全策略，包括：
 * 1. 密码加密方式
 * 2. 用户认证方式
 * 3. 请求授权规则
 * 4. 登录/登出行为
 */
@Configuration
@EnableMethodSecurity  // 启用方法级别的安全注解
public class SecurityConfig {

    /**
     * 密码编码器 Bean
     * 使用 BCrypt 加密算法，这是一种安全的单向哈希算法
     * 在创建新用户和登录验证用户身份时使用
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 用户详情服务 Bean
     * 负责根据用户名查找用户信息
     * 用于 Spring Security 的用户认证过程
     *
     * @param userRepo 用户数据访问对象
     * @return UserDetailsService 实例
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> {
            User user = userRepo.findByUsername(username);
            if (user != null) return user;
            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }

    /**
     * 安全过滤器链配置
     * 配置应用的安全策略，包括：
     * 1. 请求授权规则
     * 2. 登录表单配置
     * 3. 登出行为
     * 4. CSRF 保护
     * 5. 响应头配置
     *
     * @param http HttpSecurity 对象
     * @return 配置好的 SecurityFilterChain
     * @throws Exception 配置过程中的异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 需要 USER 角色的请求
                        .requestMatchers("/design", "/orders").hasRole("USER")
                        // 允许所有人访问的请求
                        .requestMatchers("/", "/**").permitAll()
                        // 允许访问 H2 控制台
                        .requestMatchers("/h2-console/**").permitAll()
                )
                // 配置表单登录
                .formLogin(form -> form
                        // 自定义登录页面
                        .loginPage("/login")
                        // 登录成功后的跳转页面
                        .defaultSuccessUrl("/design")
                )
                // 配置登出行为
                .logout(logout -> logout.logoutSuccessUrl("/"))
                // 配置 CSRF 保护
                .csrf(csrf -> csrf
                        // 禁用 H2 控制台的 CSRF 保护
                        .ignoringRequestMatchers("/h2-console/**")
                )
                // 配置响应头
                .headers(headers -> headers
                        // 禁用 X-Frame-Options，允许 H2 控制台在 iframe 中显示
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .build();
    }
}
