package CCAPI;

import java.util.Vector;


/**
 *
 * A financial library implementation, provides technical analysis routines. please report all errors, extensions and bugfixes if possible. This class provides works mostly on close values of candles, but provides function to use open, hi, low instead of close, too.<br>
 Throughout the library skipdays is used to skip days from the used dataset. For example: to calculate the simple mean ten day average of two days ago, you need to call SMA(10, candles, 2); where candles contains your vector of candles. <br><br>
 REMINDER: Vector.elementAt(0) is the newest candle!!!

 *
 * @author us
 */
public class FinancialLibrary {
    /**
     * FFTfunctions - don't touch
     */
    private int n;

    /**
     * FFTfunctions - don't touch
     */
    private int nu;

    /**
     * returns the simple moving average.
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double SMA(int n, java.util.Vector candles, int skipdays) {
        double value = 0.0;

        for (int i = skipdays; i < (n + skipdays); i++) {
            Candle c = (Candle) candles.elementAt(i);

            value += c.close;
        }

        value /= n;

        return value;
    }

    /**
     * returns the exponential moving average
     * http://www.quotelinks.com/technical/ema.html
     *
     * @param n
     * @param candles
     * @param skipdays
     * @param r
     * @return
     */
    public double EMA(int n, Vector candles, int skipdays) {
        double value = 0;

        double exponent = 2 / (double) (n + 1);

        Candle cf = (Candle) candles.elementAt(candles.size() - 1);

        value = cf.close * exponent;

        for (int i = skipdays; i < (candles.size() - skipdays); i++) {
            Candle c = (Candle) candles.elementAt(candles.size() - i - 1);

            value = (c.close * exponent) + (value * (1 - exponent));

            // System.out.println("Value:"+value);
        }

        return value;
    }

    /**
     * returns the momentum
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double Momentum(int n, Vector candles, int skipdays) {
        double value = 0.0;

        Candle c0 = (Candle) candles.elementAt(skipdays);
        Candle c1 = (Candle) candles.elementAt(skipdays + n);

        value = (c0.close - c1.close) / c1.close;
        value *= 100;
        value += 100;

        return value;
    }

    /**
     * returns the roc
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double ROC(int n, Vector candles, int skipdays) {
        double value = 0.0;
        Candle c0 = (Candle) candles.elementAt(skipdays);
        Candle c1 = (Candle) candles.elementAt(skipdays + n);

        value = (c0.close - c1.close) / c1.close * 100;

        return value;
    }

    /**
     * returns the slope between two timepoints
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double slope(int n, Vector candles, int skipdays) {
        double value = 0.0;
        Candle c0 = (Candle) candles.elementAt(skipdays);
        Candle c1 = (Candle) candles.elementAt(skipdays + n);

        value = (c1.close - c0.close) / n;

        return value;
    }

    /**
     * returns a SMA smoothed slope
     * @param n
     * @param smoothingfactor
     * @param candles
     * @param skipdays
     * @return
     */
    public double smoothedSlope(int n, int smoothingunits, Vector candles,
        int skipdays) {
        double value = 0.0;
        Vector v1 = new Vector();

        for (int i = 0; i < (smoothingunits + 2); i++) {
            Candle c = new Candle();

            c.close = slope(n, candles, skipdays + i);
            v1.addElement(c);
        }

        value = SMA(smoothingunits, v1, 0);

        return value;
    }

