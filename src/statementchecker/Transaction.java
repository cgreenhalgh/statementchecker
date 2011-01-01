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
	
}
