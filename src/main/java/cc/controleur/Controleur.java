package cc.controleur;


import cc.modele.FacadeModele;
import cc.modele.data.Groupe;
import cc.modele.data.Projet;
import cc.modele.data.Utilisateur;
import cc.modele.data.UtilisateurDTO;
import cc.modele.data.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/gestionprojets")
public class Controleur {


    @Autowired
    FacadeModele facadeModele;


    @PostMapping("/utilisateurs")
    public ResponseEntity<Utilisateur> creerUtilisateur(@RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            int id = facadeModele.enregistrerUtilisateur(utilisateurDTO.getLogin(), utilisateurDTO.getPassword());
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{idUtilisateur}").buildAndExpand(id).toUri();

            Utilisateur utilisateur = facadeModele.getUtilisateurByIntId(id);

            return ResponseEntity.created(location).body(utilisateur);
        } catch (DonneeManquanteException | EmailMalFormeException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } catch (EmailDejaPrisException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/utilisateurs/{idUtilisateur}")
    public ResponseEntity<Utilisateur> obtenirUtilisateur(Principal principal, @PathVariable int idUtilisateur) {
        try {
            String email = principal.getName();
            Utilisateur utilisateurAuthentifie = facadeModele.getUtilisateurByEmail(email);
            Utilisateur utilisateurQuiDemandeLaRessource = facadeModele.getUtilisateurByIntId(idUtilisateur);
            String[] rolesQuiDemandeLaRessource= utilisateurQuiDemandeLaRessource.getRoles();
            String[] rolesAuthentifie= utilisateurAuthentifie.getRoles();
            if (utilisateurAuthentifie.getId() == utilisateurQuiDemandeLaRessource.getId()) {
                return ResponseEntity.ok().body(utilisateurQuiDemandeLaRessource);
            }
            else if (Arrays.stream(rolesAuthentifie).anyMatch(roles->roles.equals("PROFESSEUR"))&&Arrays.stream(rolesQuiDemandeLaRessource).anyMatch(roles->roles.equals("ETUDIANT"))){
                return ResponseEntity.ok().body(utilisateurQuiDemandeLaRessource);
            }
            return ResponseEntity.status(403).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<Collection<Utilisateur>> obtenirUtilisateurs() {
        return ResponseEntity.ok().body(facadeModele.getAllUtilisateurs());
    }

    @PostMapping("/projets")
    public ResponseEntity<Projet> creerProjet(Principal principal, @RequestParam String nomProjet, @RequestParam int nbGroupes) {
        try {
            String email = principal.getName();
            Utilisateur utilisateurAuthentifie = facadeModele.getUtilisateurByEmail(email);
            Projet projet = facadeModele.creationProjet(utilisateurAuthentifie, nomProjet, nbGroupes);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{idprojet}").buildAndExpand(projet.getIdProjet()).toUri();
            return ResponseEntity.created(location).body(projet);
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(404).build();
        } catch (DonneeManquanteException e) {
            return ResponseEntity.status(406).build();
        } catch (NbGroupesIncorrectException e) {
            return ResponseEntity.status(406).build();
        }
    }

    @GetMapping("/projets/{idprojet}/groupes")
    public ResponseEntity<Groupe[]> getGroupe(@PathVariable String idprojet) {
        try {
            Groupe[] groupes = facadeModele.getGroupeByIdProjet(idprojet);
            return ResponseEntity.ok().body(groupes);
        } catch (ProjetInexistantException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/projets/{idprojet}")
    public ResponseEntity<Projet> obtenirProjet(@PathVariable String idprojet) {
        try {
            Projet projet = facadeModele.getProjetById(idprojet);
            return ResponseEntity.ok().body(projet);
        } catch (ProjetInexistantException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/projets/{idprojet}/groupes/{idGroupe}")
    public ResponseEntity majProjet(Principal principal, @PathVariable String idprojet, @PathVariable int idGroupe) {
        try {
            String email = principal.getName();
            Utilisateur utilisateurAuthentifie = facadeModele.getUtilisateurByEmail(email);
            facadeModele.rejoindreGroupe(utilisateurAuthentifie, idprojet, idGroupe);
            return ResponseEntity.accepted().build();
        } catch (ProjetInexistantException e) {
            return ResponseEntity.notFound().build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.notFound().build();
        } catch (MauvaisIdentifiantDeGroupeException e) {
            return ResponseEntity.notFound().build();
        } catch (EtudiantDejaDansUnGroupeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    @DeleteMapping("/projets/{idprojet}/groupes/{idGroupe}")
    public ResponseEntity deleteProjet(Principal principal, @PathVariable String idprojet, @PathVariable int idGroupe) {
        try {
            String email = principal.getName();
            Utilisateur utilisateurAuthentifie = facadeModele.getUtilisateurByEmail(email);
            facadeModele.quitterGroupe(utilisateurAuthentifie, idprojet, idGroupe);
            return ResponseEntity.accepted().build();
        } catch (ProjetInexistantException e) {
            return ResponseEntity.notFound().build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.notFound().build();
        } catch (MauvaisIdentifiantDeGroupeException e) {
            return ResponseEntity.notFound().build();
        } catch (EtudiantPasDansLeGroupeException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

    }
}
