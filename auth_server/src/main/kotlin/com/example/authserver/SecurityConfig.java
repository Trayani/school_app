package com.example.authserver;

//import com.example.authserver.auth.MongoUserDetailsService;
//import com.example.authserver.auth.UserDetailsService;
import com.example.authserver.auth.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.util.HashMap;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private ClientDetailsService clientDetailsService;


	@Autowired
	private UserDetailsService databaseUserDetailsService;

	@Autowired
	private PasswordEncoder encoder;


	@Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(databaseUserDetailsService).passwordEncoder(passwordEncoder());
    }

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS);
	}

    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE)
    protected void configure(HttpSecurity http) throws Exception {
		http
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.csrf().disable()
				.cors().disable()
	  	.authorizeRequests()
	  	.antMatchers( HttpMethod.OPTIONS, "/oauth/token"
				, "/test"
				, "/datas"
				, "/tesst"
				, "/registration"
				, "/check_token"
				, "/register-teacher",
				"/register-student").permitAll() ;
//	  	.antMatchers(").permitAll()
//				.antMatchers("/test").permitAll()
//        .anyRequest().authenticated()
//	  	.and()
//	  	.httpBasic()
//		.realmName("CRM_REALM");
    }

//
	@Bean
	public PasswordEncoder   passwordEncoder() {

		HashMap encoders = new HashMap<String, PasswordEncoder>();
//		encoders.put("argon2"  ,  new Argon2PasswordEncoder());
		encoders.put("bcrypt"  ,  new BCryptPasswordEncoder());
		return new DelegatingPasswordEncoder("bcrypt",  encoders);
	}



    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
 
	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}
 
	@Bean
	@Autowired
	public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore){
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
//		handler.setRequestFactory(new DefaultOAuth2RequestFactory(databaseUserDetailsService));
//		handler.setClientDetailsService(databaseUserDetailsService );
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}
	
	@Bean
	@Autowired
	public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore);
		return store;
	}



}