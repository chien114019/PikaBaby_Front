package com.example.demo.config;

import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserAccountRepository userRepo;

    public SecurityConfig(UserAccountRepository userRepo) {
        this.userRepo = userRepo;
    }

    // 密碼加密用的 Bean（用在登入驗證時比較）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 登入驗證的資料來源（依帳號找使用者）
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
        	// 用帳號去資料庫找對應的使用者，如果找不到就丟錯誤
//            UserAccount user = userRepo.findByUsername(username);
//            if (user == null) throw new UsernameNotFoundException("找不到使用者：" + username);
        	UserAccount user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("找不到使用者：" + username));

         // 把資料庫撈出來的 user 轉成 Spring Security 能用的格式
            return User.withUsername(user.getUsername())
                .password(user.getPassword()) // 這裡是加密過的密碼
                .roles(user.getRole()) // 根據 role 欄位決定權限 // Spring Security 會自動加 "ROLE_" 前綴
                .disabled(!Boolean.TRUE.equals(user.getEnabled())) // 若為 null 也當作 false
                .build();
        };
    }

    // 自訂安全規則
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http 
        	.cors()
        	.and()

        	.csrf().disable()
        	.authorizeHttpRequests(auth -> auth
        	    .anyRequest().permitAll() //完全放行
        	    )
        
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login", "/css/**", "/js/**", "/logo-pikababy.png").permitAll() // 登入畫面、靜態資源不需驗證
//                .requestMatchers("/secondhand/front/**", "/orders/front/**").permitAll() // 登入畫面、靜態資源不需驗證
//                .requestMatchers("/employee/**").hasAnyRole("ADMIN", "ROOT") // 僅 ADMIN、ROOT 可用/employee 開頭的網址
//                .anyRequest().authenticated() // 其他都要登入後才可看
//            )
            
        
       
            
            
            .formLogin(login -> login
                .loginPage("/login") // 自訂登入畫面
                .defaultSuccessUrl("/", true) // 登入成功導向index
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // 登出後導向登入畫面
                .permitAll()
            );

        return http.build();
    }

    // 提供 AuthenticationManager（密碼驗證時用）
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    

    
}
