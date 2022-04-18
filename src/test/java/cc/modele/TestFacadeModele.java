package cc.modele;

import cc.DataTest;
import cc.DataTestImpl;

import cc.modele.data.*;
import cc.modele.data.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

public class TestFacadeModele {


    private FacadeModele instance;
    private DataTest dataTest;

    @BeforeEach
    public void initialisation(){
        this.instance = new FacadeModele();
        this.dataTest = new DataTestImpl();
    }


    @Test
    void testEnregistrerUtilisateurOK1() throws DonneeManquanteException, EmailDejaPrisException, EmailMalFormeException {
        String login = this.dataTest.getLoginProf();
        String password = this.dataTest.getPassword();
        int id = this.instance.enregistrerUtilisateur(login,password);
        Assertions.assertNotNull(id);
    }


    @Test
    void testEnregistrerUtilisateurOK2() throws DonneeManquanteException, EmailDejaPrisException, EmailMalFormeException {
        String login=this.dataTest.getLoginEtudiant();
        String password = this.dataTest.getPassword();
        int id = this.instance.enregistrerUtilisateur(login,password);
        Assertions.assertNotNull(id);
    }


    @Test
    void testEnregistrerUtilisateurKO1()  {
        String login=this.dataTest.getLoginProf();
        String password = this.dataTest.getEmptyPassword();
        Assertions.assertThrows(DonneeManquanteException.class,() ->
                this.instance.enregistrerUtilisateur(login,password));
    }


    @Test
    void testEnregistrerUtilisateurKO2()  {
        String login=this.dataTest.getEmptyLogin();
        String password = this.dataTest.getPassword();
        Assertions.assertThrows(DonneeManquanteException.class,
                () -> this.instance.enregistrerUtilisateur(login,password));
    }

    @Test
    void testEnregistrerUtilisateurKO3()  {
        String login=this.dataTest.getBadLogin();
        String password = this.dataTest.getPassword();
        Assertions.assertThrows(EmailMalFormeException.class,
                () -> this.instance.enregistrerUtilisateur(login,password));
    }


    @Test
    public void testGetUtilisateurByEmailOK() throws DonneeManquanteException, EmailDejaPrisException, EmailMalFormeException, UtilisateurInexistantException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        int id =this.instance.enregistrerUtilisateur(login,password);
        Utilisateur utilisateur = this.instance.getUtilisateurByEmail(login);
        Assertions.assertNotNull(utilisateur);
    }


    @Test
    public void testGetUtilisateurByEmailKO() throws UtilisateurInexistantException {
        String login = dataTest.getLoginProf();
        Assertions.assertThrows(UtilisateurInexistantException.class,() ->this.instance.getUtilisateurByEmail(login));
    }



    @Test
    public void testGetUtilisateurByIdOK() throws DonneeManquanteException, EmailDejaPrisException, EmailMalFormeException, UtilisateurInexistantException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        int id =this.instance.enregistrerUtilisateur(login,password);
        Utilisateur utilisateur = this.instance.getUtilisateurByIntId(id);
        Assertions.assertNotNull(utilisateur);
    }


    @Test
    public void testGetUtilisateurByIdKO() throws UtilisateurInexistantException {
        Assertions.assertThrows(UtilisateurInexistantException.class,() ->this.instance.getUtilisateurByIntId(0));
    }


    @Test
    public void testCreationProjetOK() throws DonneeManquanteException, NbGroupesIncorrectException {

        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);
        Assertions.assertNotNull(projet);
    }


    @Test
    public void testCreationProjetKO1() throws DonneeManquanteException, NbGroupesIncorrectException {

        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        String nomProjet = dataTest.getBadNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertThrows(DonneeManquanteException.class,() ->this.instance.creationProjet(utilisateur.get(),nomProjet,3));
    }


    @Test
    public void testCreationProjetKO3() throws DonneeManquanteException, NbGroupesIncorrectException {

        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        String nomProjet = dataTest.getNullProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertThrows(DonneeManquanteException.class,() ->this.instance.creationProjet(utilisateur.get(),nomProjet,3));
    }

    @Test
    public void testCreationProjetKO2() throws DonneeManquanteException, NbGroupesIncorrectException {

        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertThrows(NbGroupesIncorrectException.class,() ->this.instance.creationProjet(utilisateur.get(),nomProjet,-1));
    }


    @Test
    public void testRejoindreGroupeOK() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        Assertions.assertDoesNotThrow(() ->this.instance.rejoindreGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),1));
    }

    @Test
    public void testRejoindreGroupeKO1() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        this.instance.rejoindreGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),1);

        Assertions.assertThrows(EtudiantDejaDansUnGroupeException.class,()->this.instance.rejoindreGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),0));
    }



    @Test
    public void testRejoindreGroupeKO2() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        Assertions.assertThrows(MauvaisIdentifiantDeGroupeException.class,()->this.instance.rejoindreGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),5));
    }


    @Test
    public void testRejoindreGroupeKO3() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Assertions.assertThrows(ProjetInexistantException.class,()->this.instance.rejoindreGroupe(utilisateurEtudiant.get(), dataTest.getNomProjet(), 2));
    }




    @Test
    public void testQuitterGroupeKO1() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        Assertions.assertThrows(EtudiantPasDansLeGroupeException.class,() ->this.instance.quitterGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),1));
    }

    @Test
    public void testQuitterGroupeOK() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        this.instance.rejoindreGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),1);

        Assertions.assertDoesNotThrow(()->this.instance.quitterGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),1));
    }



    @Test
    public void testQuitterGroupeKO2() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        Assertions.assertThrows(MauvaisIdentifiantDeGroupeException.class,()->this.instance.quitterGroupe(utilisateurEtudiant.get(),projet.getIdProjet(),5));
    }


    @Test
    public void testQuitterGroupeKO3() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException, MauvaisIdentifiantDeGroupeException, EtudiantDejaDansUnGroupeException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Assertions.assertThrows(ProjetInexistantException.class,()->this.instance.quitterGroupe(utilisateurEtudiant.get(), dataTest.getNomProjet(), 2));
    }


    @Test
    public void testGetProjetByIdOK() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        Assertions.assertEquals(projet,instance.getProjetById(projet.getIdProjet()));

    }



    @Test
    public void testGetProjetByIdKO() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();

        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Assertions.assertThrows(ProjetInexistantException.class,()->instance.getProjetById(dataTest.getNomProjet()));

    }



    @Test
    public void testGetGroupesProjetByIdOK() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        String nomProjet = dataTest.getNomProjet();
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Projet projet= this.instance.creationProjet(utilisateur.get(),nomProjet,3);

        Assertions.assertEquals(projet.getGroupes(),instance.getGroupeByIdProjet(projet.getIdProjet()));

    }



    @Test
    public void testGetGroupesProjetByIdKO() throws DonneeManquanteException, NbGroupesIncorrectException, ProjetInexistantException {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();

        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(login,password));
        AtomicReference<Utilisateur> utilisateur =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateur.set(instance.getUtilisateurByEmail(login)));
        Assertions.assertDoesNotThrow(()->this.instance.enregistrerUtilisateur(loginEtudiant,passwordEtudiant));
        AtomicReference<Utilisateur> utilisateurEtudiant =new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> utilisateurEtudiant.set(instance.getUtilisateurByEmail(loginEtudiant)));

        Assertions.assertThrows(ProjetInexistantException.class,()->instance.getGroupeByIdProjet(dataTest.getNomProjet()));

    }



}




