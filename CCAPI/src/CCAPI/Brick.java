package CCAPI;


/**
 * 
 * a brick in a renko chart
 * 
 * @author us
 *
 */
class Brick{
	/**
	 * hi of this brck. 
	 */
	double hi;
	/**
	 * low of this brick
	 */
	double low;
	/**
	 * the date of this brick
	 */
	java.util.Date date;
	/**
	 * holds color of brick with true=white and false=black.
	 */
	boolean white;  //white=true, black=false;
}
