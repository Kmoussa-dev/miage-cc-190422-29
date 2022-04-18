package cc.modele.data;

import cc.modele.data.exceptions.AccessNonAutoriseAuGroupeException;
import cc.modele.data.exceptions.EtudiantDejaDansUnGroupeException;
import cc.modele.data.exceptions.EtudiantPasDansLeGroupeException;
import cc.modele.data.exceptions.MauvaisIdentifiantDeGroupeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.UUID;

public class Projet {

    private String nomProjet;
    private String idProjet;
    private Utilisateur createur;

    @JsonIgnore
    private Groupe[] groupes;

    public Projet() {
    }

    public Projet(String nomProjet, Utilisateur createur, int nbGroupes) {
        this.nomProjet = nomProjet;
        this.createur = createur;
        this.idProjet= UUID.randomUUID().toString();
        this.groupes = new Groupe[nbGroupes];
        for (int i = 0;i<nbGroupes;i++)
            this.groupes[i] = new Groupe(i,idProjet);
    }


    public Groupe getGroupe(int i) throws MauvaisIdentifiantDeGroupeException {
        if (i<0 || i>=this.groupes.length)
            throw new MauvaisIdentifiantDeGroupeException();
        return groupes[i];
    }

    public Groupe getGroupe(Utilisateur u ,int i) throws MauvaisIdentifiantDeGroupeException, AccessNonAutoriseAuGroupeException {
        if (i<0 || i>=this.groupes.length)
            throw new MauvaisIdentifiantDeGroupeException();

        if (u==this.createur || Arrays.stream(this.groupes).anyMatch(g -> g.getEtudiants().contains(u)))
            return groupes[i];
        throw new AccessNonAutoriseAuGroupeException();
    }

    public Groupe[] getGroupes() {
        return groupes;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public String getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(String idProjet) {
        this.idProjet = idProjet;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public void rejoindreGroupe(Utilisateur utilisateur, int idGroupe) throws EtudiantDejaDansUnGroupeException, MauvaisIdentifiantDeGroupeException {

        if (Arrays.stream(groupes).anyMatch(groupe -> groupe.getEtudiants().contains(utilisateur)))
            throw new EtudiantDejaDansUnGroupeException();
        if (idGroupe<0 || idGroupe>=groupes.length)
            throw new MauvaisIdentifiantDeGroupeException();

        groupes[idGroupe].getEtudiants().add(utilisateur);

    }

    public void quitterGroupe(Utilisateur utilisateur, int idGroupe) throws
            MauvaisIdentifiantDeGroupeException, EtudiantPasDansLeGroupeException {
        if (idGroupe<0 || idGroupe>=groupes.length)
            throw new MauvaisIdentifiantDeGroupeException();

        if (!groupes[idGroupe].getEtudiants().contains(utilisateur)) {
            throw new EtudiantPasDansLeGroupeException();
        }
        groupes[idGroupe].getEtudiants().remove(utilisateur);


    }
}
