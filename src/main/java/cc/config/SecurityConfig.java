package cc.config;

import cc.modele.FacadeModele;
import cc.modele.data.Utilisateur;
import cc.modele.data.exceptions.UtilisateurInexistantException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final FacadeModele facadeModele;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(FacadeModele facadeModele, PasswordEncoder passwordEncoder) {
        this.facadeModele = facadeModele;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return login -> {
            try {
                Utilisateur utilisateur = this.facadeModele.getUtilisateurByEmail(login);
                UserDetails userDetails = User.builder()
                        .username(utilisateur.getLogin())
                        .password(utilisateur.getPassword())
                        .roles(utilisateur.getRoles())
                        .build();
                return userDetails;
            } catch (UtilisateurInexistantException e) {
                throw new UsernameNotFoundException("Utilisateur " + login + " non existant dans la base de données");
            }


        };
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/gestionprojets/utilisateurs").permitAll()
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/utilisateurs").hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.POST,"/api/gestionprojets/projets/**" ).hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.PUT,"/api/gestionprojets/projets/{idProjet}/groupes/{idGroupe}" ).hasRole("ETUDIANT")
                .antMatchers(HttpMethod.GET,"/api/gestionprojets/projets/{idProjet}/groupes/{idGroupe}").hasRole("ETUDIANT")
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
