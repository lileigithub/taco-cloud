package tacos.security.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tacos.security.entity.User;
import tacos.security.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 注册控制器的测试类
 * 测试注册表单的各种场景，包括表单显示、验证错误和成功注册
 */
@WebMvcTest(RegistrationController.class) // 只加载RegistrationController相关的bean
@Import({ TestConfig.class }) // 导入测试配置，提供模拟对象
@WithMockUser // 提供一个模拟用户，避免Spring Security拦截测试请求
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Spring提供的模拟MVC测试工具

    @Autowired
    private UserRepository userRepo; // 从TestConfig中注入的模拟对象

    @Autowired
    private PasswordEncoder passwordEncoder; // 从TestConfig中注入的模拟对象
    
    /**
     * 每个测试执行前的设置
     * 重置所有模拟对象，防止测试之间相互影响
     */
    @BeforeEach
    public void setup() {
        reset(userRepo, passwordEncoder); // 重置模拟对象的所有状态
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword"); // 设置密码编码器的默认行为
    }

    /**
     * 测试注册表单页面加载
     * 期望返回registration视图并包含表单对象
     */
    @Test
    public void registerForm_ReturnsRegistrationView() throws Exception {
        mockMvc.perform(get("/register")) // 发送GET请求到/register
                .andExpect(status().isOk()) // 期望HTTP状态码200
                .andExpect(view().name("registration")) // 期望视图名为registration
                .andExpect(model().attributeExists("registrationForm")); // 期望模型中包含registrationForm属性
    }

    /**
     * 测试密码不匹配情况
     * 期望返回表单页面并显示密码不匹配错误
     */
    @Test
    public void processRegistration_PasswordMismatch_ReturnsRegistrationForm() throws Exception {
        mockMvc.perform(post("/register") // 发送POST请求到/register
                .param("username", "testUser") // 设置表单参数
                .param("password", "password")
                .param("confirm", "differentPassword") // 密码与确认密码不一致
                .param("fullname", "Test User"))
                .andExpect(status().isOk()) // 期望HTTP状态码200
                .andExpect(view().name("registration")) // 期望视图名为registration
                .andExpect(model().attributeHasFieldErrors("registrationForm", "confirm")); // 期望confirm字段有错误
        
        verify(userRepo, never()).save(any(User.class)); // 验证用户未被保存
    }

    /**
     * 测试用户名已存在情况
     * 期望返回表单页面并显示用户名已存在错误
     */
    @Test
    public void processRegistration_UsernameExists_ReturnsRegistrationForm() throws Exception {
        when(userRepo.existsByUsername("existingUser")).thenReturn(true); // 设置用户名已存在

        mockMvc.perform(post("/register") // 发送POST请求到/register
                .param("username", "existingUser") // 使用已存在的用户名
                .param("password", "password")
                .param("confirm", "password")
                .param("fullname", "Existing User"))
                .andExpect(status().isOk()) // 期望HTTP状态码200
                .andExpect(view().name("registration")) // 期望视图名为registration
                .andExpect(model().attributeHasFieldErrors("registrationForm", "username")); // 期望username字段有错误
        
        verify(userRepo, never()).save(any(User.class)); // 验证用户未被保存
    }

    /**
     * 测试表单验证错误情况（缺少必填字段）
     * 期望返回表单页面并显示验证错误
     */
    @Test
    public void processRegistration_ValidationErrors_ReturnsRegistrationForm() throws Exception {
        mockMvc.perform(post("/register") // 发送POST请求到/register
                .param("password", "password") // 只提供部分参数，缺少必填字段如username和fullname
                .param("confirm", "password"))
                .andExpect(status().isOk()) // 期望HTTP状态码200
                .andExpect(view().name("registration")) // 期望视图名为registration
                .andExpect(model().attributeHasErrors("registrationForm")); // 期望表单对象有错误
        
        verify(userRepo, never()).save(any(User.class)); // 验证用户未被保存
    }

    /**
     * 测试成功注册情况
     * 期望重定向到登录页面并显示成功消息
     */
    @Test
    public void processRegistration_SuccessfulRegistration_ReturnsLoginView() throws Exception {
        mockMvc.perform(post("/register") // 发送POST请求到/register
                .param("username", "newUser") // 设置有效的表单参数
                .param("password", "password")
                .param("confirm", "password")
                .param("fullname", "New User"))
                .andExpect(view().name("login")) // 期望视图名为login
                .andExpect(model().attribute("successMessage", "注册成功，请登录")); // 期望成功消息
        
        verify(userRepo).save(any(User.class)); // 验证用户被保存
    }
}
