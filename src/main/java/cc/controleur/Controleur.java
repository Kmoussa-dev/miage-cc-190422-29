package cc.controleur;


import cc.modele.FacadeModele;
import cc.modele.data.Projet;
import cc.modele.data.Utilisateur;
import cc.modele.data.UtilisateurDTO;
import cc.modele.data.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/api/gestionprojets")
public class Controleur {


    @Autowired
    FacadeModele facadeModele;



    @PostMapping( "/utilisateur")
    public ResponseEntity<Utilisateur> creerUtilisateur(@RequestBody UtilisateurDTO utilisateurDTO){
        try {
            int id = facadeModele.enregistrerUtilisateur(utilisateurDTO.getLogin(),utilisateurDTO.getPassword());
            URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

            Utilisateur utilisateur=facadeModele.getUtilisateurByIntId(id);

            return  ResponseEntity.created(location).body(utilisateur);
        } catch (DonneeManquanteException e) {
            return  ResponseEntity.status(406).build();
        } catch (EmailDejaPrisException e) {
            return  ResponseEntity.status(409).build();
        } catch (EmailMalFormeException e) {
            return  ResponseEntity.status(406).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(404).build();
        }
    }
    @GetMapping("/utilisateurs/{idUtilisateur}")
    public ResponseEntity<Utilisateur> obtenirUtilisateur(Principal principal,@PathVariable int idUtilisateur){
        try {
            String email= principal.getName();
            Utilisateur utilisateurAuthentifie=facadeModele.getUtilisateurByEmail(email);
            Utilisateur utilisateurQuiDemandeLaRessource=facadeModele.getUtilisateurByIntId(idUtilisateur);
            if (utilisateurAuthentifie.getId()!=utilisateurQuiDemandeLaRessource.getId()){
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok().body(utilisateurQuiDemandeLaRessource);
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<Collection<Utilisateur>> obtenirUtilisateurs(Principal principal){

        try {
            String email= principal.getName();
            Utilisateur utilisateurAuthentifie=facadeModele.getUtilisateurByEmail(email);
            String[] roles= utilisateurAuthentifie.getRoles();
            if(roles[0]!="PROFESSEUR") return ResponseEntity.status(403).build();
            return ResponseEntity.ok().body(facadeModele.getAllUtilisateurs());
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/projets")
    public ResponseEntity<Projet> creerProjet(Principal principal,@RequestParam String nomProjet,@RequestParam int nbGroupe){
        try {
            String email= principal.getName();
            Utilisateur utilisateurAuthentifie=facadeModele.getUtilisateurByEmail(email);
            String[] roles= utilisateurAuthentifie.getRoles();
            if (roles[0]!="PROFESSEUR") return ResponseEntity.status(401).build();
            Projet projet = facadeModele.creationProjet(utilisateurAuthentifie, nomProjet, nbGroupe);
            URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(projet.getIdProjet()).toUri();
            return  ResponseEntity.created(location).body(projet);
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(404).build();
        } catch (DonneeManquanteException e) {
            return  ResponseEntity.status(406).build();
        } catch (NbGroupesIncorrectException e) {
            return  ResponseEntity.status(406).build();
        }
    }


}
