/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitech.util;

/**
 *
 * @author DomenBasic
 */
public class PreverjanjeDavcneInEMSO {

  public static Boolean preveriDavcno(int davcna) {
    if (davcna > 99999999) {//ce ima vec kot 8 stevilk ni veljavna
      return false;
    }
    int[] tabelaDavcna = new int[8];

    for (int i = 7; i >= 0; i--) {
      tabelaDavcna[i] = davcna % 10;
      davcna = davcna / 10;
    }


    int kontrolnaStevilka;
    int sestevekZmnozkov = 0;

    sestevekZmnozkov = 8 * tabelaDavcna[0] + 7 * tabelaDavcna[1] + 6 * tabelaDavcna[2] + 5 * tabelaDavcna[3] + 4 * tabelaDavcna[4] + 3 * tabelaDavcna[5] + 2 * tabelaDavcna[6];

    int ostanekPriDeljenju = sestevekZmnozkov % 11;

    kontrolnaStevilka = 11 - ostanekPriDeljenju;
    if (kontrolnaStevilka == 10) {
      kontrolnaStevilka = 0;
    } else if (kontrolnaStevilka == 11) {
      return false;
    }
    if (kontrolnaStevilka == tabelaDavcna[7]) {
      return true;
    } else {
      return false;
    }
  }

  public static Boolean preveriEMSO(long emso) {
    long[] tabelaEMSO = new long[13];
    if (emso > 9999999999999L) {
      return false;
    }
    for (int i = 12; i >= 0; i--) {
      tabelaEMSO[i] = emso % 10;
      emso = emso / 10;
    }

    long kontrolnaStevilka;
    long sestevekZmnozkov = 0L;


    sestevekZmnozkov = 7 * tabelaEMSO[0] + 6 * tabelaEMSO[1] + 5 * tabelaEMSO[2] + 4 * tabelaEMSO[3] + 3 * tabelaEMSO[4] + 2 * tabelaEMSO[5] + 7 * tabelaEMSO[6] + 6 * tabelaEMSO[7] + 5 * tabelaEMSO[8] + 4 * tabelaEMSO[9] + 3 * tabelaEMSO[10] + 2 * tabelaEMSO[11];

    long ostanekPriDeljenju = sestevekZmnozkov % 11;
    if (ostanekPriDeljenju == 0) {
      kontrolnaStevilka = 0;
    } else {
      kontrolnaStevilka = 11 - ostanekPriDeljenju;
    }

    if (kontrolnaStevilka == 10) {
      return false;
    }

    if (kontrolnaStevilka == tabelaEMSO[12]) {
      return true;
    } else {
      return false;
    }
  }
}
