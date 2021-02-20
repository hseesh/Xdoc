package com.yatoufang.templet;

import java.text.DecimalFormat;


/**
 * @Auther: hse
 * @Date: 2021/2/4 0009
 *
 * the default value for response example
 */
public class Value {

   private static final String[] WORDS = {"I","love","waking","up","in","the","morning","not","knowing","what's","gonna","happen",
           "or","who","I'm","gonna","meet","where","I'm","gonna","wind","up",
           "I","can","not","even","picture","him","at","all","He","only","live","in","my","memory",
           "One","may","fall","in","love","with","many","people","during","the","lifetime","When","you","finally","get","your","own","happiness","you","will","understand",
           "the","previous","sadness","is","a","kind","of","treasure","which","makes","you","better","to","hold","and","cherish","the","people","you","love",
           "I","figure","life","is","a","gift","and","I","don't","intend","on","wasting","it","You","never","know","what","hand","you're","going","to","get",
           "dealt","next","You","learn","to","take","life","as","it","comes","at","you"};
   // 111

    public static String getWord(){
        return  WORDS[ (int)(Math.random()*111)];
    }

    public static String getDecimal(){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(Math.random() * 110);
    }

   public static int getInteger(){
        return (int)(Math.random()*111);
   }


}
