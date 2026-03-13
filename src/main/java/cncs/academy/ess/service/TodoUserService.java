package cncs.academy.ess.service;

import cncs.academy.ess.PBKDF2.SecurePassword;
import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import static cncs.academy.ess.PBKDF2.SecurePassword.hashLoginPasswd;

public class TodoUserService {
    private final UserRepository repository;

    public TodoUserService(UserRepository userRepository) {
        this.repository = userRepository;
    }
    public User addUser(String username, String password) throws Exception {
        String secPasswd = SecurePassword.hashNewPasswd(password);
//        User user = new User(username, password);
        User user = new User(username, secPasswd);
        int id = repository.save(user);
        user.setId(id);
        return user;
    }
    public User getUser(int id) {
        return repository.findById(id);
    }

    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    public String login(String username, String password) throws Exception {
        User user = repository.findByUsername(username);
        boolean checkPasswd = hashLoginPasswd(password,user.getPassword());
        if (user == null) {
            return null;
        }
//        if (user.getPassword().equals(password)) {
//            return createAuthToken(user);
//        }
        if(checkPasswd){
            return createAuthToken(user);
        }
        return null;
    }

    private String createAuthToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("Teste");
            return "Bearer " + JWT.create()
                    .withIssuer("auth0")
                    .withClaim("username", user.getUsername())
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new RuntimeException("Erro ao criar JWT", exception);
        }
//        return "Bearer " + user.getUsername();

    }
}
