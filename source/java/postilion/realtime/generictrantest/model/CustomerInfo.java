package postilion.realtime.generictrantest.model;

public class CustomerInfo {
	
	public String pan;
    public String expiryDate;
    public String customerId;
    public String defaultAccountType;
    public String name;
    public int issuer;
    public String extendedFields;
    public String seqNr;
    public String idType;
    public String pinOffset;
    public String cardStatus;
    public String holdRspCode;
    
    public CustomerInfo() {
    	
    }
    
    @Override
    public String toString() {
        return "CustomerInfo{" +
                "pan='" + pan + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", customerId='" + customerId + '\'' +
                ", defaultAccountType='" + defaultAccountType + '\'' +
                ", name='" + name + '\'' +
                ", issuer=" + issuer +
                ", extendedFields='" + extendedFields + '\'' +
                ", seqNr='" + seqNr + '\'' +
                ", idType='" + idType + '\'' +
                ", pinOffset='" + pinOffset + '\'' +
                ", cardStatus=" + cardStatus +
                ", holdRspCode='" + holdRspCode + '\'' +
                '}';
    }

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDefaultAccountType() {
		return defaultAccountType;
	}

	public void setDefaultAccountType(String defaultAccountType) {
		this.defaultAccountType = defaultAccountType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIssuer() {
		return issuer;
	}

	public void setIssuer(int issuer) {
		this.issuer = issuer;
	}

	public String getExtendedFields() {
		return extendedFields;
	}

	public void setExtendedFields(String extendedFields) {
		this.extendedFields = extendedFields;
	}

	public String getSeqNr() {
		return seqNr;
	}

	public void setSeqNr(String seqNr) {
		this.seqNr = seqNr;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getPinOffset() {
		return pinOffset;
	}

	public void setPinOffset(String pinOffset) {
		this.pinOffset = pinOffset;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getHoldRspCode() {
		return holdRspCode;
	}

	public void setHoldRspCode(String holdRspCode) {
		this.holdRspCode = holdRspCode;
	}
    

}
