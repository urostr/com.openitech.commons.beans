package com.openitech.value.fields;

import com.openitech.text.CaseInsensitiveString;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author uros
 */
public class Field {

  public static final Field VERSION = new Field("_SYS_EVENT_VERSION", java.sql.Types.INTEGER, Integer.MIN_VALUE);

    Integer idPolja;
    String name;
    int type;
    int fieldIndex;

  protected LookupType lookupType = null;
  protected FieldModel model = new FieldModel();

    /**
     *
     * @param name fieldName
     * @param type fieldType java.sql.Type
     */
    public Field(String name, int type) {
        this(name, type, 1);
    }

  /**
   *
   * @param name fieldName
   * @param type fieldType java.sql.Type
   */
    public Field(String name, int type, int fieldIndex) {
       this(null, name, type, fieldIndex);
    }

    /**
     *
     * @param name fieldName
     * @param type fieldType java.sql.Type
     */
    public Field(Integer idPolja, String name, int type, int fieldIndex) {
        super();
        this.idPolja = idPolja;
        this.name = name;
        this.type = type;
        this.fieldIndex = fieldIndex;
    }

  /**
   * Get the value of model
   *
   * @return the value of model
   */
  public FieldModel getModel() {
    return model;
  }
  protected String opis;

  /**
   * Get the value of opis
   *
   * @return the value of opis
   */
  public String getOpis() {
    return opis==null?name:opis;
  }