    /**
     * returns the parabolic SAR - seems to be buggy
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double SAR(double af, double max, Vector candles, int skipdays) {
        double value = 0.0;

        double hi;
        double low;

        hi = low = 0;

        // find the first
        boolean uptrend = true;

        // check first trend
        Candle cref0 = (Candle) candles.elementAt(candles.size() - 1 -
                skipdays);
        Candle cref1 = (Candle) candles.elementAt(candles.size() - 1 -
                skipdays - 1);

        if (cref1.close > cref0.close) {
            uptrend = true;
        } else {
            uptrend = false;
        }

        // find first extreme point.
        boolean extremefound = false;

        int index = 0;

        while (!extremefound) {
            cref0 = (Candle) candles.elementAt(candles.size() - 1 - skipdays -
                    index);
            cref1 = (Candle) candles.elementAt(candles.size() - 1 - skipdays -
                    index - 1);

            if (uptrend) {
                if (cref1.close > cref0.close) {
                } else {
                    extremefound = true;

                    break;
                }
            } else {
                if (cref1.close < cref0.close) {
                } else {
                    extremefound = true;

                    break;
                }
            }

            index++;
        }

        if (cref1.close > cref0.close) {
            uptrend = true;
        } else {
            uptrend = false;
        }

        double currentstop = 0.0;
        double previousstop = 0.0;
        double extreme = 0.0;

        double currentacc = af;

        // set initial values
        if (uptrend) {
            extreme = cref0.close;
        } else {
            extreme = cref1.close;
        }

        // System.out.println("Starting at candle:" + startcandle);
        double _cs = af;

        for (int i = 0; i >= skipdays; i--) {
            Candle c = (Candle) candles.elementAt(i);

            if (uptrend) {
                value = value + (_cs * (c.hi - value));

                if (c.low < value) {
                    low = c.low;
                    uptrend = false;
                    _cs = af;
                } else {
                    if (c.hi > hi) {
                        hi = c.hi;

                        if (_cs < max) {
                            _cs += af;
                        }
                    }
                }
            } else {
                value = value + (_cs * (c.low - value));

                if (c.hi > value) {
                    hi = c.hi;
                    uptrend = true;
                    _cs = af;
                } else {
                    if (c.low < low) {
                        low = c.low;

                        if (_cs < max) {
                            _cs += af;
                        }
                    }
                }
            }
        }

        // System.out.println(
        // "Break - trend is at the start is in an up:" + uptrend);
        return value;
    }

    /**
     * returns the SAR direction as a bool - true if up and false if down.
     *
     * @param af
     * @param max
     * @param candles
     * @param skipdays
     * @return
     */
    public boolean SARd(double af, double max, Vector candles, int skipdays) {
        double value = 0.0;

        double hi;
        double low;

        hi = low = 0;

        // find the first
        boolean uptrend = true;
        int startcandle = 0;

        for (int i = 0; i < (candles.size() - skipdays); i++) {
            Candle c = (Candle) candles.elementAt(candles.size() - i - 1 -
                    skipdays);

            if (hi == 0) {
                hi = c.hi;
                low = c.low;
            } else {
                if ((c.hi > hi) & (c.low < low)) {
                    uptrend = true;
                    startcandle = candles.size() - i - 1;
                    i = 100000;

                    break;
                } else if ((c.hi < hi) & (c.low < low)) {
                    uptrend = false;
                    startcandle = candles.size() - i - 1;
                    i = 100000;

                    break;
                }
            }
        }

        double _cs = af;

        for (int i = startcandle; i >= skipdays; i--) {
            Candle c = (Candle) candles.elementAt(i);

            if (uptrend) {
                value = value + (_cs * (c.hi - value));

                if (c.low < value) {
                    low = c.low;
                    uptrend = false;
                    _cs = af;
                } else {
                    if (c.hi > hi) {
                        hi = c.hi;

                        if (_cs < max) {
                            _cs += af;
                        }
                    }
                }
            } else {
                value = value + (_cs * (c.low - value));

                if (c.hi > value) {
                    hi = c.hi;
                    uptrend = true;
                    _cs = af;
                } else {
                    if (c.low < low) {
                        low = c.low;

                        if (_cs < max) {
                            _cs += af;
                        }
                    }
                }
            }
        }

        return uptrend;
    }

