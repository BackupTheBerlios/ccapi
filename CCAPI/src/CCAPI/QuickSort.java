package CCAPI;

// Sample code file: QuickSort.java

// Warning: This code has been marked up for HTML
 

/****************************************************************************
  $Workfile: QuickSort.java $
  $Revision: 1.1 $
  $Modtime:: $
  $Copyright:

  Copyright (c) 1997 Novell, Inc.  All Rights Reserved.

  THIS WORK IS  SUBJECT  TO  U.S.  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
  TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
  COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
  EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
  WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION
  OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
  CRIMINAL AND CIVIL LIABILITY.$

 ***************************************************************************/

import java.util.Vector;

public class QuickSort
{
    // Sorts entire array
    public static void sort(Vector array)
    {
        sort(array, 0, array.size() - 1);
    }

    // Sorts partial array
    public static void sort(Vector array, int start, int end)
    {
        int p;
        if (end > start)
        {
            p = partition(array, start, end);
            sort(array, start, p-1);
            sort(array, p+1, end);
        }
    }

    protected static int compare(Sortable a, Sortable b)
    {
       return a.compare(b);
    }

    protected static int partition(Vector array, int start, int end)
    {
        int left, right;
        Sortable partitionElement;

        // Arbitrary partition start...there are better ways...
        partitionElement = (Sortable)array.elementAt(end);

        left = start - 1;
        right = end;
        for (;;)
        {
            while (compare(partitionElement, (Sortable)array.elementAt(++left)) == 1)
            {
                if (left == end) break;
            }
            while (compare(partitionElement, (Sortable)array.elementAt(--right)) == -1)
            {
                if (right == start) break;
            }
            if (left >= right) break;
            swap(array, left, right);
        }
        swap(array, left, end);

        return left;
    }

    protected static void swap(Vector array, int i, int j)
    {
        Object temp;

        temp = array.elementAt(i);
        array.setElementAt(array.elementAt(j), i);
        array.setElementAt(temp, j);
    }
}
 
