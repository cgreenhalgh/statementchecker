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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** A whole statement.
 * 
 * @author cmg
 *
 */
public class Statement {
	private List<Transaction> transactions;
	/** cons */
	public Statement() {
	}
	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}
	/**
	 * @param transactions the transactions to set
	 */
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public static int DATE = 0;
	public static int TYPE = 1;
	public static int SORT_CODE = 2;
	public static int ACCOUNT_NUMBER = 3;
	public static int DESCRIPTION = 4;
	public static int IN = 5;
	public static int OUT = 6;
	public static int BALANCE = 7;
	public static int COUNT = 8;
	
	/** read from CSV file 
	 * @param cheques */
	public static Statement readStatement(File f, Map<String, Cheque> cheques) throws IOException {
		Statement s = new Statement();
		List<Transaction> ts = new LinkedList<Transaction>();
		s.setTransactions(ts);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int count = 0;
		while (true) {
			String line = br.readLine();
			count++;
			if (line==null)
				break;
			if (line.contains("\"")) {
				// work harder to split
				int ix1 = line.indexOf('\"');
				int ix2 = line.indexOf('\"', ix1+1);
				//int ix3 = line.indexOf(',');
				String line2 = line.substring(0,ix1)+line.substring(ix1+1,ix2).replace(",",".")+line.substring(ix2+1);				
				System.out.println("Replace "+line+" with "+line2);
				line = line2;
			}
			String toks[] = line.split(",");
			//Date	Type	Sort Code	Account Number	Description	In	Out	Balance
			if (toks.length!=COUNT) {
				System.err.println("Ignore "+f+":"+count+": "+line);
				continue;
			}
			if (toks[DATE].equals("Date"))
				continue;
			Transaction t = new Transaction();
			//01/02/2010	FEE	309932	308854	PLATINUM ACCOUNT		12	4180.03
			t.type = toks[TYPE];
			t.description =toks[DESCRIPTION];
			t.sortCode = toks[SORT_CODE];
			t.accountNumber = toks[ACCOUNT_NUMBER];
			try {
				t.inPence = toPence(toks[IN]);
				t.outPence = toPence(toks[OUT]);
				t.balancePence = toPence(toks[BALANCE]);
			} catch (NumberFormatException nfe) {
				System.err.println("Error parsing pence in "+f+":"+count+": "+line+" ("+nfe+")");
				continue;
			}
			try {
				int ix1 = toks[DATE].indexOf('/');
				int ix2 = toks[DATE].indexOf('/', ix1+1);
				t.day = Integer.parseInt(toks[DATE].substring(0,ix1));
				t.month = Integer.parseInt(toks[DATE].substring(ix1+1,ix2));
				t.year = Integer.parseInt(toks[DATE].substring(ix2+1));
			}
			catch (Exception e) {
				System.err.println("Error parsing date in "+f+":"+count+": "+line);
			}
			
			// cheque?
			if (t.type.equals(Transaction.TYPE_CHQ) || t.type.equals(Transaction.TYPE_PAY)) {
				t.cheque = cheques.get(t.description.trim());
				if (t.cheque==null)
					System.err.println("Did not find cheque "+t.description);
				else if (t.outPence!=null && !t.outPence.equals(t.cheque.amountPence)) 
					System.err.println("Cheque "+t.description+" amount does not match "+t.outPence+" vs "+t.cheque.amountPence);
			}
			
			ts.add(t);
		}
		br.close();
		return s;
	}
	/**
	 * @param string
	 * @return
	 */
	public static Integer toPence(String string) throws NumberFormatException {
		if (string.length()==0)
			return null;
		int ix = string.indexOf(".");
		int pence = 100*Integer.parseInt(ix<0 ? string : string.substring(0, ix));
		if (ix>=0) {
			int factor = 10;
			while ((++ix)<string.length()) {
				char c= string.charAt(ix);
				if (Character.isDigit(c)) {
					pence += factor*((int)(c-'0'));
					factor /= 10;
				}
				else
					throw new NumberFormatException("Not a digit after . in "+string);
			}
		}
		return pence;
	}
	/** get all types */
	public TreeSet<String> getTypes() {
		TreeSet<String> types = new TreeSet<String>();
		for (Transaction t: transactions) {
			types.add(t.type);
		}
		return types;
	}
	/** get all types */
	public TreeSet<String> getClassifications() {
		TreeSet<String> types = new TreeSet<String>();
		for (Transaction t: transactions) {
			types.add(t.classify());
		}
		return types;
	}
	/** get all years */
	public TreeSet<Integer> getYears() {
		TreeSet<Integer> years = new TreeSet<Integer>();
		for (Transaction t: transactions) {
			years.add(t.year);
		}
		return years;
	}
}
