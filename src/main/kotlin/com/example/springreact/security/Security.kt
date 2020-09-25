package com.example.springreact.security

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.GenericFilterBean
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
class SecurityConfig(val userDetailsService: UserDetailsServiceImpl) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
//        http.csrf().disable().cors().and().authorizeRequests().anyRequest().permitAll()
        http.csrf().disable().cors().and().authorizeRequests()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .anyRequest().authenticated()
            .and()
            // Filter for the api/login requests
            .addFilterBefore(
                LoginFilter("/login", authenticationManager()),
                UsernamePasswordAuthenticationFilter::class.java
            )
            // Filter for other requests to check JWT in header
            .addFilterBefore(AuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun corsConfigurationSource() : CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        config.exposedHeaders = listOf("Authorization")
        config.applyPermitDefaultValues()

        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(BCryptPasswordEncoder())
    }

    //    override fun configure(http: HttpSecurity) {
//        super.configure(http)
//    }

    // for demo and development
//    override fun userDetailsService(): UserDetailsService {
//        val user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build()
//        return InMemoryUserDetailsManager(user)
}

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var userName: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var role: String
)

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByUserName(userName: String): User
}

@Service
class UserDetailsServiceImpl(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(userName: String): UserDetails {
        val currentUser = userRepository.findByUserName(userName)
        return org.springframework.security.core.userdetails.User(
            userName,
            currentUser.password,
            true,
            true,
            true,
            true,
            AuthorityUtils.createAuthorityList(currentUser.role)
        )
    }
}

class AuthenticationService {
    companion object Constants {
        const val EXPIRATION_TIME: Long = 864_00_00
        const val SIGNING_KEY: String = "SecretKey"
        const val PREFIX: String = "Bearer"
    }

    fun addToken(res: HttpServletResponse, userName: String) {
        val jwtToken = Jwts.builder()
            .setSubject(userName)
            .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
            .compact()
        res.addHeader(HttpHeaders.AUTHORIZATION, "$PREFIX $jwtToken")
        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.AUTHORIZATION)
    }

    fun retrieveAuthentication(req: HttpServletRequest): Authentication? {
        val token = req.getHeader(HttpHeaders.AUTHORIZATION)
        var authenticationToken: Authentication? = null
        if (token != null) {
            val user = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token.replace(PREFIX, ""))
                .body
                .subject

            if (user != null) {
                authenticationToken = UsernamePasswordAuthenticationToken(user, null, emptyList())
            }
        }
        return authenticationToken
    }
}

data class AccountCredentials @JsonCreator constructor(
    @JsonProperty("username") var userName: String,
    @JsonProperty("password") var password: String
)

class LoginFilter(url: String, authManager: AuthenticationManager) :
    AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(url)) {

    init {
        authenticationManager = authManager
    }

    override fun attemptAuthentication(req: HttpServletRequest, res: HttpServletResponse): Authentication {
        val credentials = ObjectMapper()
            .readValue<AccountCredentials>(req.inputStream)
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                credentials.userName,
                credentials.password,
                emptyList()
            )
        )
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        AuthenticationService().addToken(response, authResult.name)
    }
}

class AuthenticationFilter : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val authentication = AuthenticationService().retrieveAuthentication(request as HttpServletRequest)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }
}