  /**
   * Set the value of opis
   *
   * @param opis new value of opis
   */
  public void setOpis(String opis) {
    this.opis = opis;
  }


 
    /**
     *
     * @param name fieldName
     * @param type fieldType java.sql.Type
     */
    public Field(Field field) {
        this(field.idPolja, field.name, field.type, field.fieldIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Field)) {
            return false;
        }
        final Field other = (Field) obj;
        if (this.fieldIndex != other.fieldIndex) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) {
            return false;
        }
        if ((this.lookupType == null) ? (other.lookupType != null) : !this.lookupType.equals(other.lookupType)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @return field name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return field type
     */
    public int getType() {
        return type;
    }

    /**
     * Get the value of fieldIndex
     *
     * @return the value of fieldIndex
     */
    public int getFieldIndex() {
        return fieldIndex;
    }

    public Integer getIdPolja() {
        return idPolja;
    }

    public void setIdPolja(Integer idPolja) {
        this.idPolja = idPolja;
    }

  /**
   * Get the value of lookupType
   *
   * @return the value of lookupType
   */
  public LookupType getLookupType() {
    return lookupType;
  }

  /**
   * Set the value of lookupType
   *
   * @param lookupType new value of lookupType
   */
  public void setLookupType(LookupType lookupType) {
    this.lookupType = lookupType;
  }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * (hash + (this.lookupType != null ? this.lookupType.hashCode() : 0) + (this.name != null ? this.name.toUpperCase().hashCode() : 0) + fieldIndex);
        return hash;
    }
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

  @Override
  public String toString() {
    return name + ":" + fieldIndex + ":" + type + ":" + ValueType.getType(type);
  }


  private final static Pattern indexed = Pattern.compile("(.*)(\\d+)$");

  public static Field getField(String fieldName, int fieldValueIndex,  final java.util.Map<CaseInsensitiveString, Field> fields) {
    Field field = fields.get(CaseInsensitiveString.valueOf(fieldName));
    if (field==null) {
      final Matcher matcher = indexed.matcher(fieldName);
      if (matcher.find()) {
        fieldName = matcher.group(1);
        fieldValueIndex = Integer.parseInt(matcher.group(2));

        field = fields.get(CaseInsensitiveString.valueOf(fieldName));
      }
    }
    return new Field(field.getName(), field.getType(), fieldValueIndex);
  }

  public Field getNonIndexedField() {
    final Matcher matcher = indexed.matcher(name);
    if (matcher.find()) {
      return new Field(matcher.group(1),type,Integer.parseInt(matcher.group(2)));
    } else {
      return new Field(this);
    }
  }

  public static enum LookupType {
    PRIMARY_KEY,
    VERSION_ID,
    ID_SIFRANTA,
    ID_SIFRE
  }

  public static class FieldModel {

    protected String replace;
    protected FieldModel.Query query;
    protected FieldModel.TableColumns tableColumns;

    /**
     * Gets the value of the replace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReplace() {
      return replace;
    }

    /**
     * Sets the value of the replace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReplace(String value) {
      this.replace = value;
    }

    /**
     * Gets the value of the query property.
     *
     * @return
     *     possible object is
     *     {@link FieldModel.Query }
     *
     */
    public FieldModel.Query getQuery() {
      return query;
    }

    /**
     * Sets the value of the query property.
     *
     * @param value
     *     allowed object is
     *     {@link FieldModel.Query }
     *
     */
    public void setQuery(FieldModel.Query value) {
      this.query = value;
    }

    /**
     * Gets the value of the tableColumns property.
     *
     * @return
     *     possible object is
     *     {@link FieldModel.TableColumns }
     *
     */
    public FieldModel.TableColumns getTableColumns() {
      return tableColumns;
    }

    /**
     * Sets the value of the tableColumns property.
     *
     * @param value
     *     allowed object is
     *     {@link FieldModel.TableColumns }
     *
     */
    public void setTableColumns(FieldModel.TableColumns value) {
      this.tableColumns = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded">
     *         &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    public static class Query {

        protected FieldModel.Query.Select select;
        protected FieldModel.Query.Join join;

        /**
         * Gets the value of the select property.
         *
         * @return
         *     possible object is
         *     {@link FieldModel.Query.Select }
         *
         */
        public FieldModel.Query.Select getSelect() {
            return select;
        }

        /**
         * Sets the value of the select property.
         *
         * @param value
         *     allowed object is
         *     {@link FieldModel.Query.Select }
         *
         */
        public void setSelect(FieldModel.Query.Select value) {
            this.select = value;
        }

        /**
         * Gets the value of the join property.
         *
         * @return
         *     possible object is
         *     {@link FieldModel.Query.Join }
         *
         */
        public FieldModel.Query.Join getJoin() {
            return join;
        }

        /**
         * Sets the value of the join property.
         *
         * @param value
         *     allowed object is
         *     {@link FieldModel.Query.Join }
         *
         */
        public void setJoin(FieldModel.Query.Join value) {
            this.join = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence maxOccurs="unbounded">
         *         &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        public static class Join {

            protected List<String> sql;

            /**
             * Gets the value of the sql property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the sql property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSQL().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             *
             *
             */
            public List<String> getSQL() {
                if (sql == null) {
                    sql = new ArrayList<String>();
                }
                return this.sql;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence maxOccurs="unbounded">
         *         &lt;element name="SQL" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        public static class Select {

            protected List<String> sql;

            /**
             * Gets the value of the sql property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the sql property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSQL().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             *
             *
             */
            public List<String> getSQL() {
                if (sql == null) {
                    sql = new ArrayList<String>();
                }
                return this.sql;
            }

        }

    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded">
     *         &lt;element name="TableColumnDefinition">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="TableColumnEntry" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    public static class TableColumns {

      protected List<FieldModel.TableColumns.TableColumnDefinition> tableColumnDefinition;

      /**
       * Gets the value of the tableColumnDefinition property.
       *
       * <p>
       * This accessor method returns a reference to the live list,
       * not a snapshot. Therefore any modification you make to the
       * returned list will be present inside the JAXB object.
       * This is why there is not a <CODE>set</CODE> method for the tableColumnDefinition property.
       *
       * <p>
       * For example, to add a new item, do as follows:
       * <pre>
       *    getTableColumnDefinition().add(newItem);
       * </pre>
       *
       *
       * <p>
       * Objects of the following type(s) are allowed in the list
       * {@link FieldModel.TableColumns.TableColumnDefinition }
       *
       *
       */
      public List<FieldModel.TableColumns.TableColumnDefinition> getTableColumnDefinition() {
        if (tableColumnDefinition == null) {
          tableColumnDefinition = new ArrayList<FieldModel.TableColumns.TableColumnDefinition>();
        }
        return this.tableColumnDefinition;
      }

      /**
       * <p>Java class for anonymous complex type.
       *
       * <p>The following schema fragment specifies the expected content contained within this class.
       *
       * <pre>
       * &lt;complexType>
       *   &lt;complexContent>
       *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
       *       &lt;sequence>
       *         &lt;element name="TableColumnEntry" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
       *       &lt;/sequence>
       *     &lt;/restriction>
       *   &lt;/complexContent>
       * &lt;/complexType>
       * </pre>
       *
       *
       */
      public static class TableColumnDefinition {

        protected List<String> tableColumnEntry;

        /**
         * Gets the value of the tableColumnEntry property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tableColumnEntry property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTableColumnEntry().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         *
         *
         */
        public List<String> getTableColumnEntry() {
          if (tableColumnEntry == null) {
            tableColumnEntry = new ArrayList<String>();
          }
          return this.tableColumnEntry;
        }
      }
    }
  }
}
