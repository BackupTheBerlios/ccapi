/**
 * Created on 26.02.2005
 * 
 * GPL protected. Author: Ulrich Staudinger
 *  
 */

package CCAPI;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

public class DatabaseLayer {

	SimpleDateFormat sdf;

	public DatabaseLayer() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			String l = ex.toString();
		}

		sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		connect();
	}

	Connection connection;

	void connect() {
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://192.168.40.122/test", "root", "root");
			System.out.println("Database connected.");
		} catch (SQLException ex) {
			ex.printStackTrace();
			String l = "";
			l += ("SQLException: " + ex.getMessage());
			l += ("SQLState: " + ex.getSQLState());
			l += ("VendorError: " + ex.getErrorCode());
			System.exit(0);
		}
	}

	void disconnect() {

		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void initializePortfolio() {
		try {
			Statement stmt = connection.createStatement();

			String statement = "drop table if exists portfolio;";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			statement = "CREATE table portfolio (wkn varchar(255), type varchar(255), price_buy float, date_buy varchar(255), price_sell float, date_sell varchar(255), stoplevel float, change_percent float, change_net float , cash float);";

			i = stmt.executeUpdate(statement);
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void initialize() {
		if (connection != null) {
			int i = 0;
			try {
				// assume conn is an already created JDBC connection
				Statement stmt = null;

				String statement = "drop table if exists eurodata_tenminute;";

				stmt = connection.createStatement();
				i = stmt.executeUpdate(statement);

				statement = "drop table if exists eurodata_onehour;";

				stmt = connection.createStatement();
				i = stmt.executeUpdate(statement);

				statement = "drop table if exists dax_onehour;";

				stmt = connection.createStatement();
				i = stmt.executeUpdate(statement);

				statement = "drop table if exists dax_tenminute;";

				stmt = connection.createStatement();
				i = stmt.executeUpdate(statement);

				statement = "drop table if exists dax_threeminute;";

				stmt = connection.createStatement();
				i = stmt.executeUpdate(statement);

				statement = "drop table if exists portfolio;";

				stmt = connection.createStatement();
				i = stmt.executeUpdate(statement);

				statement = "CREATE table portfolio (wkn varchar(255), type varchar(255), price_buy float, date_buy varchar(255), price_sell float, date_sell varchar(255), stoplevel float, change_percent float, change_net float , cash float);";

				i = stmt.executeUpdate(statement);

				statement = "CREATE table dax_onehour (datestring varchar(255), open float, hi float, low float, close float, SMA_10 float, SMA_40 float);";

				i = stmt.executeUpdate(statement);

				statement = "CREATE table dax_threeminute (datestring varchar(255), open float, hi float, low float, close float, SMA_10 float, SMA_40 float);";

				i = stmt.executeUpdate(statement);

				statement = "CREATE table dax_tenminute (datestring varchar(255), open float, hi float, low float, close float, SMA_10 float, SMA_40 float);";

				i = stmt.executeUpdate(statement);

				statement = "CREATE table eurodata_tenminute (datestring varchar(255), open float, hi float, low float, close float, SMA_10 float, SMA_40 float);";

				i = stmt.executeUpdate(statement);

				statement = "CREATE table eurodata_onehour (datestring varchar(255), open float, hi float, low float, close float, SMA_10 float, SMA_40 float);";
				i = stmt.executeUpdate(statement);

				System.out.println("Database initialized.");

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Not connected to database. ");
		}

	}

	public void saveTenMinuteCandleDax(Candle c) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "insert into dax_tenminute (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void saveThreeMinuteCandleDax(Candle c) {
		disconnect();
		connect();
		System.out.println("Saving three minute candle dax." + c.toString());
		try {
			Statement stmt = null;

			String statement = "insert into dax_threeminute (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveHourCandleDax(Candle c) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "insert into dax_onehour (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveTenMinuteCandle(Candle c) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "insert into eurodata_tenminute (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveHourCandle(Candle c) {
		disconnect();
		connect();
		try {

			Statement stmt = null;

			String statement = "insert into eurodata_onehour (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + ");";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public Vector getHourCandles(java.util.Date start, java.util.Date end) {
		Vector ret = new Vector();
		return ret;
	}

	public Vector getTenMinuteCandles(java.util.Date start, java.util.Date end) {
		
		
		disconnect();
		connect();
		Vector ret = new Vector();

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * returns the last *howmany* hour candles
	 * 
	 * @param howmany
	 * @return
	 */
	public Vector getHourCandles(int howmany) {
		disconnect();
		connect();
		Vector ret = new Vector();

		try {

			Statement stmt = null;
			String statement = "select  datestring, open, hi, low, close from eurodata_onehour order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			Vector tempvector = new Vector();
			int k = 0;
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				c.date = sdf.parse(rs.getString(1));
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			if (k > howmany) {
				for (int i = 0; i < k; i++) {
					ret.addElement(tempvector.elementAt(tempvector.size() - 1
							- i));
				}
			} else {
				return tempvector;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public int candlesTenMinuteDatabase() {
		disconnect();
		connect();
		int k = 0;
		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from eurodata_tenminute order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();

			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.date = sdf.parse(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}
		} catch (Exception e) {
		}

		return k;
	}

	/**
	 * returns the last *howmany* ten minute candles
	 * 
	 * @param howmany
	 * @return
	 */
	public Vector getTenMinuteCandles(int howmany) {
		disconnect();
		connect();

		Vector ret = new Vector();

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from eurodata_tenminute order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			//int cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.date = sdf.parse(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			if (k > howmany) {
				for (int i = 0; i < howmany; i++) {
					Candle c = (Candle) tempvector.elementAt(tempvector.size()
							- 1 - i);

					ret.addElement(c);
				}
			} else {
				return tempvector;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	void export() {
		disconnect();
		connect();
		try {

			Statement stmt = null;
			String statement = "select  datestring, open, hi, low, close from eurodata_tenminute order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			String s = "";
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);
				s += c.date.toGMTString() + ";" + c.open + ";" + c.hi + ";"
						+ c.low + ";" + c.open + ";\n";

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			//save the tempvector into a file.

			File f = new File("euro_tenminute.csv");
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from eurodata_onehour  order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			String s = "";
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);
				s += c.date.toGMTString() + ";" + c.open + ";" + c.hi + ";"
						+ c.low + ";" + c.open + ";\n";

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			//save the tempvector into a file.

			File f = new File("euro_onehour.csv");
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from dax_threeminute;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			String s = "";
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);
				s += c.date.toGMTString() + ";" + c.open + ";" + c.hi + ";"
						+ c.low + ";" + c.open + ";\n";

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			//save the tempvector into a file.

			File f = new File("dax_threeminute.csv");
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from dax_tenminute;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			String s = "";
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);
				s += c.date.toGMTString() + ";" + c.open + ";" + c.hi + ";"
						+ c.low + ";" + c.open + ";\n";

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			//save the tempvector into a file.

			File f = new File("dax_tenminute.csv");
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from dax_onehour;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			String s = "";
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);
				s += c.date.toGMTString() + ";" + c.open + ";" + c.hi + ";"
						+ c.low + ";" + c.open + ";\n";

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			//save the tempvector into a file.

			File f = new File("dax_onehour.csv");
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Vector getOneHourCandlesDax(int howmany) {

		disconnect();
		connect();
		Vector ret = new Vector();

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from dax_onehour order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				c.date = sdf.parse(rs.getString(1));

				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			if (k > howmany) {
				for (int i = 0; i < howmany; i++) {
					ret.addElement(tempvector.elementAt(tempvector.size() - 1
							- i));
				}
			} else {
				return tempvector;
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return ret;
	}

	public Vector getOneHourCandlesDaxFuture(int howmany) {
		disconnect();
		connect();
		Vector ret = new Vector();

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from daxfuture_onehour order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				c.date = sdf.parse(rs.getString(1));
				//System.out.println("Date: "+c.date.toGMTString());
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			if (k > howmany) {
				for (int i = 0; i < howmany; i++) {
					ret.addElement(tempvector.elementAt(tempvector.size() - 1
							- i));
				}
			} else {
				return tempvector;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public Vector getFiveMinuteCandlesDaxFuture(int howmany) {
		disconnect();
		connect();
		Vector ret = new Vector();

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from daxfuture_fiveminute order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				c.date = sdf.parse(rs.getString(1));
				//System.out.println("Date: "+c.date.toGMTString());
				//c.date = new java.util.Date(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			if (k > howmany) {
				for (int i = 0; i < howmany; i++) {
					ret.addElement(tempvector.elementAt(tempvector.size() - 1
							- i));
				}
			} else {
				return tempvector;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public Vector getTenMinuteCandlesDax(int howmany) {
		disconnect();
		connect();
		Vector ret = new Vector();

		try {

			Statement stmt = null;
			String statement = "select datestring, open, hi, low, close from dax_tenminute order by datestring;";
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			Vector tempvector = new Vector();
			int k = 0;
			//t cc=rsmd.getColumnCount();
			while (rs.next()) {
				//data found
				Candle c = new Candle();
				//c.date = new java.util.Date(rs.getString(1));
				c.date = sdf.parse(rs.getString(1));
				c.open = rs.getDouble(2);
				c.hi = rs.getDouble(3);
				c.low = rs.getDouble(4);
				c.close = rs.getDouble(5);

				tempvector.addElement(c);
				//System.out.println(c.toString());
				k++;
			}

			if (k > howmany) {
				for (int i = 0; i < howmany; i++) {
					ret.addElement(tempvector.elementAt(tempvector.size() - 1
							- i));
				}
			} else {
				return tempvector;
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * converts from old date syntax to new one ...
	 */

	public void convert() {
		disconnect();
		connect();
		try {

			String statement = "select * from dax_threeminute;";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			while (rs.next()) {
				try {
					//	data found
					Candle c = new Candle();
					c.date = new java.util.Date(rs.getString(1));
					c.open = rs.getDouble(2);
					c.hi = rs.getDouble(3);
					c.low = rs.getDouble(4);
					c.close = rs.getDouble(5);
					//replace the current entry
					try {
						String st2 = "update dax_threeminute set datestring='"
								+ sdf.format(c.date) + "' where datestring='"
								+ rs.getString(1) + "';";

						Statement stmt2 = connection.createStatement();
						int i = stmt.executeUpdate(st2);
						System.out.println("converted");

						if (stmt2 != null)
							stmt2.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			String statement = "select * from dax_tenminute;";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			while (rs.next()) {
				try {
					//	data found
					Candle c = new Candle();
					c.date = new java.util.Date(rs.getString(1));
					c.open = rs.getDouble(2);
					c.hi = rs.getDouble(3);
					c.low = rs.getDouble(4);
					c.close = rs.getDouble(5);
					//replace the current entry
					try {
						String st2 = "update dax_tenminute set datestring='"
								+ sdf.format(c.date) + "' where datestring='"
								+ rs.getString(1) + "';";

						Statement stmt2 = connection.createStatement();
						int i = stmt.executeUpdate(st2);
						System.out.println("converted");

						if (stmt2 != null)
							stmt2.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			String statement = "select * from dax_onehour;";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			while (rs.next()) {
				try {
					//	data found
					Candle c = new Candle();
					c.date = new java.util.Date(rs.getString(1));
					c.open = rs.getDouble(2);
					c.hi = rs.getDouble(3);
					c.low = rs.getDouble(4);
					c.close = rs.getDouble(5);
					//replace the current entry
					try {
						String st2 = "update dax_onehour set datestring='"
								+ sdf.format(c.date) + "' where datestring='"
								+ rs.getString(1) + "';";

						Statement stmt2 = connection.createStatement();
						int i = stmt.executeUpdate(st2);
						System.out.println("converted");

						if (stmt2 != null)
							stmt2.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			String statement = "select * from eurodata_onehour;";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			while (rs.next()) {
				try {
					//	data found
					Candle c = new Candle();
					c.date = new java.util.Date(rs.getString(1));
					c.open = rs.getDouble(2);
					c.hi = rs.getDouble(3);
					c.low = rs.getDouble(4);
					c.close = rs.getDouble(5);
					//replace the current entry
					try {
						String st2 = "update eurodata_onehour set datestring='"
								+ sdf.format(c.date) + "' where datestring='"
								+ rs.getString(1) + "';";

						Statement stmt2 = connection.createStatement();
						int i = stmt.executeUpdate(st2);
						System.out.println("converted");

						if (stmt2 != null)
							stmt2.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			String statement = "select * from eurodata_tenminute;";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			while (rs.next()) {
				try {
					//	data found
					Candle c = new Candle();
					c.date = new java.util.Date(rs.getString(1));
					c.open = rs.getDouble(2);
					c.hi = rs.getDouble(3);
					c.low = rs.getDouble(4);
					c.close = rs.getDouble(5);
					//replace the current entry
					try {
						String st2 = "update eurodata_tenminute set datestring='"
								+ sdf.format(c.date)
								+ "' where datestring='"
								+ rs.getString(1) + "';";

						Statement stmt2 = connection.createStatement();
						int i = stmt.executeUpdate(st2);
						System.out.println("converted");

						if (stmt2 != null)
							stmt2.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * portfolio management routines on database level are following here.
	 * 
	 * statement = "CREATE table portfolio (wkn varchar(255), type varchar(255),
	 * price_buy float, date_buy varchar(255), price_sell float, date_sell
	 * varchar(255), stoplevel float, change_percent float, change_net float ,
	 * cash float);";
	 *  
	 */

	public double getCash() {
		disconnect();
		connect();
		try {

			Statement stmt = null;
			String statement = "select sum(cash) from portfolio;";

			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			double cash = 0.0;
			if (rs.next()) {
				cash = rs.getDouble(1);
			}
			if (stmt != null)
				stmt.close();

			return cash;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return 0.0;

	}

	/**
	 * updates the stop level on database layer. means the stop column is
	 * updated.
	 * 
	 * @param evaluation
	 * @param level
	 */
	public void updateStop(String name, double level) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "update portfolio set stoplevel=" + level
					+ " where type='" + name + "' and price_sell IS NULL;";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getStop(String name) {

		try {

			Statement stmt = null;
			String statement = "select stoplevel from portfolio where type='"
					+ name + "' AND price_sell IS NULL;";

			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			double stop = 0.0;
			if (rs.next()) {
				stop = rs.getDouble(1);
			}
			if (stmt != null)
				stmt.close();

			return stop;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;

	}

	public double getPriceBuy(String name) {
		disconnect();
		connect();

		try {

			Statement stmt = null;
			String statement = "select price_buy from portfolio where type='"
					+ name + "' AND price_sell IS NULL;";

			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			double price = 0.0;
			if (rs.next()) {
				price = rs.getDouble(1);
			}
			if (stmt != null)
				stmt.close();

			return price;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;

	}

	public String getWKN(String name) {
		disconnect();
		connect();

		try {

			Statement stmt = null;
			String statement = "select wkn from portfolio where type='" + name
					+ "' AND price_sell IS NULL;";

			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);

			String wkn = "";
			if (rs.next()) {
				wkn = rs.getString(1);
			}
			if (stmt != null)
				stmt.close();

			return wkn;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	public boolean positionOpen(String name) {
		disconnect();
		connect();
		try {

			String st1 = "select price_buy from portfolio where type='" + name
					+ "' AND price_sell IS NULL;";

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(st1);

			if (rs.next()) {

				if (stmt != null)
					stmt.close();

				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// been pizza driving on 8.feb.2004.

	/**
	 * closes the position on database layer.
	 * 
	 * @param evaluation
	 * @param price
	 */
	public void closePosition(String name, double price) {
		//the open position has price_sell=NULL and date_sell=NULL!

		
		disconnect();
		connect();
		double transactionfee = 19.95;

		try {

			String st1 = "select price_buy from portfolio where type='" + name
					+ "' AND price_sell IS NULL;";

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(st1);
			if (rs.next()) {

				System.out.println("Open position selected.  ");

				//get the old buy price.
				double buyprice = rs.getDouble(1);
				double change = price - buyprice;
				double percentchange = (price / buyprice) * 100 - 100;
				double cash = change * 1000 - transactionfee;

				String st2 = "update portfolio set price_sell=" + price
						+ ", change_net=" + change + ", change_percent="
						+ percentchange + ", cash=" + cash + ", date_sell='"
						+ sdf.format(new java.util.Date()) + "' "
						+ " where price_sell IS NULL and type='" + name + "';";

				Statement stmt2 = connection.createStatement();
				int i = stmt.executeUpdate(st2);
				System.out.println("Open Position closed!");

				if (stmt != null)
					stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * opens the position on database layer
	 * 
	 * @param evaluation
	 * @param wkn
	 * @param price
	 */
	public void openPosition(String name, String wkn, double price, double stop) {
		disconnect();
		connect();
		java.util.Date d = new java.util.Date();

		try {

			String statement = "insert into portfolio(wkn,  type, price_buy, date_buy, stoplevel)  values ('"
					+ wkn
					+ "',"
					+ " '"
					+ name
					+ "', "
					+ price
					+ ", '"
					+ sdf.format(new java.util.Date()) + "', " + stop + ");";

			Statement stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

			System.out.println("Position opened on database layer. ");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// future routines.
	public void saveThreeMinuteCandleDaxFuture(Candle c) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "replace daxfuture_threeminuter (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// future routines.
	public void saveFiveMinuteCandleDaxFuture(Candle c) {
		disconnect();
		connect();
		boolean retry = true;
		while (retry) {
			try {
				Statement stmt = null;

				String statement = "replace daxfuture_fiveminute (datestring, open, hi, low, close) values('"
						+ sdf.format(c.date)
						+ "', "
						+ c.open
						+ ", "
						+ c.hi
						+ ", " + c.low + ", " + c.close + " );";

				stmt = connection.createStatement();
				int i = stmt.executeUpdate(statement);

				if (stmt != null)
					stmt.close();

				retry = false;

			} catch (Exception e) {
				e.printStackTrace();

				this.disconnect();
				this.connect();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				retry = true;
			}
		}
	}

	// future routines.
	public void saveTenMinuteCandleDaxFuture(Candle c) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "replace daxfuture_tenminute (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// future routines.
	public void saveHourCandleDaxFuture(Candle c) {
		disconnect();
		connect();
		try {
			Statement stmt = null;

			String statement = "replace daxfuture_onehour (datestring, open, hi, low, close) values('"
					+ sdf.format(c.date)
					+ "', "
					+ c.open
					+ ", "
					+ c.hi
					+ ", "
					+ c.low + ", " + c.close + " );";

			stmt = connection.createStatement();
			int i = stmt.executeUpdate(statement);

			if (stmt != null)
				stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
    public Vector loadHistory(String wkn, int skipdays) {
        Vector ret = new Vector();
        Vector t=new Vector();
        
        System.out.println("StockSymbolEntityBean: Loading from database");
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.40.122:3306/test?user=root&password=root");
            //create the statement
            Statement stmt = con.createStatement();
            try {
                //first check if we have a timestamp already
                String query="select * from symboldata where wkn='"+(wkn)+"' order by timestamp;";
                System.out.println(query);
                ResultSet rs = stmt
                        .executeQuery(query);

                if(rs!=null){
                    while (rs.next()) {
                        //System.out.println("Adding a candle.");
                        Candle c = new Candle();
                        c.isin = rs.getString(1);
                        c.datestring = rs.getString(2);
                        c.open = rs.getDouble(3);
                        c.hi = rs.getDouble(4);
                        c.low = rs.getDouble(5);
                        c.close = rs.getDouble(6);
                        c.volume = rs.getInt(7);
                        c.wkn = rs.getString(8);
                        t.add(c);
                    }
                }
                else{
                    System.out.println("rs == null");
                }
                for(int i=0;i<t.size();i++){
                    ret.add( (Candle)t.elementAt(t.size()-1-i-skipdays));
                }

                if(ret.size()>0){
                    System.out.println( ((Candle)ret.elementAt(0)).toString() ) ;
                }

            } catch (Exception e) {
                System.out.println("Inner try catch");
                e.printStackTrace();
            }
            
            con.close();
            
        } catch (Exception e) {
            System.out.println("Outer try catch");
            e.printStackTrace();
        }
        System.out.println("StockSymbolEntityBean: Loaded from database: "+ret.size()+" candles.");
        return ret;
    }


}