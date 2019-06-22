package test;

import java.util.stream.Stream;

/**
 *
 * @author gideon
 */
public class test2 {

    static class F implements Comparable<F> {

        F(int val) {
            this.val = val;
        }
        int val;

        @Override
        public String toString() {
            return "" + val;
        }

        @Override
        public int compareTo(F o) {
            return val - o.val;
        }

        @Override
        public int hashCode() {
            return val;
        }

        @Override
        public boolean equals(Object obj) {
            return val == ((F) obj).val;
        }

    }

    public static void main(String... args) {
        Stream a = Stream.of(new F(1), new F(2), new F(3));
        Stream b = Stream.of(new F(5), new F(4), new F(3), new F(2));
        Stream c = Stream.concat(a, b);

        c.sorted().distinct().forEach(System.out::println);
//        c.distinct().forEach(System.out::println);
    }
}
