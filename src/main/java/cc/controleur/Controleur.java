package cc.controleur;

import cc.modele.*;
import cc.modele.data.Projet;
import cc.modele.data.Utilisateur;
import cc.modele.data.UtilisateurDTO;
import cc.modele.data.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gestionprojets")
public class Controleur {

    private final FacadeModele facadeModele;


    public Controleur(FacadeModele facadeModele) {
        this.facadeModele = facadeModele;
    }

    @PostMapping("/utilisateurs")
    public ResponseEntity<Utilisateur> createUtilisateur(@RequestBody UtilisateurDTO utilisateurDTO){
        try {
            int  id = this.facadeModele.enregistrerUtilisateur(utilisateurDTO.getLogin(),utilisateurDTO.getPassword());
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{idUtilisateur}")
                    .buildAndExpand(id).toUri();
            return ResponseEntity.created(location).body(this.facadeModele.getUtilisateurByIntId(id));
        } catch (DonneeManquanteException | EmailMalFormeException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (EmailDejaPrisException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/utilisateurs/{idUtilisateur}")
    public ResponseEntity<Utilisateur> getProfil(@AuthenticationPrincipal User user, @PathVariable int idUtilisateur){


        try {
            Utilisateur utilisateur = this.facadeModele.getUtilisateurByEmail(user.getUsername());
            if(utilisateur.getId() == idUtilisateur)
                return ResponseEntity.ok(utilisateur);
            else if (Arrays.stream(utilisateur.getRoles()).anyMatch(r -> r.equals("PROFESSEUR")))
                return ResponseEntity.ok(this.facadeModele.getUtilisateurByIntId(idUtilisateur));
            else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<Collection<Utilisateur>> getAllUtilisateurs(){
        return ResponseEntity.ok(this.facadeModele.getAllUtilisateurs());
    }

    @PostMapping("/projets")
    public ResponseEntity<Projet> createProject(@AuthenticationPrincipal User user, @RequestParam String nomProjet, @RequestParam int nbGroupes){
        try {
            Utilisateur utilisateur = this.facadeModele.getUtilisateurByEmail(user.getUsername());
            Projet p = this.facadeModele.creationProjet(utilisateur,nomProjet,nbGroupes);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{idprojet}")
                    .buildAndExpand(p.getIdProjet()).toUri();
            return ResponseEntity.created(location).body(p);
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DonneeManquanteException | NbGroupesIncorrectException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @GetMapping("/projets/{idprojet}")
    public ResponseEntity<Projet> getProjet(@PathVariable String idprojet){
        try {
            return ResponseEntity.ok(this.facadeModele.getProjetById(idprojet));
        } catch (ProjetInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/projets/{idProjet}/groupes/{idGroupe}")
    public ResponseEntity rejoindreGroupe(@AuthenticationPrincipal User user, @PathVariable String idProjet, @PathVariable int idGroupe){
        try {
            Utilisateur utilisateur = this.facadeModele.getUtilisateurByEmail(user.getUsername());
            this.facadeModele.rejoindreGroupe(utilisateur, idProjet, idGroupe);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ProjetInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MauvaisIdentifiantDeGroupeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (EtudiantDejaDansUnGroupeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/projets/{idProjet}/groupes/{idGroupe}")
    public ResponseEntity quitterGroupe(@AuthenticationPrincipal User user, @PathVariable String idProjet, @PathVariable int idGroupe) {

        try {
            Utilisateur utilisateur = this.facadeModele.getUtilisateurByEmail(user.getUsername());
            this.facadeModele.quitterGroupe(utilisateur, idProjet, idGroupe);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ProjetInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (MauvaisIdentifiantDeGroupeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (EtudiantPasDansLeGroupeException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }
}
