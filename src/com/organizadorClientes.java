/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.util.ArrayList;

/**
 *
 * @author Jose
 */
public class organizadorClientes {
    
     private static String ipPendiente = null;
     private static Integer conectados = 0;

     public static void setIp (String ip)
     {
         ipPendiente = ip;
     }
     
     public static String getIp()
     {
         return ipPendiente;
     }
     
     public static void sumarConectado()
     {
         conectados = conectados + 1;
     }
     
     public static void restarConectado()
     {
         conectados = conectados - 1;
     }
     
     public static Integer numeroConectados()
     {
         return conectados;
     }
     
}
