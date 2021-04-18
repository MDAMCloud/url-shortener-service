package com.cloud.urlshortenerservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloud.urlshortenerservice.exception.UserAuthenticationException;
import com.cloud.urlshortenerservice.model.UserDto;

import java.util.Base64;
import java.util.Calendar;

public class TokenVerification {

    public static final String SECRET = "URLSHORTENER";
    private static final String ERR_USER_AUTH = "The user is not logged in!";

    public static UserDto verify(String token) throws RuntimeException, UserAuthenticationException {

        if (token == null || token.length() == 0){
            return null;
        }

        DecodedJWT jwt = JWT.decode(token);

        UserDto userDto = new UserDto(  jwt.getClaims().get("userId").asString(),
                                        jwt.getClaims().get("sub").asString(),
                                        jwt.getClaims().get("accountType").asString());

        Algorithm algorithm = Algorithm.HMAC512(Base64.getDecoder().decode(SECRET));
        algorithm.verify(jwt);

        // Check expiration
        if (jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
            throw new UserAuthenticationException(ERR_USER_AUTH);
        }

        return userDto;
    }

}
