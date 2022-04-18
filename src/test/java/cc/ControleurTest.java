package cc;

import cc.modele.*;
import cc.modele.data.exceptions.EtudiantDejaDansUnGroupeException;
import cc.modele.data.Projet;
import cc.modele.data.Utilisateur;
import cc.modele.data.UtilisateurDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc

class ControleurTest {


    @Autowired
    MockMvc mvc;

    @Autowired
    DataTest dataTest;


    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    FacadeModele facadeModele;


    @BeforeEach
    public void reinitFacade(){
        facadeModele.reInitFacade();
    }


    @Test
    public void testEnregistrerUtilisateurOK1() throws Exception {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();

        UtilisateurDTO utilisateur = new UtilisateurDTO(login, password);
        ObjectMapper objectMapper = new ObjectMapper();

        mvc.perform(post(URI.create("/api/gestionprojets/utilisateurs"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(x -> {

                    Utilisateur utilisateurRecupere = objectMapper.readValue(x.getResponse().getContentAsString(),Utilisateur.class);
                    Assertions.assertEquals(login,utilisateurRecupere.getLogin());
                });
    }


    @Test
    public void testEnregistrerUtilisateurOK2() throws Exception {
        String login = dataTest.getLoginEtudiant();
        String password = dataTest.getPassword();
        UtilisateurDTO utilisateur = new UtilisateurDTO(login, password);
        ObjectMapper objectMapper = new ObjectMapper();

        mvc.perform(post(URI.create("/api/gestionprojets/utilisateurs"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(x -> {

                    Utilisateur utilisateurRecupere = objectMapper.readValue(x.getResponse().getContentAsString(),Utilisateur.class);
                    Assertions.assertEquals(login,utilisateurRecupere.getLogin());
                });
    }

    @Test
    public void testEnregistrerUtilisateurKO1() throws Exception {
        String login = dataTest.getLoginProf();
        String password = dataTest.getPassword();
        facadeModele.enregistrerUtilisateur(login,passwordEncoder.encode(password));
        UtilisateurDTO utilisateur = new UtilisateurDTO(login,password);
        ObjectMapper objectMapper = new ObjectMapper();
        mvc.perform(post(URI.create("/api/gestionprojets/utilisateurs"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isConflict());
    }



    @Test
    public void testEnregistrerUtilisateurKO2() throws Exception {
        String login = dataTest.getBadLogin();
        String password = dataTest.getPassword();
        UtilisateurDTO utilisateur = new UtilisateurDTO(login,password);
        ObjectMapper objectMapper = new ObjectMapper();
        mvc.perform(post(URI.create("/api/gestionprojets/utilisateurs"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    public void testEnregistrerUtilisateurKO3() throws Exception {
        String login = dataTest.getLoginProf();
        String password = dataTest.getEmptyPassword();
        UtilisateurDTO utilisateur = new UtilisateurDTO(login,password);
        ObjectMapper objectMapper = new ObjectMapper();
        mvc.perform(post(URI.create("/api/gestionprojets/utilisateurs"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isNotAcceptable());
    }




    @Test
    public void testGetUtilisateurProfOK() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs"))
                .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andDo(e -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Utilisateur> resultat = objectMapper.readValue(e.getResponse().getContentAsString(), List.class);
                    Assertions.assertEquals(2,resultat.size());
                });

    }


    @Test
    public void testGetUtilisateurEtudiantKO() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs"))
                        .with(httpBasic(loginEtudiant,passwordEtudiant)))
                .andExpect(status().isForbidden());

    }


    @Test
    public void testGetUtilisateurKO() throws Exception {
        String loginProfesseur = dataTest.getLoginInexistant();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs"))
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andExpect(status().isUnauthorized());

    }



    @Test
    public void testCreationProjetOK() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginProfesseur,
                passwordEncoder.encode(passwordProfesseur));

        mvc.perform(post(URI.create("/api/gestionprojets/projets"))
                        .with(httpBasic(loginProfesseur,passwordProfesseur))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("nomProjet="+dataTest.getNomProjet()+"&nbGroupes="+3))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(x -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Projet projet = objectMapper.readValue(x.getResponse().getContentAsString(),Projet.class);
                    Assertions.assertNotNull(projet);
                });



    }



    @Test
    public void testCreationProjetKO1() throws Exception {
        String loginEtudiant = dataTest.getLoginEtudiant();
        String password = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginEtudiant,
                passwordEncoder.encode(password));

        mvc.perform(post(URI.create("/api/gestionprojets/projets"))
                        .with(httpBasic(loginEtudiant,password))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("nomProjet="+dataTest.getNomProjet()+"&nbGroupes="+3))
                .andExpect(status().isForbidden());
    }


    @Test
    public void testCreationProjetKO2() throws Exception {
        String loginEtudiant = dataTest.getLoginInexistant();
        String password = dataTest.getPassword();
        mvc.perform(post(URI.create("/api/gestionprojets/projets"))
                        .with(httpBasic(loginEtudiant,password))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("nomProjet="+dataTest.getNomProjet()+"&nbGroupes="+3))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testCreationProjetKO3() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginProfesseur,
                passwordEncoder.encode(passwordProfesseur));

        mvc.perform(post(URI.create("/api/gestionprojets/projets"))
                        .with(httpBasic(loginProfesseur,passwordProfesseur))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("nomProjet="+dataTest.getNomProjet()+"&nbGroupes=-"+1))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testCreationProjetKO4() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        this.facadeModele.enregistrerUtilisateur(loginProfesseur,
                passwordEncoder.encode(passwordProfesseur));
        mvc.perform(post(URI.create("/api/gestionprojets/projets"))
                        .with(httpBasic(loginProfesseur,passwordProfesseur))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("nomProjet="+dataTest.getBadNomProjet()+"&nbGroupes=-"+3))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testGetUtilisateurByIdProfesseyrOK1() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs/"+idProf))
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andDo(e -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Utilisateur resultat = objectMapper.readValue(e.getResponse().getContentAsString(), Utilisateur.class);
                    Assertions.assertNotNull(resultat);
                });

    }

    @Test
    public void testGetUtilisateurByIdProfesseyrOK2() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs/"+idEtudiant))
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andDo(e -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Utilisateur resultat = objectMapper.readValue(e.getResponse().getContentAsString(), Utilisateur.class);
                    Assertions.assertNotNull(resultat);
                });

    }

    @Test
    public void testGetUtilisateurByIdEtudiantOK() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs/"+idEtudiant))
                        .with(httpBasic(loginEtudiant,passwordEtudiant)))
                .andDo(e -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Utilisateur resultat = objectMapper.readValue(e.getResponse().getContentAsString(), Utilisateur.class);
                    Assertions.assertNotNull(resultat);
                });

    }


    @Test
    public void testGetUtilisateurByIdEtudiantKO() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));

        String loginEtudiant = dataTest.getLoginEtudiant();
        String passwordEtudiant = dataTest.getPassword();
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordEtudiant));

        mvc.perform(get(URI.create("/api/gestionprojets/utilisateurs/"+idProf))
                        .with(httpBasic(loginEtudiant,passwordEtudiant)))
                .andExpect(status().isForbidden());

    }



    @Test
    public void testGetProjetByIdOK1() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(get(URI.create("/api/gestionprojets/projets/"+projet.getIdProjet()))
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andExpect(status().isOk());


    }


    @Test
    public void testGetProjetByIdOK2() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(get(URI.create("/api/gestionprojets/projets/"+projet.getIdProjet()))
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isOk());
    }



    @Test
    public void testGetProjetByIdKO1() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginExterieur = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(get(URI.create("/api/gestionprojets/projets/"+projet.getIdProjet()))
                        .with(httpBasic(loginExterieur,passwordProfesseur)))
                .andExpect(status().isUnauthorized());
    }




    @Test
    public void testGetGroupeByIdOK1() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();
        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(get(URI.create("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes"))
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andExpect(status().isOk());


    }


    @Test
    public void testGetGroupesByIdOK2() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(get(URI.create("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes"))
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isOk());
    }



    @Test
    public void testGetGroupesByIdKO1() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginExterieur = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(get(URI.create("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes"))
                        .with(httpBasic(loginExterieur,passwordProfesseur)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testRejoindreGroupeOK() throws Exception {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(put("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/1")
                .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isAccepted());


    }



    @Test
    public void testRejoindreGroupeKO1() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Utilisateur etudiant = this.facadeModele.getUtilisateurByIntId(idEtudiant);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);
        this.facadeModele.rejoindreGroupe(etudiant,projet.getIdProjet(),0);
        mvc.perform(put("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/1")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isConflict());


    }



    @Test
    public void testRejoindreGroupeKO2() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);

        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(put("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/4")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isNotFound());


    }







    @Test
    public void testRejoindreGroupeKO3() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);


        mvc.perform(put("/api/gestionprojets/projets/"+dataTest.getNomProjet()+"/groupes/2")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isNotFound());


    }


    @Test
    public void testRejoindreGroupeKO4() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(put("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/2")
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andExpect(status().isForbidden());


    }






    @Test
    public void testQuitterGroupeOK() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Utilisateur etudiant = this.facadeModele.getUtilisateurByIntId(idEtudiant);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);
        this.facadeModele.rejoindreGroupe(etudiant,projet.getIdProjet(),1);

        mvc.perform(delete("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/1")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isAccepted());


    }



    @Test
    public void testQuitterGroupeKO1() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Utilisateur etudiant = this.facadeModele.getUtilisateurByIntId(idEtudiant);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(delete("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/1")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isNotAcceptable());


    }



    @Test
    public void testQuitterGroupeKO2() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);

        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(delete("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/4")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isNotFound());


    }







    @Test
    public void testQuitterGroupeKO3() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);


        mvc.perform(delete("/api/gestionprojets/projets/"+dataTest.getNomProjet()+"/groupes/2")
                        .with(httpBasic(loginEtudiant,passwordProfesseur)))
                .andExpect(status().isNotFound());


    }


    @Test
    public void testQuitterGroupeKO4() throws Exception, EtudiantDejaDansUnGroupeException {
        String loginProfesseur = dataTest.getLoginProf();
        String passwordProfesseur = dataTest.getPassword();

        String loginEtudiant = dataTest.getLoginEtudiant();

        int idProf = this.facadeModele.enregistrerUtilisateur(loginProfesseur,passwordEncoder.encode(passwordProfesseur));
        int idEtudiant = this.facadeModele.enregistrerUtilisateur(loginEtudiant,passwordEncoder.encode(passwordProfesseur));
        Utilisateur u = this.facadeModele.getUtilisateurByIntId(idProf);
        Projet projet = this.facadeModele.creationProjet(u, dataTest.getNomProjet(), 3);

        mvc.perform(delete("/api/gestionprojets/projets/"+projet.getIdProjet()+"/groupes/2")
                        .with(httpBasic(loginProfesseur,passwordProfesseur)))
                .andExpect(status().isForbidden());

    }


}
