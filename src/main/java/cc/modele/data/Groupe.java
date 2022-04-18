package cc.modele.data;

import java.util.ArrayList;
import java.util.List;

public class Groupe {
    private int idGroupe;
    private String idProjet;
    private List<Utilisateur> etudiants;

    public Groupe(int idGroupe,String idProjet) {
        this.idGroupe = idGroupe;
        this.idProjet = idProjet;
        this.etudiants = new ArrayList<>();
    }

    public String getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(String idProjet) {
        this.idProjet = idProjet;
    }

    public int getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(int idGroupe) {
        this.idGroupe = idGroupe;
    }

    public List<Utilisateur> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<Utilisateur> etudiants) {
        this.etudiants = etudiants;
    }
}
