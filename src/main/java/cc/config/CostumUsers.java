package cc.config;

import cc.modele.FacadeModele;
import cc.modele.data.Utilisateur;
import cc.modele.data.exceptions.UtilisateurInexistantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class CostumUsers implements UserDetailsService {
    @Autowired
    FacadeModele facadeModele;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Utilisateur u = facadeModele.getUtilisateurByEmail(username);
            UserDetails userDetails = User.builder()
                    .username(u.getLogin())
                    .password(u.getPassword())
                    .roles(u.getRoles())
                    .build();
            return userDetails;
        } catch (UtilisateurInexistantException e) {
            throw new UsernameNotFoundException("username  " +username );
        }
    }

}
