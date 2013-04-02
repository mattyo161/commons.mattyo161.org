package org.mattyo161.commons.util;

import java.io.PrintStream;
import java.sql.Array;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DiffString {
	public class EditScript {
		public final static int DELETE = 1;

		public final static int INSERT = 2;

		public final static int EXCEED = 3;

		int op;

		int line1;

		int line2;

		Object object;

		EditScript link;

		public String toString() {
			String opString = "UNKNOWN";
			if (op == 1)
				opString = "DELETE";
			if (op == 2)
				opString = "INSERT";

			return "Edit: " + opString + " line1: " + line1 + " line2: " + line2 + " obj:" + object;
		}
	}

	public static int MAXFILE = 2000;

	public static int ORIGIN = MAXFILE;

	private Object[] in1 = null;
	private Object[] in2 = null;
	private EditScript[] script = new EditScript[2 * MAXFILE + 1];
	private EditScript start = null;

	public DiffString() {
		super();
	}

	public DiffString(String str1, String str2) {
		in1 = str1.split("");
		in2 = str2.split("");
	}

	/**
	 * Clear the script from any old entries
	 *
	 */
	private void clear() {
		this.start = null;
		for (int i = 0; i < script.length; i++) {
			if (script[i] != null) {
				script[i] = null;
			}
		}
	}

	public static String quickCompare(String str1, String str2) {
		DiffString diff = new DiffString(str1, str2);
		diff.compare();
		return diff.getDiffString();
	}

	public String getDiffString() {
		String returnString = new String("");
		Object[] newString = ArrayUtils.clone(in1);

		// The reverse will have to be done in the compare code
//		EditScript script = reverse(this.script[this.start]);
		EditScript currScript = this.start;
		while (currScript != null) {
			if (currScript.op == EditScript.EXCEED) {
				return "Differences exceed maximum.";
			} else if (currScript.op == EditScript.INSERT) {
				newString[currScript.line1 - 1] = newString[currScript.line1 - 1] + "(" + in2[currScript.line2 - 1] + ")";
				//out.println("Insert after line " + script.line1 + ":\t" + in2[script.line2 - 1]);
			} else {
				EditScript next = currScript.link;

				boolean change = next != null && next.op == EditScript.INSERT && next.line1 == currScript.line1;

				if (change) {
					newString[currScript.line1 - 1] = "{" + in2[next.line2 - 1] + "}";
//					out.println("Change line " + script.line1 + " from " + in1[script.line1 - 1] + " to " + in2[next.line2 - 1]);
					currScript = currScript.link; // skip insert

				} else
					newString[currScript.line1 - 1] = "[" + in1[currScript.line1 - 1] + "]";
//					out.println("Delete line " + script.line1 + ":\t" + in1[script.line1 - 1]);
			}
			currScript = currScript.link;
		}

		returnString = StringUtils.join(newString,"")
			.replaceAll("\\)\\(|\\}\\{|\\]\\[","")
			.replaceAll("\\n","\\\\n")
			.replaceAll("\\r","\\\\r")
			.replaceAll("\\t","\\\\t");

		return returnString;
	}


	public String getDiffOnlyString() {
		String returnString = new String("");
		Object[] newString = new Object[in1.length];
		// now fill it with spaces
		for (int i = 0; i < newString.length; i++) {
			newString[i] = " ";
		}

		// The reverse will have to be done in the compare code
//		EditScript script = reverse(this.script[this.start]);
		EditScript currScript = this.start;
		while (currScript != null) {
			if (currScript.op == EditScript.EXCEED) {
				return "Differences exceed maximum.";
			} else if (currScript.op == EditScript.INSERT) {
				newString[currScript.line1 - 1] = newString[currScript.line1 - 1] + "(" + in2[currScript.line2 - 1] + ")";
				//out.println("Insert after line " + script.line1 + ":\t" + in2[script.line2 - 1]);
			} else {
				EditScript next = currScript.link;

				boolean change = next != null && next.op == EditScript.INSERT && next.line1 == currScript.line1;

				if (change) {
					newString[currScript.line1 - 1] = "{" + in2[next.line2 - 1] + "}";
//					out.println("Change line " + script.line1 + " from " + in1[script.line1 - 1] + " to " + in2[next.line2 - 1]);
					currScript = currScript.link; // skip insert

				} else
					newString[currScript.line1 - 1] = "[" + in1[currScript.line1 - 1] + "]";
//					out.println("Delete line " + script.line1 + ":\t" + in1[script.line1 - 1]);
			}
			currScript = currScript.link;
		}

		returnString = StringUtils.join(newString,"")
			.replaceAll(" +"," ")
			.replaceAll("\\)\\(|\\}\\{|\\]\\[","")
			.replaceAll("\\n","\\\\n")
			.replaceAll("\\r","\\\\r")
			.replaceAll("\\t","\\\\t");

		return returnString.trim();
	}


	public int getDiffCount() {
		int diffCount = 0;

		EditScript currScript = this.start;
		while (currScript != null) {
			if (currScript.op == EditScript.EXCEED) {
				return MAXFILE;
			} else if (currScript.op == EditScript.INSERT) {
				diffCount++;
			} else {
				EditScript next = currScript.link;

				boolean change = next != null && next.op == EditScript.INSERT && next.line1 == currScript.line1;

				if (change) {
					diffCount++;
					currScript = currScript.link; // skip insert
				} else {
					diffCount++;
				}
			}
			currScript = currScript.link;
		}
		return diffCount;
	}

	public void compare(String str1, String str2) {
		in1 = str1.split("");
		in2 = str2.split("");
		compare();
	}

	public void compare() {
		int col, distance, lower, k, m, maxDistance, n, row, upper;

		int[] last_d = new int[2 * MAXFILE + 1];
//		EditScript[] script = new EditScript[2 * MAXFILE + 1];
		clear();
		EditScript newEdit;

		maxDistance = 2 * MAXFILE;

		m = in1.length;
		n = in2.length;

		for (row = 0; row < m && row < n && in1[row].equals(in2[row]); row++)
			;
		last_d[ORIGIN] = row;
		script[ORIGIN] = null;

		lower = (row == m) ? ORIGIN + 1 : ORIGIN - 1;
		upper = (row == n) ? ORIGIN - 1 : ORIGIN + 1;

		if (lower > upper) {
			return;
//			return script[0];
//			out.println("Files identical");
//			return;
		}

		for (distance = 1; distance <= maxDistance; distance++) {
			// for each relevant diagonal
			for (k = lower; k <= upper; k += 2) {
				newEdit = new EditScript();

				// Move down from last d-1 on diagonal k+1 puts you
				// further along diagonal k than does moving right from last d-1
				// on diagonal k-1
				boolean moveDownBeatsMoveRight = (k == ORIGIN - distance) || k != ORIGIN + distance && last_d[k + 1] >= last_d[k - 1];

				if (moveDownBeatsMoveRight) {
					row = last_d[k + 1] + 1;
					newEdit.link = script[k + 1];
					newEdit.op = EditScript.DELETE;
					newEdit.object = in1[row - 1];
				} else {
					// move right from last d-1 on diagonal k-1
					row = last_d[k - 1];
					newEdit.link = script[k - 1];
					newEdit.op = EditScript.INSERT;
					newEdit.object = in2[row + k - ORIGIN - 1];
				}

				newEdit.line1 = row;
				col = row + k - ORIGIN;
				newEdit.line2 = col;
				script[k] = newEdit;

				// slide down the diagonal
				while (row < m && col < n && in1[row].equals(in2[col])) {
					row++;
					col++;
				}
				last_d[k] = row;

				if (row == m && col == n) { // hit southeast corner, have the
											// answer
					// we need to reverse the order
					this.start = reverse(script[k]);
					return;
//					return script[k];
//					print(out, script[k], in1, in2);
//					return;
				}

				if (row == m) // hit last row, don't look to the left
					lower = k + 2;

				if (col == n) // hit last column; don't look to the right
					upper = k - 2;
			}

			lower--;
			upper++;
		}
		// this should throw an exception
		script[0].op = EditScript.EXCEED;
		this.start = script[0];
		return;
//		return script[0];
	}

	private EditScript reverse(EditScript start) {
		EditScript ahead;
		EditScript behind;
		EditScript result;

		ahead = start;
		result = null;
		while (ahead != null) {
			behind = result;
			result = ahead;
			ahead = ahead.link;
			result.link = behind; // flip the pointer
		}
		return result;
	}

	private void print(PrintStream out, EditScript start, Object[] in1, Object[] in2) {

		EditScript script = reverse(start);

		while (script != null) {
			if (script.op == EditScript.INSERT)
				out.println("Insert after line " + script.line1 + ":\t" + in2[script.line2 - 1]);
			else {
				EditScript next = script.link;

				boolean change = next != null && next.op == EditScript.INSERT && next.line1 == script.line1;

				if (change) {
					out.println("Change line " + script.line1 + " from " + in1[script.line1 - 1] + " to " + in2[next.line2 - 1]);
					script = script.link; // skip insert

				} else
					out.println("Delete line " + script.line1 + ":\t" + in1[script.line1 - 1]);
			}
			script = script.link;
		}
	}

	private void exceeds(PrintStream out, int d) {
		out.println("At least " + d + " line(s) inserted or deleted");
	}
}