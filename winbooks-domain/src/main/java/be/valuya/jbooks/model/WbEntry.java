package be.valuya.jbooks.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Yannick
 */
public class WbEntry implements Cloneable {

    private Integer recordNumber;
    private WbDocType wbDocType;
    private String dbkCode;
    private WbDbkType wbDbkType;
    private String docNumber;
    private WbDocOrderType wbDocOrderType = WbDocOrderType.NUMBER;
    /**
     * Winbooks doc: Ce champs peut rester à blanc. Il est automatiquement complété lors de l’import.
     *
     * Il retient l’ordre d’encodage des imputations pour les financiers et OD. Il commence à ‘001’ pour chaque nouveau
     * document et est incrémenté de 1 ( ex : 001,002,003, … ) Dans le cas d’un financier, l’ensemble des imputations
     * passées sur un numéro d’extrait (DOCNUMBER) donne lieu à l’enregistrement d’une ou deux imputations sur le compte
     * centralisateur de ce journal financier : <br/>
     * - une imputation avec le total des mouvements passés au débit du compte centralisateur<br/>
     * - une imputation avec le total des mouvements passés au crédit du compte centralisateur<br/>
     * Les imputations sur ce compte centralisateur ont un docorder = ‘999’ et ‘998’
     *
     * Le DOCORDER reste vide pour les autres journaux ( ventes, achats, notes de crédit )
     */
    private Integer docOrder;
    /**
     * Winbooks doc: Il s’agit du numéro de compte général. Pour les records de type 1 (client) ou 2 (fournisseur), il
     * est complété automatiquement par le compte centralisateur. Pour les records de type 3 non TVA, il doit être
     * rempli par le compte d’imputation. Pour les records de type 3 TVA (VATCODE rempli) ou de type 4 (TVA 0%), il est
     * complété par WB par le compte d’imputation TVA.
     */
    private String accountGl;
    /**
     * Winbooks doc: Il s’agit de la référence client ou fournisseur ( enregistrée dans le CSF.DBF). Il doit toujours
     * être complété par une réference client pour un doctype = 1 et par une référence fournisseur pour un doctype = 2.
     */
    private String accountRp;
    /**
     * Winbooks doc: Un chiffre de 1 à 9 indiquant le numéro de l’exercice comptable. Rempli par Wb par l’exercice
     * comptable actif.
     */
    private String bookYear;

    private WbBookYearFull wbBookYearFull;
    /**
     * Winbooks doc: 00 = période d’ouverture uniquement autorisée dans un journal d’opérations diverses
     * d’ouverture<br/>
     * 01, 02, …. = les périodes comptables mensuelles ou trimestrielles autorisées pour tous les journaux sauf pour les
     * journaux d’opérations diverses d’ouverture et de clôture.<br/>
     * 99 = période de clôture uniquement autorisées dans un journal d’opérations diverses de clôture<br/>
     * Il s’agit de la période comptable, qui ne correspond pas forcément au mois (exercice décalé,de + de 12 mois, …).
     * Voir aussi Wb.Import.Setdefaultperiod pour la manière de remplir automatiquement cette zone.
     *
     */
    private String period;
    private Date date;
    private Date dateDoc;
    private Date dueDate;
    private String comment;
    private String commentExt;
    /**
     * Winbooks doc: Exercice en BEF : montant de l’imputation comptable Exercice en EURO : Vaut toujours 0 ! Doit
     * toujours être <> 0 dans un record de doctype = 1, 2 ou 3 pour exercice BEF Montant positif = imputation au débit
     * Montant négatif = imputation au crédit Le total des amount d’un même docnumber doit toujours donner 0 ( débit =
     * crédit )
     */
    private BigDecimal amount;
    /**
     * Winbooks doc: Exercice en BEF : équivalent EURO du montant en BEF (AMOUNT), complété par Wb. Exercice EURO :
     * montant en EURO de l’imputation comptable. Doit toujours être <> 0 dans un record de doctype = 1, 2 ou 3 pour
     * exercice EURO Montant positif = imputation au débit Montant négatif = imputation au crédit Le total des amounteur
     * d’un même docnumber doit toujours donner 0 ( débit = crédit )
     */
    private BigDecimal amountEur;
    /**
     * Winbooks doc: Reprend la base de l’imputation TVA. Est complété dans le cas d’une facture ou note de crédit, pour
     * les records TVA uniquement (type 3 ou type 4) Reste vide pour les financiers et les opérations diverses Le
     * VATBASE du record de doctype = 1 ( imputation client) ou du record de doctype = 2 ( imputation fournisseur)
     * reprend le montant total du chiffre d’affaires de la facture ( servira aux statistiques et au listing Tva
     * clients)
     *
     * Wb totalise automatiquement dans le VATBASE du record tiers, le montant total du CA qui servira pour les
     * statistiques et le listing tva annuel.
     */
    private BigDecimal vatBase;
    /**
     * Winbooks doc: On y indique soit le code TVA interne WB (6 chiffres), soit le code tva en clair (21) : dans ce cas
     * Wb convertira automatiquement en code interne sur base du type de document (Cli/Fou) et de la langue. Voir
     * explication VATCODE dans la description du CSF.
     *
     */
    private String vatCode;
    /**
     * Winbooks doc: Complété automatiquement par Winbooks. Il reprend le montant total de la taxe de la facture (
     * servira principalement au listing Tva clients ) Reste vide pour les financiers et les opérations diverses)
     */
    private BigDecimal vatTax;
    /**
     * Winbooks doc: Peut rester vide. Ne sert que lors d’un encodage via d’une facture ou note de crédit via WinBooks.
     */
    private String vatImput;
    private BigDecimal currAmount;
    private String currCode;
    private BigDecimal curEurBase;
    private BigDecimal curRate;
    /**
     * Winbooks doc: Informations pour le lettrage
     */
    private String matchNo;
    /**
     * Winbooks doc: Informations pour le lettrage
     */
    private Date oldDate;
    private boolean locked;
    private boolean imported;
    private boolean temp;
    private WbMemoType memoType;
    private boolean doc;
    private WbDocStatus docStatus;

