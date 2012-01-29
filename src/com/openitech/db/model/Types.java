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


package com.openitech.db.model;

/**
 * <p>Title: J2EE Common components</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Uros Trojar
 */

public interface Types {
  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>BIT</code>.
   */
          public final static Integer BIT 	=  new Integer(-7);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>TINYINT</code>.
   */
          public final static Integer TINYINT 	=  new Integer(-6);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>SMALLINT</code>.
   */
          public final static Integer SMALLINT	=  new Integer(5);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>INTEGER</code>.
   */
          public final static Integer INTEGER 	=  new Integer(4);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>BIGINT</code>.
   */
          public final static Integer BIGINT 		=  new Integer(-5);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>FLOAT</code>.
   */
          public final static Integer FLOAT 		=  new Integer(6);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>REAL</code>.
   */
          public final static Integer REAL 		=     new Integer(7);


  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>DOUBLE</code>.
   */
          public final static Integer DOUBLE 		=  new Integer(8);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>NUMERIC</code>.
   */
          public final static Integer NUMERIC 	=  new Integer(2);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>DECIMAL</code>.
   */
          public final static Integer DECIMAL		=  new Integer(3);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>CHAR</code>.
   */
          public final static Integer CHAR		=  new Integer(1);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>VARCHAR</code>.
   */
          public final static Integer VARCHAR 	=  new Integer(12);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>LONGVARCHAR</code>.
   */
          public final static Integer LONGVARCHAR 	=  new Integer(-1);


  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>DATE</code>.
   */
          public final static Integer DATE 		=  new Integer(91);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>TIME</code>.
   */
          public final static Integer TIME 		=  new Integer(92);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>TIMESTAMP</code>.
   */
          public final static Integer TIMESTAMP 	=  new Integer(93);


  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>BINARY</code>.
   */
          public final static Integer BINARY		=  new Integer(-2);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>VARBINARY</code>.
   */
          public final static Integer VARBINARY 	=  new Integer(-3);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>LONGVARBINARY</code>.
   */
          public final static Integer LONGVARBINARY 	=  new Integer(-4);

  /**
   * <P>The constant in the Java programming language, sometimes referred
   * to as a type code, that identifies the generic SQL type
   * <code>NULL</code>.
   */
          public final static Integer NULL		=  new Integer(0);

      /**
       * The constant in the Java programming language that indicates
       * that the SQL type is database-specific and
       * gets mapped to a Java object that can be accessed via
       * the methods <code>getObject</code> and <code>setObject</code>.
       */
          public final static Integer OTHER		=  new Integer(1111);



      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>JAVA_OBJECT</code>.
       * @since 1.2
       */
          public final static Integer JAVA_OBJECT         =  new Integer(2000);

      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>DISTINCT</code>.
       * @since 1.2
       */
          public final static Integer DISTINCT            =  new Integer(2001);

      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>STRUCT</code>.
       * @since 1.2
       */
          public final static Integer STRUCT              =  new Integer(2002);

      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>ARRAY</code>.
       * @since 1.2
       */
          public final static Integer ARRAY               =  new Integer(2003);

      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>BLOB</code>.
       * @since 1.2
       */
          public final static Integer BLOB                =  new Integer(2004);

      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>CLOB</code>.
       * @since 1.2
       */
          public final static Integer CLOB                =  new Integer(2005);

      /**
       * The constant in the Java programming language, sometimes referred to
       * as a type code, that identifies the generic SQL type
       * <code>REF</code>.
       * @since 1.2
       */
          public final static Integer REF                 =  new Integer(2006);

      /**
       * The constant in the Java programming language, somtimes referred to
       * as a type code, that identifies the generic SQL type <code>DATALINK</code>.
       *
       * @since 1.4
       */
      public final static Integer DATALINK =  new Integer(70);

      /**
       * The constant in the Java programming language, somtimes referred to
       * as a type code, that identifies the generic SQL type <code>BOOLEAN</code>.
       *
       * @since 1.4
       */
      public final static Integer BOOLEAN =  new Integer(16);

      /**
       * The constant type code, that identifies that the value sould be substituted.
       *
       */
      public final static Integer SUBST =  new Integer(Integer.MAX_VALUE-1);
      public final static Integer SUBST_FIRST =  SUBST;
      public final static Integer SUBST_ALL =  new Integer(Integer.MAX_VALUE);
}
