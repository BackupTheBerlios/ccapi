package CCAPI;
// Sample code file: Sortable.java

// Warning: This code has been marked up for HTML
 

/****************************************************************************
  $Workfile: Sortable.java $
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

public interface Sortable
{
   /* Return -1 if self less than other,
    *         0 if same,
    *         1 if self greater than other
    */
   public int compare (Object other);
} // class Sortable
