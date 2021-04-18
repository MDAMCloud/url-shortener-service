package com.cloud.urlshortenerservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    private String userId;
    private String username;
    private String accountType;

}
