package com.cloud.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Url implements Serializable {

    @Id
    private String id;

    @Field(name = "shorten-key")
    private String shortenKey;

    @Field(name = "original-url")
    private String originalUrl;

    @Field(name = "expiration-date")
    @Indexed(expireAfter = "30d")
    private String expirationDate;

    @Field(name = "user-id")
    private String userId;

    public Url(String shortenKey, String originalUrl, String expirationDate) {
        this.shortenKey = shortenKey;
        this.originalUrl = originalUrl;
        this.expirationDate = expirationDate;
    }

    public Url(String shortenKey, String originalUrl, String expirationDate, String userId) {
        this.shortenKey = shortenKey;
        this.originalUrl = originalUrl;
        this.expirationDate = expirationDate;
        this.userId = userId;
    }
}
