package tacos.security.controller;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tacos.security.repository.UserRepository;

/**
 * 测试专用配置类
 * 提供测试所需的Bean和安全配置
 */
@Configuration
public class TestConfig {

    /**
     * 提供UserRepository的模拟实现
     * 用于测试而不是实际访问数据库
     */
    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    /**
     * 提供PasswordEncoder的模拟实现
     * 用于测试而不是实际加密密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }
    
    /**
     * 配置测试环境的安全设置
     * 禁用CSRF保护并允许所有请求，简化测试
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护，允许不带令牌的POST请求
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 允许所有请求，不需要认证
            .build();
    }
} 