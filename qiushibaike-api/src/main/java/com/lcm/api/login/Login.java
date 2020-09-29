package com.lcm.api.login;

import lombok.Data;

import java.io.Serializable;

@Data
public class Login implements Serializable {
    private String username;
    private String password;
    private String phone;
    private String code;
    private String openid;
}
