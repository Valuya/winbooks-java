package be.valuya.jbooks.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Yannick
 */
public class WbClientSupplier {

    private String number;
    private WbClientSupplierType wbClientSupplierType = WbClientSupplierType.CLIENT;
    private String name1;
    private String name2;
    private String civName1;
    private String civName2;
    private String address1;
    private String address2;
    private WbVatCat wbVatCat;
    private String country;
    private String vatNumber;
    private String payCode;
    private String telNumber;
    private String faxNumber;
    private String bnkAccnt;
    private String zipCode;
    private String city;
    private String defltPost;
    private String lang;
    private String category;
    private String central = "400000";
    private String vatCode;
    private String currency;
    private String lastRemLev;
    private Date lastRemDat;
    private double totDeb1 = 0.0;
    private double totCre1 = 0.0;
    private double totDebTmp1 = 0.0;
    private double totCreTmp1 = 0.0;
    private double totDeb2 = 0.0;
    private double totCre2 = 0.0;
    private double totDebTmp2 = 0.0;
    private double totCreTmp2 = 0.0;
    private List<WbCustomClientAttribute> wbkCustomClientAttributes;
    private boolean locked;
    private WbMemoType wbMemoType;
    private boolean doc;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public WbClientSupplierType getWbClientSupplierType() {
        return wbClientSupplierType;
    }

    public void setWbClientSupplierType(WbClientSupplierType wbClientSupplierType) {
        this.wbClientSupplierType = wbClientSupplierType;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getCivName1() {
        return civName1;
    }

    public void setCivName1(String civName1) {
        this.civName1 = civName1;
    }

    public String getCivName2() {
        return civName2;
    }

    public void setCivName2(String civName2) {
        this.civName2 = civName2;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public WbVatCat getWbVatCat() {
        return wbVatCat;
    }

    public void setWbVatCat(WbVatCat wbVatCat) {
        this.wbVatCat = wbVatCat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getBnkAccnt() {
        return bnkAccnt;
    }

    public void setBnkAccnt(String bnkAccnt) {
        this.bnkAccnt = bnkAccnt;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDefltPost() {
        return defltPost;
    }

    public void setDefltPost(String defltPost) {
        this.defltPost = defltPost;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCentral() {
        return central;
    }

    public void setCentral(String central) {
        this.central = central;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLastRemLev() {
        return lastRemLev;
    }

    public void setLastRemLev(String lastRemLev) {
        this.lastRemLev = lastRemLev;
    }

    public Date getLastRemDat() {
        return lastRemDat;
    }

    public void setLastRemDat(Date lastRemDat) {
        this.lastRemDat = lastRemDat;
    }

    public double getTotDeb1() {
        return totDeb1;
    }

    public void setTotDeb1(double totDeb1) {
        this.totDeb1 = totDeb1;
    }

    public double getTotCre1() {
        return totCre1;
    }

    public void setTotCre1(double totCre1) {
        this.totCre1 = totCre1;
    }

    public double getTotDebTmp1() {
        return totDebTmp1;
    }

    public void setTotDebTmp1(double totDebTmp1) {
        this.totDebTmp1 = totDebTmp1;
    }

    public double getTotCreTmp1() {
        return totCreTmp1;
    }

    public void setTotCreTmp1(double totCreTmp1) {
        this.totCreTmp1 = totCreTmp1;
    }

    public double getTotDeb2() {
        return totDeb2;
    }

    public void setTotDeb2(double totDeb2) {
        this.totDeb2 = totDeb2;
    }

    public double getTotCre2() {
        return totCre2;
    }

    public void setTotCre2(double totCre2) {
        this.totCre2 = totCre2;
    }

    public double getTotDebTmp2() {
        return totDebTmp2;
    }

    public void setTotDebTmp2(double totDebTmp2) {
        this.totDebTmp2 = totDebTmp2;
    }

    public double getTotCreTmp2() {
        return totCreTmp2;
    }

    public void setTotCreTmp2(double totCreTmp2) {
        this.totCreTmp2 = totCreTmp2;
    }

    public List<WbCustomClientAttribute> getWbkCustomClientAttributes() {
        return wbkCustomClientAttributes;
    }

    public void setWbkCustomClientAttributes(List<WbCustomClientAttribute> wbkCustomClientAttributes) {
        this.wbkCustomClientAttributes = wbkCustomClientAttributes;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public WbMemoType getWbMemoType() {
        return wbMemoType;
    }

    public void setWbMemoType(WbMemoType wbMemoType) {
        this.wbMemoType = wbMemoType;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }
}
