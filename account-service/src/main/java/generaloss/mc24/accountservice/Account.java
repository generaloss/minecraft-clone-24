package generaloss.mc24.accountservice;

import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class Account {

    public static final String DIRECTORY = "./accounts/";

    private String nickname;
    private String password;
    private final String creation_date;

    public Account(String nickname, String password, String creation_date) {
        this.nickname = nickname;
        this.password = password;
        this.creation_date = creation_date;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getCreationDate() {
        return creation_date;
    }


    public boolean setNickname(String nickname) {
        validateNickname(nickname);

        if(Resource.external(DIRECTORY + nickname).exists())
            return false;

        final ExternalResource direcoryRes = Resource.external(DIRECTORY + nickname);
        direcoryRes.child("/nickname").writeString(nickname);
        if(!direcoryRes.file().renameTo(new File(DIRECTORY + nickname)))
            return false;

        this.nickname = nickname;
        return true;
    }

    public boolean setPassword(String password) {
        validatePassword(nickname);

        Resource.external(DIRECTORY + this.nickname + "/password").writeString(nickname);
        this.password = password;
        return true;
    }


    @Override
    public int hashCode() {
        return Objects.hash(nickname, password, creation_date);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass())
            return false;
        final Account account = (Account) o;
        return Objects.equals(nickname, account.nickname) && Objects.equals(password, account.password) && Objects.equals(creation_date, account.creation_date);
    }


    public static Account load(ExternalResource resource) {
        if(!resource.exists() || resource.isFile())
            throw new IllegalArgumentException("Account does not exist.");

        final String nickname = resource.child("/nickname").readString();
        final String password = resource.child("/password").readString();
        final String creation_date = resource.child("/creation_date").readString();
        return new Account(nickname, password, creation_date);
    }

    public static Account load(String nickname) {
        return load(Resource.external(DIRECTORY + nickname));
    }

    public static Account create(String nickname, String password) {
        validateNickname(nickname);
        validatePassword(password);

        final ExternalResource direcoryRes = Resource.external(DIRECTORY + nickname);
        if(direcoryRes.exists())
            throw new IllegalArgumentException("Nickname '" + nickname + "' is already in use.");
        direcoryRes.mkdir();

        final ExternalResource nicknameRes = Resource.external(DIRECTORY + nickname + "/nickname");
        nicknameRes.create();
        nicknameRes.writeString(nickname);

        final ExternalResource passwordRes = Resource.external(DIRECTORY + nickname + "/password");
        passwordRes.create();
        passwordRes.writeString(password);

        final String creation_date = Calendar.getInstance().getTime().toString();
        final ExternalResource creationDateRes = Resource.external(DIRECTORY + nickname + "/creation_date");
        creationDateRes.create();
        creationDateRes.writeString(creation_date);

        return new Account(nickname, password, creation_date);
    }

    public static void delete(String nickname) {
        final ExternalResource directory = Resource.external(DIRECTORY + nickname);
        Arrays.stream(directory.listResources())
            .forEach(ExternalResource::delete);
        directory.delete();
    }


    public static void validateNickname(String nickname) {
        if(nickname == null || nickname.isEmpty())
            throw new IllegalArgumentException("Nickname is empty.");
        if(nickname.length() < 2)
            throw new IllegalArgumentException("Nickname must be at least 2 characters.");
        if(!nickname.matches("^[a-zA-Z0-9_-]*$"))
            throw new IllegalArgumentException("Nickname contains invalid characters.");
    }

    public static void validatePassword(String password) {
        if(password == null || password.isEmpty())
            throw new IllegalArgumentException("Password is empty.");
        if(password.length() < 3)
            throw new IllegalArgumentException("Password must be at least 3 characters.");
        if(!password.matches("^[a-zA-Z0-9_-]*$"))
            throw new IllegalArgumentException("Password contains invalid characters.");
    }

}
