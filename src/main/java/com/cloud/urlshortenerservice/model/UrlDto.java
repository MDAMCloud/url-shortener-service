package com.cloud.urlshortenerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    private String shortenKey;
    private String originalUrl;
    private String expirationDate;

    public UrlDto(String shortenKey, String originalUrl) {
        this.shortenKey = shortenKey;
        this.originalUrl = originalUrl;
    }
}
