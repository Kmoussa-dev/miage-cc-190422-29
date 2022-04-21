package cc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new CostumUsers();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/gestionprojets/utilisateur").permitAll()
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/utilisateurs/*").authenticated()
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/utilisateurs").hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.POST,"/api/gestionprojets/projets").hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/projets/*").authenticated()
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/projets/*/groupes").authenticated()
                .antMatchers(HttpMethod.PUT,"/api/projets/*/groupes/*").hasRole("ETUDIANT")
                .antMatchers(HttpMethod.DELETE,"/api/projets/*/groupes/*").hasRole("ETUDIANT")
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
