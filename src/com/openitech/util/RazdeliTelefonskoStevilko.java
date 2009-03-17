/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author domenbasic
 */
public class RazdeliTelefonskoStevilko {

    public String[] razbij(String telefonskaStevilka) {
        if (telefonskaStevilka == null) {
            return null;
        }
        String[] telefon = new String[2];

//----------
        Pattern pattern0590 = Pattern.compile("059[0-2]\\d\\d\\d\\d\\d"); //0590, 0591, 0592
        Pattern pattern0590Razdeljen = Pattern.compile("059[0-2]\\s\\d\\d\\d\\d\\d"); //0590, 0591, 0592

        Pattern pattern0597 = Pattern.compile("059[7-9&&[^8]]\\d\\d\\d\\d\\d"); //0592, 0597, 0599
        Pattern pattern0597Razdeljen = Pattern.compile("059[7-9&&[^8]]\\s\\d\\d\\d\\d\\d"); //0592, 0597, 0599

        Pattern pattern0817 = Pattern.compile("0817\\d\\d\\d\\d\\d"); //0817
        Pattern pattern0817Razdeljen = Pattern.compile("0817\\s\\d\\d\\d\\d\\d"); //0817

        Pattern telMobitel = Pattern.compile("0[3-5][0-1]\\d\\d\\d\\d\\d\\d"); //mobilne stevilke
        Pattern telMobitelRazdeljen = Pattern.compile("0[3-5][0-1]\\s\\d\\d\\d\\d\\d\\d"); //mobilne stevilke

        Pattern pattern064 = Pattern.compile("064\\d+"); //064
        Pattern pattern064Razdeljen = Pattern.compile("064\\s\\d\\d\\d\\d\\d\\d"); //064

        Pattern pattern080 = Pattern.compile("0[7-9]0\\d+"); //080, 090, 070
        Pattern pattern080Razdeljen = Pattern.compile("0[7-9]0\\s\\d+"); //080, 090, 070

        Pattern telStacionarni = Pattern.compile("0[1-8&&[^6]]\\d\\d\\d\\d\\d\\d\\d"); //domace stevilke
        Pattern telStacionarniRazdeljen = Pattern.compile("0[1-8&&[^6]]\\s\\d\\d\\d\\d\\d\\d\\d"); //domace stevilke

        Pattern telTujina1 = Pattern.compile("\\W\\W\\d\\d\\d\\d+");
        Pattern telTujina1Razdeljen = Pattern.compile("\\W\\W\\d\\d\\d\\s\\d+");

        Pattern telTujina2 = Pattern.compile("00\\d\\d\\d\\d+");
        Pattern telTujina2Razdeljen = Pattern.compile("00\\d\\d\\d\\s\\d+");

//---------
        Matcher matcherT2 = pattern0590.matcher(telefonskaStevilka);
        Matcher matcherT22 = pattern0590Razdeljen.matcher(telefonskaStevilka);

        Matcher matcherT27 = pattern0597.matcher(telefonskaStevilka);
        Matcher matcherT272 = pattern0597Razdeljen.matcher(telefonskaStevilka);

        Matcher matcher0817 = pattern0817.matcher(telefonskaStevilka);
        Matcher matcher08172 = pattern0817Razdeljen.matcher(telefonskaStevilka);

        Matcher matcherMobitel = telMobitel.matcher(telefonskaStevilka);
        Matcher matcherMobitel2 = telMobitelRazdeljen.matcher(telefonskaStevilka);

        Matcher matcher064 = pattern064.matcher(telefonskaStevilka);
        Matcher matcher0642 = pattern064Razdeljen.matcher(telefonskaStevilka);

        Matcher matcher080 = pattern080.matcher(telefonskaStevilka);
        Matcher matcher0802 = pattern080Razdeljen.matcher(telefonskaStevilka);

        Matcher matcherStacionarna = telStacionarni.matcher(telefonskaStevilka);
        Matcher matcherStacionarna2 = telStacionarniRazdeljen.matcher(telefonskaStevilka);

        Matcher matcherTujinaPlusPlus = telTujina1.matcher(telefonskaStevilka);
        Matcher matcherTujinaPlusPlus2 = telTujina1Razdeljen.matcher(telefonskaStevilka);

        Matcher matcherTujina00 = telTujina2.matcher(telefonskaStevilka);
        Matcher matcherTujina002 = telTujina2Razdeljen.matcher(telefonskaStevilka);

        if (matcherT2.matches()) {      //T2
          //  System.out.println("T2");
            String koda = telefonskaStevilka.substring(0, 4);
            String telefonska = telefonskaStevilka.substring(4);
            System.out.println(koda);
            System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcherT22.matches()) {
           // System.out.println("matcherT22");
            String koda = telefonskaStevilka.substring(0, 4);
            String telefonska = telefonskaStevilka.substring(5);
         //   System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //----------------------------
        } else if (matcherT27.matches()) {
           // System.out.println("matcherT27");
            String koda = telefonskaStevilka.substring(0, 4);
            String telefonska = telefonskaStevilka.substring(4);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcherT272.matches()) {
            System.out.println("matcherT272");
            String koda = telefonskaStevilka.substring(0, 4);
            String telefonska = telefonskaStevilka.substring(5);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //------------------------------------
        } else if (matcher0817.matches()) {
         //   System.out.println("matcher0817");
            String koda = telefonskaStevilka.substring(0, 4);
            String telefonska = telefonskaStevilka.substring(4);
         //   System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcher08172.matches()) {
          //  System.out.println("matcher08172");
            String koda = telefonskaStevilka.substring(0, 4);
            String telefonska = telefonskaStevilka.substring(5);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //---------------------------------
        } else if (matcherMobitel.matches()) {
          //  System.out.println("Mobitel");
            String koda = telefonskaStevilka.substring(0, 3);
            String telefonska = telefonskaStevilka.substring(3);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcherMobitel2.matches()) {
          //  System.out.println("Mobitel2");
            String koda = telefonskaStevilka.substring(0, 3);
            String telefonska = telefonskaStevilka.substring(4);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //--------------------------------
        } else if (matcher064.matches()) {
          //  System.out.println("matcher064");
            String koda = telefonskaStevilka.substring(0, 3);
            String telefonska = telefonskaStevilka.substring(3);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcher0642.matches()) {
          //  System.out.println("matcher0642");
            String koda = telefonskaStevilka.substring(0, 3);
            String telefonska = telefonskaStevilka.substring(4);
         //   System.out.println(koda);
         //   System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //--------------------
        } else if (matcher080.matches()) {
          //  System.out.println("matcher080");
            String koda = telefonskaStevilka.substring(0, 3);
            String telefonska = telefonskaStevilka.substring(3);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcher0802.matches()) {
          //  System.out.println("matcher0802");
            String koda = telefonskaStevilka.substring(0, 3);
            String telefonska = telefonskaStevilka.substring(4);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //------------------------

        } else if (matcherStacionarna.matches()) {
         //   System.out.println("Stacionarna");
            String koda = telefonskaStevilka.substring(0, 2);
            String telefonska = telefonskaStevilka.substring(2);
         //   System.out.println(koda);
         //   System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        } else if (matcherStacionarna2.matches()) {
          //  System.out.println("Stacionarna2");
            String koda = telefonskaStevilka.substring(0, 2);
            String telefonska = telefonskaStevilka.substring(3);
          //  System.out.println(koda);
          //  System.out.println(telefonska);
            telefon[0] = koda;
            telefon[1] = telefonska;

            return telefon;
        //----------------------------
        } else if (matcherTujinaPlusPlus.matches()) {
            if (telefonskaStevilka.length() > 6) {
            //    System.out.println("Tujina ++");
            //    System.out.println(telefonskaStevilka.substring(0, 5));
                String kodaDrzava = telefonskaStevilka.substring(0, 5);

                String telefonska = telefonskaStevilka.substring(5);

            //    System.out.println(telefonska);
                telefon[0] = kodaDrzava;
                telefon[1] = telefonska;

                return telefon;
            } else {
                return null;
            }
        } else if (matcherTujinaPlusPlus2.matches()) {
            if (telefonskaStevilka.length() > 7) {
             //   System.out.println("Tujina ++");
             //   System.out.println(telefonskaStevilka.substring(0, 5));
                String kodaDrzava = telefonskaStevilka.substring(0, 5);

                String telefonska = telefonskaStevilka.substring(6);

            //    System.out.println(telefonska);
                telefon[0] = kodaDrzava;
                telefon[1] = telefonska;

                return telefon;
            } else {
                return null;
            }
        //----------------------------
        } else if (matcherTujina00.matches()) {
            if (telefonskaStevilka.length() > 6) {
            //    System.out.println("Tujina 00");
                String kodaDrzava = telefonskaStevilka.substring(0, 5);
                String telefonska = telefonskaStevilka.substring(5);
            //    System.out.println(kodaDrzava);
             //   System.out.println(telefonska);
                telefon[0] = kodaDrzava;
                telefon[1] = telefonska;
                return telefon;
            } else {
                return null;
            }
        } else if (matcherTujina002.matches()) {
            if (telefonskaStevilka.length() > 7) {
            //    System.out.println("Tujina 00");
                String kodaDrzava = telefonskaStevilka.substring(0, 5);
                String telefonska = telefonskaStevilka.substring(6);
            //    System.out.println(kodaDrzava);
             //   System.out.println(telefonska);
                telefon[0] = kodaDrzava;
                telefon[1] = telefonska;
                return telefon;
            } else {
                return null;
            }
        }
        return null;
    }
}
