package com.cloud.urlshortenerservice.service;

import com.cloud.urlshortenerservice.entity.Url;
import com.cloud.urlshortenerservice.model.UrlDto;
import com.cloud.urlshortenerservice.model.UserDto;
import com.cloud.urlshortenerservice.repository.UrlRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {

    private final static String ERR_DUPLICATE_KEY = "This key already exists!";

    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Url createShortUrl(UrlDto urlDto, UserDto userDto){

        Url url = new Url();
        url.setOriginalUrl(urlDto.getOriginalUrl());
        url.setExpirationDate(urlDto.getExpirationDate());

        if( userDto != null && (urlDto.getShortenKey() != null) && (urlDto.getShortenKey().length() != 0)){
            url.setShortenKey(urlDto.getShortenKey());
        } else {
            String shortKey = RandomStringUtils.randomAlphanumeric(6);
            url.setShortenKey(shortKey);
        }

        if (urlRepository.findByShortenKey(url.getShortenKey()).isPresent()){
            throw new IllegalArgumentException(ERR_DUPLICATE_KEY);
        }

        if (userDto != null){
            url.setUserId(userDto.getUserId());
        }

        return urlRepository.insert(url);
    }

    @CacheEvict(value = "urls", key = "#shortenKey")
    public Optional<Url> removeUrlByKeyAndUserId(String shortenKey, String userId){
        return urlRepository.deleteByShortenKeyAndUserId(shortenKey, userId);
    }

    public List<Url> getAllUrlsByUserId( String userId, String accountType){
        List<Url> urlList = urlRepository.findAllByUserId(userId);
        if (accountType.equals("premium")){
            return urlList;
        }

        for (Url url: urlList) {
            int numOfRemainingDays = (int) (ChronoUnit.DAYS.between( new Date().toInstant(), new Timestamp( Long.parseLong(url.getExpirationDate())).toInstant()) + 1);
            url.setExpirationDate("" + numOfRemainingDays);
        }
        return urlList;
    }

    @Cacheable(value = "urls", key = "#shortenKey", unless = "#result == null")
    public Optional<Url> getUrlByShortenKey(String shortenKey) {
        return urlRepository.findByShortenKey(shortenKey);
    }

    @CacheEvict(value = "urls", key = "#shortenKey")
    public void removeUrlByKey(String shortenKey){
        urlRepository.deleteByShortenKey(shortenKey);
    }

    public List<Url> getAllUrls(){
        return urlRepository.findAll();
    }

}
