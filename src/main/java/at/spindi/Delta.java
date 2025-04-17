package at.spindi;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Delta {
    public static <A,B> void run(
        Stream<A>                   a_seq,
        Stream<B>                   b_seq,
        BiFunction<A,B,Ordering>    key_cmp_a_b,
        BiPredicate<A,B>            attribute_cmp,
        BiFunction<A,A,Ordering>    key_cmp_a_a,
        BiFunction<B,B,Ordering>    key_cmp_b_b,
        Consumer<A>                 only_in_a,
        Consumer<B>                 only_in_b,
        BiConsumer<A,B>             modified,
        BiConsumer<A,B>             samesame
        ) throws Exception {

        var a_iter = a_seq.iterator();
        var b_iter = b_seq.iterator();

        A a_last = null;
        B b_last = null;
        A a = getNext(a_iter);
        B b = getNext(b_iter);

        for (;;) {
            
            if ( a == null && b == null ) {
                break;
            }
            else if ( a != null && b == null ) {

                only_in_a.accept(a);

                a_last = a;
                a = getNext(a_iter);
                check_sort_order(a_last, a, key_cmp_a_a, "A");
            }
            else if ( a == null && b != null ) {

                only_in_b.accept(b);

                b_last = b;
                b = getNext(b_iter);
                check_sort_order(b_last, b, key_cmp_b_b, "B");
            }
            else {

                switch (key_cmp_a_b.apply(a,b)) {
                    case LESS :
                        only_in_a.accept(a);
                        a_last = a;
                        a = getNext(a_iter);
                        break;
                    case GREATER :
                        only_in_b.accept(b);
                        b_last = b;
                        b = getNext(b_iter);
                        break;
                    case EQUAL :
                        if ( attribute_cmp.test(a,b) ) {
                            samesame.accept(a,b);
                        }
                        else {
                            modified.accept(a,b);
                        }
                        a_last = a;
                        b_last = b;
                        a = getNext(a_iter);
                        b = getNext(b_iter);
                }

                if ( a != null ) {
                    check_sort_order(a_last, a, key_cmp_a_a, "A");
                }
                if ( b != null ) {
                    check_sort_order(b_last, b, key_cmp_b_b, "B");
                }
            }
        }
    }

    private static <A> A getNext(Iterator<A> iter) {

        A elem;

        if ( iter.hasNext() ) {
            elem = iter.next();
        }
        else {
            elem = null;
        }
        return elem;
    }

    private static <A> void check_sort_order(final A last, final A curr, final BiFunction<A,A,Ordering> cmp, final String context) throws Exception {

        if (last == null) {
            return;
        }

        if ( cmp.apply(last, curr) != Ordering.LESS ) {
            throw new Exception(
                String.format("list %s is not sorted ascending. LAST is greater or equal CURR. last [%s], curr [%s]"
                ,context
                ,last.toString()
                ,curr.toString()));
        }
    }
}
