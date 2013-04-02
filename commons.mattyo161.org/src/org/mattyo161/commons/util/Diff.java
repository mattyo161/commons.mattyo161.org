package org.mattyo161.commons.util;

import java.io.PrintStream;

public class Diff {
    public class EditScript {
        public final static int DELETE = 1;
        public final static int INSERT = 2;

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

            return "Edit: " + opString + " line1: " + line1 + " line2: "
                    + line2 + " obj:" + object;
        }
    }

    public static int MAXFILE = 2000;
    public static int ORIGIN = MAXFILE;

    public void compare(PrintStream out, Object[] in1, Object[] in2) {
       int col, distance, lower, k, m, maxDistance, n, row, upper;

       int[] last_d = new int[2*MAXFILE+1];
       EditScript[] script = new EditScript[2*MAXFILE+1];
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
           out.println("Files identical");
           return;
       }

       for (distance = 1; distance <= maxDistance; distance++) {
           // for each relevant diagonal
           for (k = lower; k <= upper; k+=2) {
               newEdit = new EditScript();

               // Move down from last d-1 on diagonal k+1 puts you
               // further along diagonal k than does moving right from last d-1
               // on diagonal k-1
               boolean moveDownBeatsMoveRight = (k == ORIGIN - distance) || k != ORIGIN+distance && last_d[k+1] >= last_d[k-1];

               if (moveDownBeatsMoveRight) {
                   row = last_d[k+1] + 1;
                   newEdit.link = script[k+1];
                   newEdit.op = EditScript.DELETE;
                   newEdit.object = in1[row-1];
               } else {
                   // move right from last d-1 on diagonal k-1
                   row = last_d[k-1];
                   newEdit.link = script[k-1];
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

               if (row == m && col == n) {         // hit southeast corner, have the answer
                   print(out, script[k], in1, in2);
                   return;
               }

               if (row == m)  // hit last row, don't look to the left
                   lower = k+2;

               if (col == n)     // hit last column; don't look to the right
                   upper = k-2;
           }

           lower--;
           upper++;
       }
       exceeds(out, distance);
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

    private void print(
            PrintStream out,
            EditScript start,
            Object[] in1,
            Object[] in2)
    {
        EditScript script = reverse(start);

        while (script != null) {
            if (script.op == EditScript.INSERT)
                out.println("Insert after line " + script.line1 + ":\t" + in2[script.line2 - 1]);
            else {
                EditScript next = script.link;

                boolean change = next != null && next.op == EditScript.INSERT
                        && next.line1 == script.line1;

                if (change) {
                    out.println("Change line " + script.line1 + " from "
                            + in1[script.line1 - 1] + " to "
                            + in2[next.line2 - 1]);
                    script = script.link; // skip insert

                } else
                    out.println("Delete line " + script.line1 + ":\t"
                            + in1[script.line1 - 1]);
            }
            script = script.link;
        }
    }

    private void exceeds(PrintStream out, int d) {
        out.println("At least " + d + " line(s) inserted or deleted");
    }
}