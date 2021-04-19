package com.cloud.urlshortenerservice.controller;

import com.cloud.urlshortenerservice.entity.Url;
import com.cloud.urlshortenerservice.exception.UserAuthenticationException;
import com.cloud.urlshortenerservice.model.TokenDto;
import com.cloud.urlshortenerservice.model.UrlDto;
import com.cloud.urlshortenerservice.model.UrlRequestDto;
import com.cloud.urlshortenerservice.model.UserDto;
import com.cloud.urlshortenerservice.service.UrlService;
import com.cloud.urlshortenerservice.util.TokenVerification;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final static String ERR_URL_NOT_VALID = "You have to enter a valid URL!";

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Url> shortenURL(@RequestBody UrlRequestDto urlRequest) throws UserAuthenticationException {

        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(urlRequest.getOriginalUrl())){
            throw new IllegalArgumentException(ERR_URL_NOT_VALID);
        }

        UserDto userDto = TokenVerification.verify(urlRequest.getToken());
        UrlDto urlDto = new UrlDto(urlRequest.getShortKey(), urlRequest.getOriginalUrl());

        // Set expiration date as 30 day after if user is not premium
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(timestamp);
        if (userDto == null || !userDto.getAccountType().equals("premium")){
            expirationDate.add(Calendar.DATE,30);
            timestamp.setTime(expirationDate.getTime().getTime());
            urlDto.setExpirationDate("" + timestamp.getTime());
        }

        Url generatedUrl = urlService.createShortUrl(urlDto, userDto);
        return new ResponseEntity<>(generatedUrl, HttpStatus.OK);
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> redirect(@PathVariable String key) throws URISyntaxException {

        Optional<Url> url = urlService.getUrlByShortenKey(key);
        if (url.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (url.get().getExpirationDate() != null && (new Timestamp(Long.parseLong(url.get().getExpirationDate()))).before(new Date())){
            urlService.removeUrlByKey(url.get().getShortenKey());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        URI uri = new URI(url.get().getOriginalUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);

        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @PostMapping("/urls")
    public ResponseEntity<List<Url>> showURLs(@RequestBody TokenDto token) throws UserAuthenticationException {

        UserDto userDto = TokenVerification.verify(token.getToken());
        return new ResponseEntity<>(urlService.getAllUrlsByUserId(userDto.getUserId(), userDto.getAccountType()), HttpStatus.OK);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Optional<Url>> deleteURL(@PathVariable String key, @RequestBody TokenDto token) throws UserAuthenticationException {

        UserDto userDto = TokenVerification.verify(token.getToken());
        return new ResponseEntity<>(urlService.removeUrlByKeyAndUserId(key, userDto.getUserId()), HttpStatus.OK);
    }

}
