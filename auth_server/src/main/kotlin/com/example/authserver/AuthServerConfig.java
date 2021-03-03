package com.example.authserver;

//import com.example.authserver.auth.MongoUserDetailsService;
//import com.example.authserver.auth.UserDetailsService;
//import com.example.authserver.auth.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

//	public AuthServerConfig(PasswordEncoder )
	
	private static String REALM="CRM_REALM";

	private static final int THIRTY_DAYS = 60 * 60 * 24 * 30; 
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private UserApprovalHandler userApprovalHandler;
 
	@Autowired
//	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;


	@Autowired
	private com.example.authserver.auth.UserDetailsService databaseUserDetailsService;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

	    clients.inMemory()
	    .withClient("exampleClient")
            .secret(  passwordEncoderII().encode("exampleSecret"))   		//"{noop}exampleSecret") // "exampleSecret")
            .authorizedGrantTypes("password", "refresh_token","client_credentials")
            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
            .scopes("read", "write", "trust")
            .accessTokenValiditySeconds(3000)
            .refreshTokenValiditySeconds(THIRTY_DAYS);
	}
 
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints.userDetailsService()
		endpoints.userDetailsService(databaseUserDetailsService);
		endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
		.authenticationManager(authenticationManager);


		Map<String, CorsConfiguration> corsConfigMap = new HashMap<>();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		//TODO: Make configurable
		config.setAllowedOrigins(Collections.singletonList("*"));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));

//		corsConfigMap.put("/register-teacher", config);


		corsConfigMap.put("/oauth/token", config);
		endpoints.getFrameworkEndpointHandlerMapping().setCorsConfigurations(corsConfigMap);

	}
 
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//		oauthServer
		oauthServer.realm(REALM);
		oauthServer.checkTokenAccess("permitAll()");
		oauthServer.passwordEncoder(passwordEncoderII());
	}


//	@Override
	@Bean
	public PasswordEncoder   passwordEncoderII() {

		HashMap encoders = new HashMap<String, PasswordEncoder>();
//		encoders.put("argon2"  ,  new Argon2PasswordEncoder());
		encoders.put("bcrypt"  ,  new BCryptPasswordEncoder());

		return new DelegatingPasswordEncoder("bcrypt", encoders);
	}

}


