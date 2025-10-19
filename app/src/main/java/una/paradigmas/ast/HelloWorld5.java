package una.paradigmas.ast;

  public class HelloWorld5 {
        sealed interface Nat permits Zero, S {}
        record Zero() implements Nat {}
        record S(Nat n) implements Nat {}

        sealed interface List permits Nil, Cons {}
        record Nil() implements List {}
        record Cons(Object car, List cdr) implements List {}

        sealed interface Gender permits Male, Female {}
        record Male() implements Gender {}
        record Female() implements Gender {}

        public static void print(Object arg) {
            System.out.println(arg);
        }
        public static void main(String... args) {
            int x = 666;
            print(x);
        }
    }