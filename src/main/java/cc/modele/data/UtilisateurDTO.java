package cc.modele.data;

public class UtilisateurDTO {


    String login;
    String password;

    public UtilisateurDTO() {
    }

    public UtilisateurDTO(String login, String password) {
        this.login = login;
        this.password = password;
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
}