    /**
     * returns the RSI
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double RSI(int n, Vector candles, int skipdays) {
        double U = 0.0;
        double D = 0.0;

        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(skipdays + i);
            Candle c1 = (Candle) candles.elementAt(skipdays + i + 1);
            double change = c.close - c1.close;

            if (change > 0) {
                U += change;
            } else {
                D += Math.abs(change);
            }
        }

        double ret = 0.0;

        try {
            ret = 100 - (100 / (1 + (U / D)));
        } catch (Exception e) {
        }

        return ret;
    }

    /**
     * returns the williams R indicator
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double wilr(int n, Vector candles, int skipdays) {
        double value = 0.0;

        // find the highest hi
        double hi = 0.0;
        double low = 1000000;

        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);

            if (c.close > hi) {
                hi = c.close;
            }

            if (c.low < low) {
                low = c.low;
            }
        }

        Candle c0 = (Candle) candles.elementAt(skipdays);

        value = (hi - c0.close) / (hi - low);
        value *= (-100);

        return value;
    }

    /**
     * returns fast stochastik trigger and signal in a
     * two-field double array. double[0] is trigger (%k) and double[1] is signal (%d).
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double[] fstoch(int k, int d, Vector candles, int skipdays) {
        double[] ret = { 0.0, 0.0 };

        Candle c0 = (Candle) candles.elementAt(skipdays);

        // find lowest low + highest hi
        double low = 1000000000;
        double hi = 0;

        for (int i = 0; i < k; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);

            hi = (c.hi > hi) ? c.hi : hi;
            low = (c.low < low) ? c.low : low;
        }

        double value = ((c0.close - low) / (hi - low)) * 100;

        System.out.println("hi/low:" + hi + "/" + low);

        ret[0] = value;

        // smooth it.
        double average = 0.0;

        for (int j = 0; j < (d); j++) {
            c0 = (Candle) candles.elementAt(j + skipdays);
            low = 1000000000;
            hi = 0;

            for (int i = 0; i < k; i++) {
                Candle c = (Candle) candles.elementAt(j + i + skipdays);

                hi = (c.hi > hi) ? c.hi : hi;
                low = (c.low < low) ? c.low : low;
            }

            value = ((c0.close - low) / (hi - low)) * 100;
            average += value;
        }

        ret[1] = average / d;

        return ret;
    }

    /**
     * not implemented - returns slow stochastik trigger and signal in a
     * two-field double array. double[0] is trigger and double[1] is signal.
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double[] sstoch(int trigger, Vector candles, int skipdays) {
        double[] ret = { 0.0, 0.0 };

        return ret;
    }

    /**
     * calculates the color of current renko brick returns +1 for white, -1 for
     * black and 0 for no new brick
     *
     * @param base
     * @param candles
     * @param skips
     */
    public int renko(double base, Vector candles, int skips) {
        Candle last = (Candle) candles.elementAt(candles.size() - 1);

        int i1 = (int) (last.close / base);

        // if(bricks.size()==0){
        double val = last.close;
        Vector bricks = new Vector();
        Brick b1 = new Brick();

        b1.date = last.date;
        b1.hi = (((double) i1 + 1) * base);
        b1.low = b1.hi - base;
        b1.white = true;

        // }
        bricks.addElement(b1);

        int ret = 0;

        for (int i = 0; i < candles.size(); i++) {
            Candle c0 = (Candle) candles.elementAt(candles.size() - i - 1);
            Brick current = (Brick) (bricks.elementAt(bricks.size() - 1));

            if (c0.close > (current.hi + base)) {
                while (c0.close > (current.hi + base)) {
                    double tempval = current.hi;

                    // add a white brick
                    Brick b = new Brick();

                    b.low = current.hi;
                    b.hi = b.low + base;
                    b.white = true;
                    b.date = c0.date;
                    bricks.add(b);

                    ret = 0;
                    current = b;
                }
            } else if (c0.close < (current.low - base)) {
                while (c0.close < (current.low - base)) {
                    // add a black brick
                    Brick b = new Brick();

                    b.hi = current.low;
                    b.low = b.hi - base;
                    b.white = false;
                    b.date = c0.date;
                    bricks.add(b);
                    ret = 0;

                    current = b;
                }
            } else {
                ret = -2;
            }
        }

        int indent = 20;

        System.out.println("");

        for (int i = 0; i < bricks.size(); i++) {
            b1 = (Brick) bricks.elementAt(i);

            if (b1.white) {
                indent++;

                for (int j = 0; j < indent; j++) {
                    System.out.print(" ");
                }

                System.out.print("X");
            } else {
                indent--;

                for (int j = 0; j < indent; j++) {
                    System.out.print(" ");
                }

                System.out.print("O");
            }

            System.out.print("           " + b1.low + "/" + b1.hi + "   /" +
                b1.date.toGMTString());
            System.out.println("");
        }

        try {
            Brick now = (Brick) bricks.elementAt(bricks.size() - 1);
            int i = 2;
            Brick prev = (Brick) bricks.elementAt(bricks.size() - i);

            while (prev.date.toGMTString().equals(now.date.toGMTString())) {
                i++;
                prev = (Brick) bricks.elementAt(bricks.size() - i);
            }

            if (ret != -2) {
                if (now.white & !prev.white) {
                    ret = 1;
                } else if (!now.white & prev.white) {
                    ret = -1;
                }
            } else {
                ret = 0;
            }

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * calculates the MACD
     *
     * @param p1
     * @param p2
     * @param signal
     * @param v
     * @param skipdays
     * @return
     */
    public double[] MACD(int p1, int p2, int signal, Vector v, int skipdays) {
        double[] v1 = new double[2];
        Vector temp = new Vector();

        for (int i = 0; i < 100; i++) {
            Candle c = new Candle();

            // System.out.println(i);
            c.close = EMA(p1, v, skipdays + i) - EMA(p2, v, skipdays + i);
            temp.addElement(c);
        }

        v1[0] = EMA(p1, v, skipdays) - EMA(p2, v, skipdays);
        v1[1] = EMA(signal, temp, 0);
        System.out.println("v0:" + v1[0]);
        System.out.println("v1:" + v1[1]);

        return v1;
    }

    /**
     * calculates the arms index. The Vector candlevectors MUST contain other
     * vectors which then contain symbol's candles. It is important to keep in
     * mind that arms requires intact volume.
     *
     * @param n
     * @param candlevectors
     * @param skipdays
     * @return
     */
    public double arms(int n, Vector candlevectors, int skipdays) {
        double value = 0.0;

        return value;
    }

    /**
     * returns the commodity change index
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double cci(int n, double factor, Vector candles, int skipdays) {
        double value = 0.0;

        Vector typicalprizes = new Vector();

        Candle c0 = (Candle) candles.elementAt(skipdays);
        double tr = (c0.hi + c0.low + c0.close) / 3;

        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);
            Candle c1 = new Candle();

            c1.close = (c.hi + c.low + c.close) / 3;

            typicalprizes.addElement(c1);
        }

        double v1 = this.SMA(n, typicalprizes, skipdays);

        // calculate the standard deviation
        double v2 = 0.0;

        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);

            v2 += Math.abs(v1 - ((c.hi + c.low + c.close) / 3));
        }

        v2 /= n;

        value = ((tr - v1) / (factor * v2));

        return value;
    }

    /**
     * returns the vhf value
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double vhf(int n, Vector candles, int skipdays) {
        double value = 0.0;
        double min = 1000000;
        double max = 0;

        // find highest and lowest price
        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);

            if (c.close < min) {
                min = c.close;
            }

            if (c.close > max) {
                max = c.close;
            }
        }

        double numerator = Math.abs(max - min);

        double denominator = 0.0;

        for (int i = 0; i < n; i++) {
            Candle c0 = (Candle) candles.elementAt(i + skipdays);
            Candle c1 = (Candle) candles.elementAt(i + skipdays + 1);

            denominator += Math.abs(c0.close - c1.close);
        }

        value = numerator / denominator;

        return value;
    }

    /**
     * returns the trix indicator
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double trix(int n, Vector candles, int skipdays) {
        double value = 0.0;

        return value;
    }

    /**
     * returns adx
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double adx(int n, Vector candles, int skipdays) {
        double value = 0.0;
        Vector adxv = new Vector();

        for (int i = 0; i < n; i++) {
            Candle c0 = new Candle();
            double[] v = (this.dmi(n, candles, skipdays + i));

            c0.close = (v[0] - v[1]) / (v[0] + v[1]) * 100;
            adxv.addElement(c0);
        }

        return EMA(n, adxv, 0);
    }

    /**
     * returns the dmi in an array - first value is dmi, second value is dmi+, third is dmi-
     *
     * links to DMI:
     * http://www.stockwerld.com/directional_body.htm
     * http://www.cqg.com/support/dmi.cfm
     * http://www.tradingmarkets.com/yahoo.site/stocks/education/traders/735C5C5674454359425A7658584A25131E
     *
     * calculation: http://www.akmos.com/software/afmcharts/indicators/dmi.html
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double[] dmi(int n, Vector candles, int skipdays) {
        double[] value = new double[3];

        Vector sdiplus = new Vector();
        Vector sdiminus = new Vector();

        double dm_p = 0.0;
        double dm_m = 0.0;

        // calculate the dmi
        double tr = 0;

        for (int i = 0; i < (n); i++) {
            Candle c0 = (Candle) candles.elementAt(i + skipdays);
            Candle c1 = (Candle) candles.elementAt(i + skipdays + 1);

            if (c0.hi > c1.hi) {
                dm_p += (c0.hi - c1.hi);
            } else {
                dm_p += 0;
            }

            if (c0.low < c1.low) {
                dm_m += (c0.low - c1.low);
            } else {
                dm_m += 0;
            }

            /*
             if(dm_m<dm_p)dm_m+=0;
             if(dm_p<dm_m)dm_p+=0;
             if(dm_m==dm_p){
             dm_m=dm_p=0;
             }
             */
            double sdi_p = 0;
            double sdi_m = 0;

            double tr_ = 0.0;
            double v1 = (c0.low - c1.close);
            double v2 = (c0.hi - c1.close);
            double v3 = (c0.hi - c0.low);

            if ((v1 > v2) & (v1 > v3)) {
                tr_ = v1;
            }

            if ((v2 > v1) & (v2 > v3)) {
                tr_ = v2;
            }

            if ((v3 > v1) & (v3 > v2)) {
                tr_ = v3;
            }

            tr += tr_;
        }

        value[1] = dm_p / tr;
        value[2] = -dm_m / tr;
        value[0] = -(value[1] - value[2]) / (value[1] + value[2]);

        return value;
    }

