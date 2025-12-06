package pe.edu.utp.boticaapp.config;

import pe.edu.utp.boticaapp.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final UserDetailsServiceImpl uds;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
      .headers(h -> h.frameOptions(f -> f.disable()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(
          "/", 
          "/catalogo/**", 
          "/css/**", 
          "/js/**", 
          "/img/**",
          "/images/**",
          "/styles.css",
          "/h2-console/**", 
          "/login", 
          "/register", 
          "/error"
        ).permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(login -> login
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/catalogo", true) // Cambiado: siempre redirigir al catálogo
        .failureUrl("/login?error=true")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout=true") // Cambiado: redirigir al login
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true)
        .permitAll()
      )
      .authenticationProvider(authenticationProvider());
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(uds);
    p.setPasswordEncoder(passwordEncoder());
    return p;
  }

  @Bean 
  public PasswordEncoder passwordEncoder() { 
    return new BCryptPasswordEncoder(); 
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }
}
