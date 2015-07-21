package com.backend.util;

public class StringUtil {
   public static boolean isNullandEmpty(String passStr){
	   if(passStr==null|| passStr.equals("")){
		   return true;
	   }else{
		   return false;
	   }
   }
}
