package com.delgo.api.comm.security;


import com.delgo.api.comm.security.jwt.JwtAuthenticationFilter;
import com.delgo.api.repository.UserRepository;
import com.delgo.api.comm.security.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private final UserRepository userRepository;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.formLogin().disable()
				.httpBasic().disable()

				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
				.addFilter(new JwtAuthorizationFilter(authenticationManager(),userRepository))
				.authorizeRequests()
				// Wish
				.antMatchers("/wish/**").authenticated()
				// Booking
				.antMatchers("/booking/request").authenticated()
				.antMatchers("/booking/getData").authenticated()
				.antMatchers("/booking/getData/main").authenticated()
				.antMatchers("/booking/cancel/**").authenticated()
				.antMatchers("/booking/cancel/**").authenticated()
				// Cupon
				.antMatchers("/coupon/regist").authenticated()
				.antMatchers("/coupon/getCouponList").authenticated()
				// Review
				.antMatchers("/review/write").authenticated()
				.antMatchers("/review/getReview/user").authenticated()
				.antMatchers("/photo/upload/reviewPhoto/**").authenticated()
				// myAccount
				.antMatchers("/myAccount").authenticated()
				.antMatchers("/changePetInfo").authenticated()
				.antMatchers("/changePassword").authenticated()
				.anyRequest().permitAll();
	}
}






