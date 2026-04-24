package hr.algebra.voyabackend.security;

import hr.algebra.voyabackend.model.User;
import hr.algebra.voyabackend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;


// *  JwtAuthenticationFilter class is called for each HTTP request by extending OncePerRequestFilter.
// *  Will run only once per request. For each request, it will check if the request contains a valid JWT token.
// *  Unless the User is already authenticated.

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtilities jwtUtilities;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // if there is no authorization header, send the request to the next filter in the chain
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract token and email
        String token = authorizationHeader.substring(7); // skip "Bearer "
        String email = jwtUtilities.extractEmail(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // the user we will authenticate
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
            boolean tokenValid = jwtUtilities.isTokenValid(token, email);

            if (user != null && tokenValid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                // final authentication within Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // the authenticated user goes to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
