/*
 * Copyright (C) 2019 Piotr Zerynger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.itger.mavenproject1;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
