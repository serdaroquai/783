package org.serdar.deeplearning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.anyRequest()
			.authenticated()
			.and()
			.httpBasic()
			.and().csrf().disable()
			.headers().frameOptions().disable();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.inMemoryAuthentication()
//				.withUser("serdar").password("serdar1234").roles("USER").and()
//				.withUser("player2").password("player2").roles("USER").and()
//				.withUser("player3").password("player3").roles("USER").and()
			.withUser("user29").password("29").roles("USER").and()
			.withUser("user31").password("31").roles("USER").and()
			.withUser("user37").password("37").roles("USER").and()
			.withUser("user41").password("41").roles("USER").and()
			.withUser("user43").password("43").roles("USER").and()
			.withUser("user47").password("47").roles("USER").and()
			.withUser("user53").password("53").roles("USER");
	}
}