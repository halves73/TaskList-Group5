package cncs.academy.ess.controller;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import io.javalin.http.*;
import io.javalin.security.Roles;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class AuthorizationMiddleware implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationMiddleware.class);
    private final UserRepository userRepository;

    public AuthorizationMiddleware(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        // if method is OPTIONS bypass auth middleware
        if (ctx.method() == HandlerType.OPTIONS) {
            // Optionally: validate if it is a legitimate CORS preflight
            return;
        }

        // Allow unauthenticated requests to /user (register) and /login
        if (ctx.path().equals("/user") && ctx.method().name().equals("POST") ||
                ctx.path().equals("/login") && ctx.method().name().equals("POST"))
            return;

        // Check if authorization header exists
        String authorizationHeader = ctx.header("Authorization");
        String path = ctx.path();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Authorization header is missing or invalid '{}' for path '{}'", authorizationHeader, path);
            throw new UnauthorizedResponse();
        }

        // Extract token from authorization header
        String token = authorizationHeader.substring(7); // Remove "Bearer "

        // Check if token is valid (perform authentication logic)
        int userId = validateTokenAndGetUserId(token);
        if (userId == -1) {
            logger.info("Authorization token is invalid {}", token  );
            throw new UnauthorizedResponse();
        }

        checkRoles(ctx.path(), ctx.method().name(), userId);


        // Add user ID to context for use in route handlers
        ctx.attribute("userId", userId);
    }

    private void checkRoles(String obj, String act, int userId) {
        Enforcer enforcer = new Enforcer("D:\\Users\\halves\\Desktop\\Formacao-EngSoftwareSeguro\\Github\\api-todo-list-manager-group-5\\tasklist-phase2\\src\\main\\java\\cncs\\academy\\ess\\AccessControl\\model.conf", "D:\\Users\\halves\\Desktop\\Formacao-EngSoftwareSeguro\\Github\\api-todo-list-manager-group-5\\tasklist-phase2\\src\\main\\java\\cncs\\academy\\ess\\AccessControl\\policy.csv");
        User user = userRepository.findById(userId);
        String sub = user.getUsername();
        if (enforcer.enforce(sub, obj, act)) {
            // permit  to read data1
        } else {
            throw new ForbiddenResponse();
        }
    };
    /**
     * NOTE: This method currently uses username lookup as a placeholder for real token validation.
     * Replace with proper token parsing/verification (e.g., JWT, session lookup) as needed.
     */
    private Integer validateTokenAndGetUserId(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            String username = decodedJWT.getClaim("username").asString();
            if (username == null) {
                return -1;
            }
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return -1;
            }
            return user.getId();
        } catch (Exception e) {
            return -1;
        }
    }
    public static DecodedJWT verifyToken(String token) {
        String secret = "Teste";
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();
        return verifier.verify(token);
    }
}



