package cc.config;

import cc.modele.FacadeModele;
import cc.modele.data.Utilisateur;
import cc.modele.data.exceptions.UtilisateurInexistantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CostumUsers implements UserDetailsService {
    @Autowired
    FacadeModele facadeModele;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Utilisateur u = facadeModele.getUtilisateurByEmail(username);
            UserDetails userDetails = User.builder()
                    .username(u.getLogin())
                    .password(passwordEncoder.encode(u.getPassword()))
                    .roles(u.getRoles())
                    .build();
            return userDetails;
        } catch (UtilisateurInexistantException e) {
            throw new UsernameNotFoundException("username  " +username );
        }
    }

}
