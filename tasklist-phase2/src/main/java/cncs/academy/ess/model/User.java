package cncs.academy.ess.model;

public class User {
    private int id;
    private String username;
    private String password;
//    private byte[] salt;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
//        this.salt = salt;
    }
    public void setId(int id) {
        this.id = id;
    }
//    public void setSalt(byte[] salt) {
//        this.salt = salt;
//    }
//    public byte[] getSalt() {
//        return salt;
//    }
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}

