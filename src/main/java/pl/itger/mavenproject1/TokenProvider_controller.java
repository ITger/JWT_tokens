/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.itger.mavenproject1;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author new
 */
@RestController
public class TokenProvider_controller {

    @GetMapping(path = "/")
    public String get() {
        Date expirationDate = DateUtils.addHours(new Date(), 1);
        String jwt = "Jwts.builder FAILED";
        try {
            jwt = Jwts.builder()
                    .setIssuer("http://itger.pl/")
                    .setSubject("users/Janusz i Grażyna")
                    .setAudience("something")
                    .setExpiration(expirationDate)
                    .claim("name", "Janusz i Grażyna Nosacz")
                    .claim("scope", "self groups/admins")
                    .signWith(
                            SignatureAlgorithm.HS256,
                            "secretPassword".getBytes("UTF-8")
                    )
                    .compact();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TokenProvider_controller.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return jwt;
    }

    @GetMapping("/verify/{token}")
    public @ResponseBody
    ResponseEntity<String>
            verify(@PathVariable String token) {
        String subject = "Something Wrong! ";
        System.out.println(token);
        try {
            Jws<Claims> jwtClaims = Jwts
                    .parser()
                    .setSigningKey("secretPassword".getBytes("UTF-8"))
                    .parseClaimsJws(token);
            subject = jwtClaims
                    .getBody()
                    .getSubject();
            System.out.println(subject);
        } catch (SignatureException | UnsupportedEncodingException ex) {
            subject = subject.concat(ex.getLocalizedMessage());
            Logger.getLogger(TokenProvider_controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ResponseEntity<>("GET Response : "
                + subject, HttpStatus.OK);
    }
}
