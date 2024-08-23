package dev.kayange.sdms.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Setter @Getter
public class Token {
    private String access;
    private String refresh;
}
