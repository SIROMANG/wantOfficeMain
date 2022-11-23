package com.project.office.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project.office.jwt.JwtAccessDeniedHandler;
import com.project.office.jwt.JwtAuthenticationEntryPoint;
import com.project.office.jwt.TokenProvider;

@EnableWebSecurity
public class SecurityConfig {
	
	// 인증 실패 핸들러
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;	
	// 인가 실패 핸들러
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	// 토큰 제공
	private final TokenProvider tokenProvider;
	
	public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, TokenProvider tokenProvider) {
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
		this.tokenProvider = tokenProvider;
	}

	@Bean
	public WebSecurityCustomizer configure() {
		return (web) -> web.ignoring().antMatchers("/productimgs/**");
	}
	
	// 비밀번호 암호화
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		 return http
		         .csrf()
		         	.disable()
		         .exceptionHandling()
		         	.authenticationEntryPoint(jwtAuthenticationEntryPoint)
		         	.accessDeniedHandler(jwtAccessDeniedHandler)
		         .and()
		         .sessionManagement()
		             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		         .and()
		             .authorizeRequests()
		             .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		             .antMatchers("/auth/**").permitAll()
		             .antMatchers("/api/attendance-manage").hasRole("ADMIN")
		             .antMatchers("/api/**").hasAnyRole("MEMBER", "APP_AUTH", "ADMIN")
		             .antMatchers("/api/room/**").permitAll()
		         .and()
		         	.cors()
		         .and()
		         	.apply(new JwtSecurityConfig(tokenProvider))
		         .and()
		         	.build();
		 
	}
	
	@Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // 로컬 React에서 오는 요청은 CORS 허용해준다.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000" ));
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Content-Type", "Access-Control-Allow-Headers", "Authorization", "X-Requested-With"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
	
}
