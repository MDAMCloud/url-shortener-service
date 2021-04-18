package com.cloud.urlshortenerservice.repository;

import com.cloud.urlshortenerservice.entity.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {

    Optional<Url> findByShortenKey(String shortenKey);
    List<Url> findAllByUserId(String userId);
    Optional<Url> deleteByShortenKeyAndUserId(String key, String userId);
    Optional<Url> deleteByShortenKey(String key);
}