    /**
     * returns the bollinger bands - value[2] is the upper bollinger band,
     * value[0] is the lower bollinger band
     *
     * @param n - number of days used to calculate the average
     * @param deviations - the standard deviations
     * @param candles
     * @param skipdays
     * @return
     */
    public double[] bollinger(int n, int deviations, Vector candles,
        int skipdays) {
        double[] value = new double[3];

        double centerband = SMA(n, candles, skipdays);

        double t1 = 0.0;

        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);

            t1 += ((c.close - centerband) * (c.close - centerband));

            // t1+=c.close-centerband;
        }

        double t2 = Math.sqrt(t1 / n);

        double upper = centerband + (deviations * t2);
        double lower = centerband - (deviations * t2);

        value[2] = upper;
        value[1] = centerband;
        value[0] = lower;

        return value;
    }

    /**
     * returns the envelope for a candle series - value[0] contains the upper
     * envelope, value[1] contains the lower envelope
     *
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double[] env(int n, double f, Vector candles, int skipdays) {
        double[] value = new double[2];

        return value;
    }

    /**
     * returns the standard deviation for a timeline
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double deviation(int n, Vector candles, int skipdays) {
        double centerband = SMA(n, candles, skipdays);

        double t1 = 0.0;

        for (int i = 0; i < n; i++) {
            Candle c = (Candle) candles.elementAt(i + skipdays);

            t1 += ((c.close - centerband) * (c.close - centerband));

            // t1+=c.close-centerband;
        }

        double t2 = Math.sqrt(t1 / n);

        return t2;
    }


    /**
     * returns the true range of a day, defined by the skipdays
     *
     * @param candles
     * @param skipdays
     * @return
     */
    public double tr(Vector candles, int skipdays) {
        double val = 0;
        Candle c = (Candle) candles.elementAt(skipdays);

        val = c.hi - c.low;

        return val;
    }

    /**
     * returns r-change
     * @param n
     * @param candles
     * @param skipdays
     * @return
     */
    public double rchange(int n, Vector candles, int skipdays) {
        return 0.0;
    }

    /**
     * --- helper functions - since *ALL FUNCTIONS WORK ON CLOSE VALUES OF
     * CANDLES*, we need to provide helper functions to move open values into
     * close position (for example)
     */
    /**
     * moves all the opens into the candle's close positions
     *
     * @param candles
     * @return
     */
    public Vector openToClose(Vector candles) {
        Vector ret = new Vector();

        for (int i = 0; i < candles.size(); i++) {
            Candle c = new Candle();
            Candle c1 = (Candle) candles.elementAt(i);

            c.close = c1.open;
            ret.addElement(c);
        }

        return ret;
    }

    /**
     * moves all the hi into the candle's close positions
     *
     * @param candles
     * @return
     */
    public Vector hiToClose(Vector candles) {
        Vector ret = new Vector();

        for (int i = 0; i < candles.size(); i++) {
            Candle c = new Candle();
            Candle c1 = (Candle) candles.elementAt(i);

            c.close = c1.hi;
            ret.addElement(c);
        }

        return ret;
    }

    /**
     * moves all the lows into the candles close
     *
     * @param candles
     * @return
     */
    public Vector lowToClose(Vector candles) {
        Vector ret = new Vector();

        for (int i = 0; i < candles.size(); i++) {
            Candle c = new Candle();
            Candle c1 = (Candle) candles.elementAt(i);

            c.close = c1.low;
            ret.addElement(c);
        }

        return ret;
    }

    /**
     * moves the daily average into the close positions of an vector
     *
     * @param candles
     * @return
     */
    public Vector average(Vector candles) {
        Vector ret = new Vector();

        for (int i = 0; i < candles.size(); i++) {
            Candle c = new Candle();
            Candle c1 = (Candle) candles.elementAt(i);

            c.close = c1.hi + c1.low + c1.open + c1.close;
            c.close /= 4;
            ret.addElement(c);
        }

        return ret;
    }

    public void vldmi(int n, Vector candles, int skipdays) {
    }

    /**
     * fft analysis
     * @param candles
     * @param skipdays
     * @return
     */
    public float[] fft(Vector candles, int skipdays) {
        float[] sin = new float[512];

        double min = 1000000;
        double max = 0;

        for (int i = 0; i < sin.length; i++) {
            double r1 = ((Candle) candles.elementAt(i)).close;

            System.out.println(r1);
        }

        for (int i = 0; i < sin.length; i++) {
            double r1 = ((Candle) candles.elementAt(i)).close;

            if (r1 < min) {
                min = r1;
            }

            if (r1 > max) {
                max = r1;
            }
        }

        System.out.println("Min/Max:" + min + "/" + max);

        for (int i = 0; i < sin.length; i++) {
            double r1 = ((Candle) candles.elementAt(i)).close;

            sin[i] = (float) ((r1 - min) / (max - min));
        }

        System.out.println("**");

        for (int i = 0; i < sin.length; i++) {
            System.out.println(sin[i]);
        }

        float[] out = new float[sin.length];

        out = fftMag(sin);

        System.out.println("**");

        for (int i = 0; i < out.length; i++) {
            System.out.println((out[i] * 1000) + "     Periodenlaenge:" +
                (i * 2) + "/" + i);
        }

        return out;
    }

    /**
     *        calculates the pivots for a candle at position skipdays
     */
    double[] pivots(Vector candles, int skipdays) {
        Candle c = (Candle) candles.elementAt(skipdays);
        double[] ret = new double[5];

        /*
         ret[2]=shorten((c.hi+c.low+c.close)/3);

         ret[1]=shorten((2*ret[2])-c.low);
         ret[0]=shorten(ret[2]+(c.hi-c.low));
         ret[3]=shorten((2*ret[2])-c.hi);
         ret[4]=shorten(ret[2]-(c.hi-c.low));
         */
        return ret;
    }

    private int bitrev(int j) {
        int j2;
        int j1 = j;
        int k = 0;

        for (int i = 1; i <= nu; i++) {
            j2 = j1 / 2;
            k = ((2 * k) + j1) - (2 * j2);
            j1 = j2;
        }

        return k;
    }

    public final float[] fftMag(float[] x) {
        // assume n is a power of 2
        n = x.length;
        nu = (int) (Math.log(n) / Math.log(2));

        int n2 = n / 2;
        int nu1 = nu - 1;
        float[] xre = new float[n];
        float[] xim = new float[n];
        float[] mag = new float[n2];
        float tr;
        float ti;
        float p;
        float arg;
        float c;
        float s;

        for (int i = 0; i < n; i++) {
            xre[i] = x[i];
            xim[i] = 0.0f;
        }

        int k = 0;

        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitrev(k >> nu1);
                    arg = (2 * (float) Math.PI * p) / n;
                    c = (float) Math.cos(arg);
                    s = (float) Math.sin(arg);
                    tr = (xre[k + n2] * c) + (xim[k + n2] * s);
                    ti = (xim[k + n2] * c) - (xre[k + n2] * s);
                    xre[k + n2] = xre[k] - tr;
                    xim[k + n2] = xim[k] - ti;
                    xre[k] += tr;
                    xim[k] += ti;
                    k++;
                }

                k += n2;
            }

            k = 0;
            nu1--;
            n2 = n2 / 2;
        }

        k = 0;

        int r;

        while (k < n) {
            r = bitrev(k);

            if (r > k) {
                tr = xre[k];
                ti = xim[k];
                xre[k] = xre[r];
                xim[k] = xim[r];
                xre[r] = tr;
                xim[r] = ti;
            }

            k++;
        }

        mag[0] = (float) (Math.sqrt((xre[0] * xre[0]) + (xim[0] * xim[0]))) / n;

        for (int i = 1; i < (n / 2); i++) {
            mag[i] = (2 * (float) (Math.sqrt((xre[i] * xre[i]) +
                    (xim[i] * xim[i])))) / n;
        }

        return mag;
    }

    // general helper routines

    /**
     * returns minimum of a series of close values
     */
    public double minClose(Vector v, int start, int end) {
        double min = 100000000;

        for (int i = start; i < end; i++) {
            Candle c = (Candle) v.elementAt(i);

            if (c.close < min) {
                min = c.close;
            }
        }

        return min;
    }

    /**
     * returns the maximum of a series of close values
     * @param v
     * @param start
     * @param end
     * @return
     */
    public double maxClose(Vector v, int start, int end) {
        double max = 0;

        for (int i = start; i < end; i++) {
            Candle c = (Candle) v.elementAt(i);

            if (c.close > max) {
                max = c.close;
            }
        }

        return max;
    }

    /**
     * returns the absolut minimum of a vector
     * @param v
     * @param start
     * @param end
     * @return
     */
    public double min(Vector v, int start, int end) {
        double min = 100000000;

        for (int i = start; i < end; i++) {
            Candle c = (Candle) v.elementAt(i);

            if (c.low < min) {
                min = c.low;
            }
        }

        return min;
    }

    /**
     * returns the absolute maximum of a vector
     * @param v
     * @param start
     * @param end
     * @return
     */
    public double max(Vector v, int start, int end) {
        double max = 0;

        for (int i = start; i < end; i++) {
            Candle c = (Candle) v.elementAt(i);

            if (c.hi > max) {
                max = c.hi;
            }
        }

        return max;
    }

    public Vector getCandles(Vector v, int skipdays) {
        Vector ret = new Vector();

        for (int i = 0; i < (v.size() - skipdays); i++) {
            ret.addElement((Candle) v.elementAt(i));
        }

        return ret;
    }

	public Vector normalizeVector(Vector input){
		Vector ret=new Vector();

		double min=min(input,0, input.size());
		double max=max(input,0,input.size());

		for(int i=0;i<input.size();i++){
			Candle c=(Candle)input.elementAt(i);
			ret.addElement(""+((c.close-min)/(max-min)));
		}

		return ret;

	}


    /**
     * returns the starc bands in a double array, val[0] lower starc band, val[3] upper starc band
     * use val[1] + val[2] for the factor1 starc bands.
     * good parameter settings are: v/20/14/2/3.
     * @param v
     * @param closeAverage
     * @param truerangeAverage
     * @param factor1
     * @param factor2
     * @return
     */
    public double[] starc(Vector v, int closeAverage, int truerangeAverage, double factor1, double factor2, int skipdays){
		double[] ret = new double[4];

		double atr = 0;
		// calculate the average of the true range for a couple of days.
		for(int i=0;i<truerangeAverage;i++){
			//
			atr+=tr(v, skipdays+i);
		}
		atr/=(double)truerangeAverage;


		// calculate the moving average of the close price
		double mav=SMA(closeAverage, v, skipdays);

		ret[0]=mav - (factor2*atr);
		ret[1]=mav - (factor1*atr);
		ret[2]=mav + (factor1*atr);
		ret[3]=mav + (factor1*atr);

		return ret;
	}



}
