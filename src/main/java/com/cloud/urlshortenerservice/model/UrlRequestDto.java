package com.cloud.urlshortenerservice.model;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {

    @NonNull
    private String originalUrl;

    @Nullable
    private String shortKey;

    @Nullable
    private String token;
}
