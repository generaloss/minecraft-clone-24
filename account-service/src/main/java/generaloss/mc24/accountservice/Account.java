package generaloss.mc24.accountservice;

import jpize.util.res.FileResource;
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

        if(Resource.file(DIRECTORY + nickname).exists())
            return false;

        final FileResource direcoryRes = Resource.file(DIRECTORY + nickname);
        direcoryRes.child("/nickname").writeString(nickname);
        if(!direcoryRes.file().renameTo(new File(DIRECTORY + nickname)))
            return false;

        this.nickname = nickname;
        return true;
    }

    public boolean setPassword(String password) {
        validatePassword(nickname);

        Resource.file(DIRECTORY + this.nickname + "/password").writeString(nickname);
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


    public static Account load(FileResource resource) {
        if(!resource.exists() || resource.isFile())
            throw new IllegalArgumentException("Account does not exist.");

        final String nickname = resource.child("/nickname").readString();
        final String password = resource.child("/password").readString();
        final String creation_date = resource.child("/creation_date").readString();
        return new Account(nickname, password, creation_date);
    }

    public static Account load(String nickname) {
        return load(Resource.file(DIRECTORY + nickname));
    }

    public static Account create(String nickname, String password) {
        validateNickname(nickname);
        validatePassword(password);

        final FileResource direcoryRes = Resource.file(DIRECTORY + nickname);
        if(direcoryRes.exists())
            throw new IllegalArgumentException("Nickname '" + nickname + "' is already in use.");
        direcoryRes.mkdir();

        final FileResource nicknameRes = Resource.file(DIRECTORY + nickname + "/nickname");
        nicknameRes.create();
        nicknameRes.writeString(nickname);

        final FileResource passwordRes = Resource.file(DIRECTORY + nickname + "/password");
        passwordRes.create();
        passwordRes.writeString(password);

        final String creation_date = Calendar.getInstance().getTime().toString();
        final FileResource creationDateRes = Resource.file(DIRECTORY + nickname + "/creation_date");
        creationDateRes.create();
        creationDateRes.writeString(creation_date);

        return new Account(nickname, password, creation_date);
    }

    public static void delete(String nickname) {
        final FileResource directory = Resource.file(DIRECTORY + nickname);
        Arrays.stream(directory.listResources())
            .forEach(FileResource::delete);
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
