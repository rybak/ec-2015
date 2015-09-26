import java.io.*;
import java.util.*;

public class OneMax {

    Random rnd;
    final char ZERO = '0';
    final char ONE = '1';
    StringBuffer sb;
    int n;
    int maxTries;
    int maxFF;

    private void solve() {
        init();
        int ff = tester.ask(sb.toString());
        maxFF = ff;
        maxTries -= 1;
        for (int i = 0; i < maxTries; ++i) {
            if (ff == n) {
                break;
            }
            StringBuffer t = mutate(sb);
            ff = tester.ask(t.toString());
            if (ff >= maxFF) {
                maxFF = ff;
                sb = t;
            }
        }
        if (ff != n) {
            if (tester.DEBUG) {
                System.out.println("☹");
            }
        }
    }

    class Tester {
        final boolean DEBUG = true;
        String answer = "";
        private int len = 0;

        private int ask(String str) {
            if (DEBUG) {
                System.err.print("ask : " + str + " res : ");
                int res = 0;
                for (int i = 0; i < len; ++i) {
                    if (str.charAt(i) == answer.charAt(i)) {
                        ++res;
                    }
                }
                System.err.println(res);
                return res;
            } else {
                System.out.println(str);
                return (in.nextInt());
            }
        }

        public int getN() {
            int res = in.nextInt();
            this.len = res;
            if (DEBUG) {
                StringBuffer sb = new StringBuffer();
                char sym[] = new char[]{ZERO, ONE};
                for (int i = 0; i < res; ++i) {
                    sb.append(sym[rnd.nextInt(2)]);
                }
                assert (res == sb.length());
                answer = sb.toString();
                System.err.println("answer : " + answer);
            }
            return res;
        }
    }

    private StringBuffer mutate(StringBuffer sb) {
        StringBuffer res = new StringBuffer(sb);
        boolean mutated = false;
        for (int i = 0; i < n; ++i) {
            if (rnd.nextInt(n) == 0) {
                flip(res, i);
                mutated = true;
            }
        }
        if (!mutated) {
            int index = rnd.nextInt(n);
            flip(res, index);
            mutated = true;
        }
        return res;
    }

    Tester tester = new Tester();

    private void init() {
        n = tester.getN();
        sb = new StringBuffer();
        char str[] = new char[n];
        Arrays.fill(str, ZERO);
        sb.append(str);
        maxTries = 4 * n;
    }

    private void flip(StringBuffer sb, int index) {
        char c = sb.charAt(index);
        assert (c == ZERO || c == ONE);
        if (ZERO == c) {
            sb.setCharAt(index, ONE);
        } else {
            sb.setCharAt(index, ZERO);
        }
    }


    public void run() {
        Locale.setDefault(Locale.US);
        in = new MyScanner();

        rnd = new Random();
        solve();

//        in.close();
//        out.close();
    }

    public static void main(String[] args) {
        new OneMax().run();
    }

    private BufferedReader br;
    private StringTokenizer st;
    private MyScanner in;
    private PrintWriter out;

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
