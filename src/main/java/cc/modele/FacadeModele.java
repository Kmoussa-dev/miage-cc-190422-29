package cc.modele;

import cc.modele.data.*;
import cc.modele.data.exceptions.*;
import cc.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
public class FacadeModele {


    private Map<String,Utilisateur> utilisateurs;
    private Map<Integer,Utilisateur> mapUtilisateurs;

    private Map<String,Projet> projets;

    public FacadeModele(){
        utilisateurs=new HashMap<>();
        mapUtilisateurs=new HashMap<>();
        projets=new HashMap<>();
    }

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

        if (Objects.isNull(login)) throw new DonneeManquanteException();
        if (Objects.isNull(password)) throw new DonneeManquanteException();
        if (login.isBlank()) throw new DonneeManquanteException();
        if (password.isBlank()) throw new DonneeManquanteException();
        if (!EmailUtils.verifier(login)) throw new EmailMalFormeException();
        if (utilisateurs.containsKey(login)) throw new EmailDejaPrisException();
        Utilisateur utilisateur = new Utilisateur(login, password);
        utilisateurs.put(login, utilisateur);
        mapUtilisateurs.put(utilisateur.getId(), utilisateur);
        return utilisateur.getId();
    }

    /**
     * Permet de récupérer un utilisateur à partir de son identifiant int
     * @param id
     * @return utilisateur concerné
     * @throws UtilisateurInexistantException : Aucun utilisateur existe avec cet identifiant
     */
    public Utilisateur getUtilisateurByIntId(int id) throws UtilisateurInexistantException {
        Utilisateur utilisateur=mapUtilisateurs.get(id);
        if(utilisateur==null&&!mapUtilisateurs.containsKey(id)) throw new UtilisateurInexistantException();
        return utilisateur;
    }


    /**
     * Permet de récupérer un utilisateur à partir de son login
     * @param login
     * @return utilisateur concerné
     * @throws UtilisateurInexistantException : Aucun utilisateur existe avec cet email
     */
    public Utilisateur getUtilisateurByEmail(String login) throws UtilisateurInexistantException {
        Utilisateur utilisateur=utilisateurs.get(login);
        if(utilisateur==null&&!utilisateurs.containsKey(login)) throw new UtilisateurInexistantException();
        return utilisateur;
    }


    /**
     * Permet de réinitialiser la façade en vidant les structures
     * et en remettant les compteurs identifiants à 0
     */

    public void reInitFacade(){
        utilisateurs=new HashMap<>();
        mapUtilisateurs=new HashMap<>();
        Utilisateur.resetID();
    }

    /**
     * Permet de récupérer tous les utilisateurs enregistrés
     * @return
     */
    public Collection<Utilisateur> getAllUtilisateurs() {
        Collection<Utilisateur> utilisateursList = new ArrayList<>();
        BiConsumer<String,Utilisateur> action = (String login,Utilisateur utilisateur) -> {
            utilisateursList.add(utilisateur);
        };
        utilisateurs.forEach(action);
        return utilisateursList;
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
        if(nomProjet==null||nomProjet.isBlank()) throw new DonneeManquanteException();
        if (nbGroupes<0) throw new NbGroupesIncorrectException();
        Projet projet=new Projet(nomProjet,utilisateur,nbGroupes);
        projets.put(projet.getIdProjet(),projet);
        return projets.get(projet.getIdProjet());
    }

    /**
     * Permet de récupérer un projet par son identifiant s'il existe
     * @param idProjet
     * @return
     * @throws ProjetInexistantException
     */
    public Projet getProjetById(String idProjet) throws ProjetInexistantException {
        if (!projets.containsKey(idProjet)) throw new ProjetInexistantException();
       Projet projet=projets.get(idProjet);
       return projet;
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
            Projet projet=getProjetById(idProjet);
            projet.rejoindreGroupe(utilisateur,idGroupe);
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
        if (!projets.containsKey(idProjet)) throw new ProjetInexistantException();
        Projet projet=getProjetById(idProjet);
        projet.quitterGroupe(utilisateur,idGroupe);
    }


    /**
     * Permet de récupérer les groupes d'un projet existant
     * @param idProjet
     * @return
     * @throws ProjetInexistantException
     */
    public Groupe[] getGroupeByIdProjet(String idProjet) throws ProjetInexistantException {
        if (!projets.containsKey(idProjet)) throw new ProjetInexistantException();
        Projet projet=getProjetById(idProjet);
        return projet.getGroupes();
    }
}
