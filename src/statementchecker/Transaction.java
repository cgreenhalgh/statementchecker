/**
 * Copyright 2010 The University of Nottingham
 * 
 * This file is part of statementchecker.
 *
 *  statementchecker is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  statementchecker is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with statementchecker.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package statementchecker;

/** A single transaction.
 * 
 * @author cmg
 *
 */
public class Transaction {
	public int day;
	public int month;
	public int year;
	public String type;
	public String sortCode;
	public String accountNumber;
	public String description;
	public Integer inPence;
	public Integer outPence;
	public int balancePence;
	/** cons */
	public Transaction() {		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Transaction [accountNumber=" + accountNumber
				+ ", balancePence=" + balancePence + ", day=" + day
				+ ", description=" + description + ", inPence=" + inPence
				+ ", month=" + month + ", outPence=" + outPence + ", sortCode="
				+ sortCode + ", type=" + type + ", year=" + year + "]";
	}
	/** class */
	public static final String GIVING = "giving";
	public static final String BILLS = "bills";
	public static final String SAVING = "saving";
	public static final String CASH = "cash";
	public static final String DEPOSITS = "deposits";
	public static final String PAYMENT = "payment";
	public static final String INCOME = "income";
	public static final String TRANSPORT = "transport";
	public static final String SUPERMARKET = "supermarket";

	public static final String TYPE_BGC = "BGC";
	public static final String TYPE_DD = "DD";
	public static final String TYPE_DEB = "DEB";
	public static final String TYPE_CPT = "CPT";
	public static final String TYPE_SO = "SO";
	public static final String TYPE_FEE = "FEE";
	public static final String TYPE_TFR = "TFR";
	public static final String TYPE_PAY = "PAY";
	public static final String [] DESCRIPTION_DD_GIVING = new String[] {
		"CHRISTIAN AID", "COMPASSION", "TEARFUND"
	};
	public static final String [] DESCRIPTION_DD_PAYMENT = new String[] {
		"HMRC", "CO-OPERATIVE VISA"
	};
	public static final String [] DESCRIPTION_SO_SAVING = new String[] {
		"COVENTRY BUILD"
	};
	public static final String [] DESCRIPTION_DEB_TRANSPORT = new String[] {
		"TESCO GARAGE", "PACE ", "ESSO ", "AA MOTOR INS", "ASDA F/STN", "BP", "KEYWORTH GARAGE", "SHELL", "TIBSHELF MWSA",
		"BUNNEYS BIKES", "EVANS CYCLES", "CENTRAL TYRES", "KWIK FIT",
		"EAST MIDLANDS TRAI", "EC MAINLINE", "THETRAINLINE.COM"
	};
	public static final String [] DESCRIPTION_DEB_BILLS = new String[] {
		"NOTTINGHAM ICE CTR", "WWW.DVLA.GOV", "COTGRAVE LEISURE"
	};

	public static final String [] DESCRIPTION_DEB_GIVING = new String[] {
		"DONATION VIA CAF"
	};

	public static final String [] DESCRIPTION_DEB_SUPERMARKET = new String[] {
		"WAITROSE", "W M MORRISON", "ASDA ", "CO-OP ", "JS ONLINE", "SAINSBURY", "SACAT SAINSBURY"
	};
	public static final String [] DESCRIPTION_DEB_CLOTHES = new String[] {
		"BHS", "CLARKS", "D&P", "DAVID HOLMES" // ,...
	};
	public boolean descriptionMatches(String[] ds) {
		for (int i=0; i<ds.length; i++)
			if (description.startsWith(ds[i]))
				return true;
		return false;
	}
	/** classify */
	public String classify() {
		if (TYPE_BGC.equals(type) && inPence!=null)
			return INCOME;
		if (TYPE_SO.equals(type) && descriptionMatches(DESCRIPTION_SO_SAVING))
			return SAVING;
		if (TYPE_TFR.equals(type))
			// from/to esavings
			return SAVING;
		if (inPence!=null && !TYPE_DEB.equals(type))
			return DEPOSITS;
		if (TYPE_SO.equals(type) || (TYPE_DD.equals(type) && descriptionMatches(DESCRIPTION_DD_GIVING)))
			return GIVING;
		if (TYPE_DD.equals(type) && !descriptionMatches(DESCRIPTION_DD_PAYMENT))
			return BILLS;
		if (TYPE_DEB.equals(type) && descriptionMatches(DESCRIPTION_DEB_BILLS))
			return BILLS;
		if (TYPE_DEB.equals(type) && descriptionMatches(DESCRIPTION_DEB_TRANSPORT))
			return TRANSPORT;
		if (TYPE_DEB.equals(type) && descriptionMatches(DESCRIPTION_DEB_SUPERMARKET))
			return SUPERMARKET;
		if (TYPE_DEB.equals(type) && descriptionMatches(DESCRIPTION_DEB_GIVING))
			return GIVING;
		if (TYPE_FEE.equals(type))
			return BILLS;
		if (TYPE_CPT.equals(type))
			return CASH;
		return PAYMENT;
	}
}
