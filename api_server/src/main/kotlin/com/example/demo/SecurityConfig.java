package com.example.demo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {
    @Override
    @Order(Ordered.HIGHEST_PRECEDENCE)
	public void configure(HttpSecurity http) throws Exception {
		http .cors().and()  //.disable()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
//		.csrf().disable()
//	  	.authorizeRequests()
//        .anyRequest().permitAll().and().au //.authenticated()
////	  	.and()  //.cors().disable()
////	  	.httpBasic().realmName("CRM_REALM");


                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()  //.cors().disable()
                .httpBasic().realmName("CRM_REALM");
    }

//    @Autowired AuthEntryPoint entryPoint;


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {

        resources.resourceId(null);
    }


}



