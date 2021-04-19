package com.cloud.urlshortenerservice.controller;

import com.cloud.urlshortenerservice.entity.Url;
import com.cloud.urlshortenerservice.model.TokenDto;
import com.cloud.urlshortenerservice.model.UrlDto;
import com.cloud.urlshortenerservice.model.UrlRequestDto;
import com.cloud.urlshortenerservice.model.UserDto;
import com.cloud.urlshortenerservice.service.UrlService;
import com.cloud.urlshortenerservice.util.AppResponse;
import com.cloud.urlshortenerservice.util.AppResponses;
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
    private static final String ERR_USER_AUTH = "The user is not logged in!";

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public AppResponse<Url> shortenURL(@RequestBody UrlRequestDto urlRequest) {

        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(urlRequest.getOriginalUrl())){
            return AppResponses.failure(ERR_URL_NOT_VALID);
        }

        UserDto userDto;
        if (urlRequest.getToken() == null || urlRequest.getToken().length() == 0){
            userDto = null;
        } else {
            userDto = TokenVerification.verify(urlRequest.getToken());
            if (userDto == null){
                return AppResponses.failure(ERR_USER_AUTH);
            }
        }

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
        return AppResponses.from(generatedUrl);
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
    public AppResponse<List<Url>> showURLs(@RequestBody TokenDto token) {

        UserDto userDto = TokenVerification.verify(token.getToken());
        if (userDto == null){
            return AppResponses.failure(ERR_USER_AUTH);
        }
        return AppResponses.from(urlService.getAllUrlsByUserId(userDto.getUserId(), userDto.getAccountType()));
    }

    @DeleteMapping("/{key}")
    public AppResponse<Optional<Url>> deleteURL(@PathVariable String key, @RequestBody TokenDto token) {

        UserDto userDto = TokenVerification.verify(token.getToken());
        if (userDto == null){
            return AppResponses.failure(ERR_USER_AUTH);
        }
        return AppResponses.from(urlService.removeUrlByKeyAndUserId(key, userDto.getUserId()));
    }

}
