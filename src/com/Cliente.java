/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.io.Serializable;

/**
 *
 * @author Jose
 */
public class Cliente implements Serializable {
    public String nickname;
    public String password;

    public Cliente(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }
    
    
}
