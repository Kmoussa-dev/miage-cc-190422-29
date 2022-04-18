package cc.modele.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Utilisateur {
    private String login;

    @JsonIgnore
    private String password;

    private String[] roles;
    private int id;
    private static int ID = 0;

    public Utilisateur() {
    }

    public Utilisateur(String login, String password) {
        this.login = login;
        this.password = password;
        String s = (this.login.split("@"))[1];
        switch (s){
            case "etu.univ-orleans.fr":{
                roles= new String[]{"ETUDIANT"};
                break;
            }
            case "univ-orleans.fr" : {
                roles= new String[]{"PROFESSEUR"};
                break;
            }
            default: {
                roles= new String[0];
            }
        }
        this.id = ID;
        ID++;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void resetID(){
        ID=0;
    }
}
