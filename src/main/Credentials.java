package main;

import java.util.Objects;

import no.stelar7.api.r4j.basic.APICredentials;

public class Credentials {
	public static  String lolkey     = "";
    public static  String tkey = "NO_KEY_:(";
    public static  String lorkey        = "NO_KEY_:(";
    public static  String tftkey        = "NO_KEY_:(";
    public static  String vkey        = "NO_KEY_:(";
    
    public static APICredentials creds;
    
    public static APICredentials getCreds() {
    	if(Objects.isNull(creds))
    		return creds = new APICredentials(lolkey, tkey, tftkey, lorkey, vkey);
    	else return creds;
    }
}
