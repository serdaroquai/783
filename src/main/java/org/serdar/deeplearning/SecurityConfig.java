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
			.withUser("user29").password("29").roles("USER").and() //serdar
			.withUser("user31").password("31").roles("USER").and() //suheyl
			.withUser("user37").password("37").roles("USER").and() //zülü
			.withUser("user41").password("41").roles("USER").and() //erk
			.withUser("user43").password("43").roles("USER").and() //ilke
			.withUser("user47").password("47").roles("USER").and() //sinan
			.withUser("user53").password("53").roles("USER").and() //cevik
			.withUser("user59").password("59").roles("USER").and()
			.withUser("user61").password("61").roles("USER").and()
			.withUser("user67").password("67").roles("USER").and()
			.withUser("user71").password("71").roles("USER").and()
			.withUser("user73").password("73").roles("USER").and()
			.withUser("user79").password("79").roles("USER").and()
			.withUser("user83").password("83").roles("USER").and()
			.withUser("user89").password("89").roles("USER").and()
			.withUser("user97").password("97").roles("USER").and()
			.withUser("user101").password("101").roles("USER").and()
			.withUser("user103").password("103").roles("USER").and()
			.withUser("user107").password("107").roles("USER").and()
			.withUser("user109").password("109").roles("USER").and()
			.withUser("user113").password("113").roles("USER").and()
			.withUser("user127").password("127").roles("USER").and()
			.withUser("user131").password("131").roles("USER").and()
			.withUser("user137").password("137").roles("USER").and()
			.withUser("user139").password("139").roles("USER").and()
			.withUser("user149").password("149").roles("USER").and()
			.withUser("user151").password("151").roles("USER");
			
	}
}