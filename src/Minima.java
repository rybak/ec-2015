import java.io.BufferedReader;
import java.util.List;
import java.util.StringTokenizer;

import java.io.*;
import java.util.*;

public class Minima {

    private final boolean debug;

    private Minima() {
        debug = false;
    }

    public Minima(boolean debug) {
        this.debug = debug;
    }

    private int N;
    private Tester tester;

    private class Point {
        double[] x;
        private double value;
        private boolean valueReady;

        public double getValue() {
            if (!valueReady) {
                value = tester.ask(this);
                valueReady = true;
            }
            return value;
        }

        Point(int n) {
            x = new double[n];
        }

        public String toString() {
            return Arrays.toString(this.x);
        }
    }

    private final int populationSize = 100;
    private ArrayList<Point> population;

    private void solve() {
        init();
        Point best = null;
        populate();
        do {
            ArrayList<Point> nextPopulation = new ArrayList<>(populationSize);
            for (int i = 0; i < populationSize; ++i) {
                int ia = randomNot(populationSize, i, -1, -1);
                int ib = randomNot(populationSize, i, ia, -1);
                int ic = randomNot(populationSize, i, ia, ib);
                Point a = population.get(ia);
                Point b = population.get(ib);
                Point c = population.get(ic);
                Point d = mutation(a, b, c);
                Point Pi = population.get(i);
                Point e = crossover(d, Pi);
                double fi = Pi.getValue();
                double fe = e.getValue();
                if (fi < fe) {
                    nextPopulation.add(Pi);
                } else {
                    nextPopulation.add(e);
                }
            }
            population = nextPopulation;
            if (best == null) {
                best = population.get(0);
            }
            for (Point p : population) {
                if (p.getValue() < best.getValue()) {
                    best = p;
                }
            }
        } while (tester.hasTries());
    }

    Random indexRnd = new Random();

    private int randomNot(int n, int ii, int ia, int ib) {
        int res;
        do {
            res = indexRnd.nextInt(n);
        } while (res == ii || res == ia || res == ib);
        return res;
    }

    private final double MAX_VAL = 10.0;
    private final double MIN_VAL = -MAX_VAL;

    private static final double alpha = 0.75;

    private Point mutation(Point a, Point b, Point c) {
        Point res = new Point(N);
        boolean needDebug = false;
        for (int i = 0; i < N; ++i) {
            double val = a.x[i] + alpha * (b.x[i] - c.x[i]);
            if (val > MAX_VAL) {
                if (tester.debug) {
                    System.err.println("THIS HAPPENED " + Double.toString(val));
                    needDebug = true;
                }
                val = MAX_VAL;
            }
            if (val < MIN_VAL) {
                if (tester.debug) {
                    System.err.println("THAT HAPPENED " + Double.toString(val));
                    needDebug = true;
                }
                val = MIN_VAL;
            }
            res.x[i] = val;
        }
        return res;
    }

    private Point crossover(Point p1, Point p2) {
        Point res = new Point(N);
        for (int i = 0; i < N; ++i) {
            res.x[i] = randomInRange(p1.x[i], p2.x[i]);
        }
        return res;
    }

    final Random chooserRnd = new Random();

    private double randomInRange(double min, double max) {
        if (min > max) {
            double tmp = min;
            min = max;
            max = tmp;
        }
        return chooserRnd.nextBoolean() ? ((1.0 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
    }

    private void init() {
        if (this.debug) {
            N = 1;
        } else {
            N = in.nextInt();
        }
        int maxTries = 10000 * N * N;
        tester = new Tester(this.debug, maxTries);
    }

    private void populate() {
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; ++i) {
            population.add(randomPoint());
        }
    }

    private Random intRnd = new Random();
    private Random doubleRnd = new Random();
    private Random booleanRnd = new Random();

    private Point randomPoint() {
        Point p = new Point(N);
        for (int i = 0; i < N; ++i) {
            double tmp = intRnd.nextInt(10) + doubleRnd.nextDouble();
            if (booleanRnd.nextBoolean()) {
                tmp = -tmp;
            }
            p.x[i] = tmp;
        }
        return p;
    }

    private class Tester {
        private static final double DEBUG_EPS = 0.00000001;
        final boolean debug;
        private int triesLeft;

        Random rnd = null;
        private boolean bingo;

        public boolean isBingo() {
            return bingo;
        }

        Tester(boolean debug, int maxTries) {
            this.bingo = false;
            this.debug = debug;
            this.triesLeft = maxTries;
            if (debug) {
                rnd = new Random();
            }
        }

        double noise() {
            return rnd.nextDouble() - 0.5d;
        }

        final int DEBUG_SIZE = 5;

        double realFunction(Point p) {
            double x = p.x[0];
            List<Double> list = Arrays.asList(p1(x), p2(x), p3(x), p4(x), p5(x));
            assert (DEBUG_SIZE == list.size());
            return Collections.min(list);
        }

        private double sqr(double x) {
            return x * x;
        }

        double p1(double x) {
            return 1 + 2.0d * sqr(x - 1);
        }

        double p2(double x) {
            return 2 + 1.8d * sqr(x - 2);
        }

        double p3(double x) {
            return 3 + 1.6d * sqr(x - 3);
        }

        double p4(double x) {
            return 4 + 1.4d * sqr(x - 4);
        }

        double p5(double x) {
            return 5 + 1.2d * sqr(x - 5);
        }

        private boolean debugOutput = false;

        private double ask(Point p) {
            triesLeft -= 1;
            if (debug) {
                double realValue = realFunction(p);
                if (debugOutput) {
                    System.err.print("x = ");
                    for (double xi : p.x) {
                        System.err.print(xi);
                        System.err.print(' ');
                    }
                    System.err.print("F = ");
                    System.err.println(realValue);
                }
                if (triesLeft % 100 == 0) {
                    System.err.print("F = ");
                    System.err.println(realValue);
                    System.err.print("tries left : ");
                    System.err.println(triesLeft);
                }
                if (Math.abs(realValue - 1.0) < DEBUG_EPS) {
                    System.err.print("F = ");
                    System.err.println(realValue);
                    System.err.print("Tries left : ");
                    System.err.println(triesLeft);
                    System.err.println("BINGO");
                    System.exit(42);
                }
                return realValue + noise();
            } else {
                for (double xi : p.x) {
                    System.out.print(xi);
                    System.out.print(' ');
                }
                System.out.println();
                String answer = in.next();
                if (answer.equals("Bingo")) {
                    this.bingo = true;
                    System.exit(0);
                    return -42 * 239;
                } else {
                    return Double.parseDouble(answer);
                }
            }
        }

        public boolean hasTries() {
            return triesLeft > 0;
        }
    }

    public void run() {
        Locale.setDefault(Locale.US);
        in = new MyScanner();

        solve();

//        in.close();
//        out.close();
    }

    public static void main(String[] args) {
        new Minima(false).run();
    }

    private BufferedReader br;
    private StringTokenizer st;
    private MyScanner in;

    private class MyScanner {
        public MyScanner() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        public String next() {
            try {
                while (st == null || !st.hasMoreTokens()) {
                    st = new StringTokenizer(br.readLine());
                }
                return st.nextToken();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }

        public void close() {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}