    public WbDocType getWbDocType() {
        return wbDocType;
    }

    public void setWbDocType(WbDocType wbDocType) {
        this.wbDocType = wbDocType;
    }

    public String getDbkCode() {
        return dbkCode;
    }

    public void setDbkCode(String dbkCode) {
        this.dbkCode = dbkCode;
    }

    public WbDbkType getWbDbkType() {
        return wbDbkType;
    }

    public void setWbDbkType(WbDbkType wbDbkType) {
        this.wbDbkType = wbDbkType;
    }

    public WbDocOrderType getWbDocOrderType() {
        return wbDocOrderType;
    }

    public void setWbDocOrderType(WbDocOrderType wbDocOrderType) {
        this.wbDocOrderType = wbDocOrderType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public Integer getDocOrder() {
        return docOrder;
    }

    public void setDocOrder(Integer docOrder) {
        this.docOrder = docOrder;
    }

    public String getAccountGl() {
        return accountGl;
    }

    public void setAccountGl(String accountGl) {
        this.accountGl = accountGl;
    }

    public String getAccountRp() {
        return accountRp;
    }

    public void setAccountRp(String accountRp) {
        this.accountRp = accountRp;
    }

    public String getBookYear() {
        return bookYear;
    }

    public void setBookYear(String bookYear) {
        this.bookYear = bookYear;
    }

    public WbBookYearFull getWbBookYearFull() {
        return wbBookYearFull;
    }

    public void setWbBookYearFull(WbBookYearFull wbBookYearFull) {
        this.wbBookYearFull = wbBookYearFull;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateDoc() {
        return dateDoc;
    }

    public void setDateDoc(Date dateDoc) {
        this.dateDoc = dateDoc;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentExt() {
        return commentExt;
    }

    public void setCommentExt(String commentExt) {
        this.commentExt = commentExt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountEur() {
        return amountEur;
    }

    public void setAmountEur(BigDecimal amountEur) {
        this.amountEur = amountEur;
    }

    public BigDecimal getVatBase() {
        return vatBase;
    }

    public void setVatBase(BigDecimal vatBase) {
        this.vatBase = vatBase;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public BigDecimal getVatTax() {
        return vatTax;
    }

    public void setVatTax(BigDecimal vatTax) {
        this.vatTax = vatTax;
    }

    public String getVatImput() {
        return vatImput;
    }

    public void setVatImput(String vatImput) {
        this.vatImput = vatImput;
    }

    public BigDecimal getCurrAmount() {
        return currAmount;
    }

    public void setCurrAmount(BigDecimal currAmount) {
        this.currAmount = currAmount;
    }

    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    public BigDecimal getCurEurBase() {
        return curEurBase;
    }

    public void setCurEurBase(BigDecimal curEurBase) {
        this.curEurBase = curEurBase;
    }

    public BigDecimal getCurRate() {
        return curRate;
    }

    public void setCurRate(BigDecimal curRate) {
        this.curRate = curRate;
    }

    public String getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(String matchNo) {
        this.matchNo = matchNo;
    }

    public Date getOldDate() {
        return oldDate;
    }

    public void setOldDate(Date oldDate) {
        this.oldDate = oldDate;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public WbMemoType getMemoType() {
        return memoType;
    }

    public void setMemoType(WbMemoType memoType) {
        this.memoType = memoType;
    }

    public boolean isDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        this.doc = doc;
    }

    public WbDocStatus getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(WbDocStatus docStatus) {
        this.docStatus = docStatus;
    }

    @Override
    public WbEntry clone() {
        try {
            return (WbEntry) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new IllegalStateException(cloneNotSupportedException);
        }
    }

    @Override
    public String toString() {
        return "WbEntry{" +
                "wbDocType=" + wbDocType +
                ", dbkCode='" + dbkCode + '\'' +
                ", wbDbkType=" + wbDbkType +
                ", docNumber='" + docNumber + '\'' +
                ", wbDocOrderType=" + wbDocOrderType +
                ", docOrder=" + docOrder +
                ", accountGl='" + accountGl + '\'' +
                ", accountRp='" + accountRp + '\'' +
                ", bookYear='" + bookYear + '\'' +
                ", period='" + period + '\'' +
                ", date=" + date +
                ", dateDoc=" + dateDoc +
                ", dueDate=" + dueDate +
                ", comment='" + comment + '\'' +
                ", commentExt='" + commentExt + '\'' +
                ", amount=" + amount +
                ", amountEur=" + amountEur +
                ", vatBase=" + vatBase +
                ", vatCode='" + vatCode + '\'' +
                ", vatTax=" + vatTax +
                ", vatImput='" + vatImput + '\'' +
                ", currAmount=" + currAmount +
                ", currCode='" + currCode + '\'' +
                ", curEurBase=" + curEurBase +
                ", curRate=" + curRate +
                ", matchNo='" + matchNo + '\'' +
                ", oldDate=" + oldDate +
                ", locked=" + locked +
                ", imported=" + imported +
                ", temp=" + temp +
                ", memoType=" + memoType +
                ", doc=" + doc +
                ", docStatus=" + docStatus +
                '}';
    }

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }
}