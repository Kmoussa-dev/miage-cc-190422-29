package cc.modele;

import cc.modele.data.*;
import cc.modele.data.exceptions.*;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class FacadeModele {

    /**
     * Permet d'enregistrer un utilisateur
     * @param login : login de l'utilisateur (email)
     * @param password : password chiffré de l'utilisateur
     * @return identifiant int de l'utilisateur
     * @throws DonneeManquanteException : une des données est manquante
     * @throws EmailDejaPrisException : l'email donné est déjà pris
     * @throws EmailMalFormeException : l'email donné n'est pas de la bonne forme
     */

    public int enregistrerUtilisateur(String login, String password)
            throws DonneeManquanteException, EmailDejaPrisException, EmailMalFormeException {
        return 0;
    }

    /**
     * Permet de récupérer un utilisateur à partir de son identifiant int
     * @param id
     * @return utilisateur concerné
     * @throws UtilisateurInexistantException : Aucun utilisateur existe avec cet identifiant
     */
    public Utilisateur getUtilisateurByIntId(int id) throws UtilisateurInexistantException {
        return null;
    }


    /**
     * Permet de récupérer un utilisateur à partir de son login
     * @param login
     * @return utilisateur concerné
     * @throws UtilisateurInexistantException : Aucun utilisateur existe avec cet email
     */
    public Utilisateur getUtilisateurByEmail(String login) throws UtilisateurInexistantException {
        return null;

    }


    /**
     * Permet de réinitialiser la façade en vidant les structures
     * et en remettant les compteurs identifiants à 0
     */

    public void reInitFacade(){
    }

    /**
     * Permet de récupérer tous les utilisateurs enregistrés
     * @return
     */
    public Collection<Utilisateur> getAllUtilisateurs() {

        return null;
    }

    /**
     * Permet à un utilisateur (un professeur) de créer un projet
     * @param utilisateur : le contrôleur devra s'assurer qu'il s'agit d'un professeur
     * @param nomProjet : le nom du projet ne doit pas être nul ou vide
     * @param nbGroupes : le nombre de groupes doit être > 0
     * @return le projet créé
     * @throws DonneeManquanteException : le nom du projet est incorrect
     * @throws NbGroupesIncorrectException : le nombre de groupes n'est pas > 0
     */
    public Projet creationProjet(Utilisateur utilisateur,String nomProjet, int nbGroupes) throws DonneeManquanteException, NbGroupesIncorrectException {
        return null;
    }

    /**
     * Permet de récupérer un projet par son identifiant s'il existe
     * @param idProjet
     * @return
     * @throws ProjetInexistantException
     */
    public Projet getProjetById(String idProjet) throws ProjetInexistantException {
        return null;
    }


    /**
     * Permet à utilisateur (normalement un étudiant si springboot fait bien son job)
     * de rejoindre un groupe dans un projet s'il n'est pas déjà inscrit dans un autre
     * groupe du même projet
     * @param utilisateur
     * @param idProjet
     * @param idGroupe
     * @throws ProjetInexistantException
     * @throws MauvaisIdentifiantDeGroupeException
     * @throws EtudiantDejaDansUnGroupeException
     */
    public void rejoindreGroupe(Utilisateur utilisateur, String idProjet,int idGroupe) throws ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {

    }

    /**
     * Permet à utilisateur (normalement un étudiant si springboot fait bien son job)
     * de quitter un groupe dans un projet s'il est bien membre du groupe
     * @param utilisateur
     * @param idProjet
     * @param idGroupe
     * @throws ProjetInexistantException
     * @throws MauvaisIdentifiantDeGroupeException
     * @throws EtudiantPasDansLeGroupeException
     */
    public void quitterGroupe(Utilisateur utilisateur, String idProjet, int idGroupe) throws ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantPasDansLeGroupeException {

    }


    /**
     * Permet de récupérer les groupes d'un projet existant
     * @param idProjet
     * @return
     * @throws ProjetInexistantException
     */
    public Groupe[] getGroupeByIdProjet(String idProjet) throws ProjetInexistantException {
        return new Groupe[0];
    }
}
