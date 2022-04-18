package cc;

import org.springframework.stereotype.Component;

@Component
public class DataTestImpl implements DataTest{
    @Override
    public String getLoginProf() {
        return "yohan.boichut@univ-orleans.fr";
    }

    @Override
    public String getPassword() {
        return "dummypassword";
    }

    @Override
    public String getEmptyPassword() {
        return "      ";
    }

    @Override
    public String getEmptyPasswordURL() {
        return "%20%20%20";
    }

    @Override
    public String getEmptyLogin() {
        return "         ";
    }

    @Override
    public String getBadLogin() {
        return "yohan.boichutuniv-orleans.fr";
    }

    @Override
    public String getLoginEtudiant() {
        return "etudiant.brillant@etu.univ-orleans.fr";
    }

    @Override
    public String getNomProjet() {
        return "dummynomprojet";
    }

    @Override
    public String getBadNomProjet() {
        return "         ";
    }

    @Override
    public String getNullProjet() {
        return null;
    }

    @Override
    public String getLoginInexistant() {
        return "frederic.moal@univ-orleans.fr";
    }
}
