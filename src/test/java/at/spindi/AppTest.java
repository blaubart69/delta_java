package at.spindi;

import java.util.stream.Stream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public static Ordering int_ordering(int a, int b) {
        if ( a  < b) return Ordering.LESS;
        if ( a == b) return Ordering.EQUAL;
        return Ordering.GREATER;
    }

    public static DeltaResult run_delta(Stream<D365> a_seq, Stream<Nmp> b_seq) throws Exception {

        DeltaResult r = new DeltaResult();

        at.spindi.Delta.<D365, Nmp>run(
            a_seq,
            b_seq,
            (a, b) -> int_ordering(a.id, Integer.parseInt(b.id)),
            (a, b) -> a.name.equals(b.name),
            (a1, a2) -> int_ordering(a1.id, a2.id),
            (b1, b2) -> int_ordering(Integer.parseInt(b1.id), Integer.parseInt(b2.id)),
            (a) -> {
                // only in list a
                r.only_a.add(a);
            },
            (b) -> {
                // only in list b
                r.only_b.add(b);
            },
            (a, b) -> {
                // modified
                r.modified.add( new Pair<>(a, b) );
            },
            (a, b) -> {
                // same
                r.same.add( new Pair<>(a, b) );
            });

        return r;
    }

    public void testSameSame() throws Exception
    {
        var stream_a = Stream.of(
            new D365(0, "berni")
        );
        var stream_b = Stream.of(
            new Nmp("0", "berni")
        );

        var r = run_delta(stream_a, stream_b);
            
        assertEquals(0, r.only_a.size());
        assertEquals(0, r.only_b.size());
        assertEquals(0, r.modified.size());
        assertEquals(1, r.same.size());
    }
    public void testModified() throws Exception
    {
        var stream_a = Stream.of(
            new D365(0, "berni")
        );
        var stream_b = Stream.of(
            new Nmp("0", "neuer Name")
        );

        var r = run_delta(stream_a, stream_b);
            
        assertEquals(0, r.only_a.size());
        assertEquals(0, r.only_b.size());
        assertEquals(1, r.modified.size());
        assertEquals(0, r.same.size());

        var modified = r.modified.get(0);
        assertEquals(new D365(0, "berni"), modified.a);
        assertEquals(new Nmp("0", "neuer Name"), modified.b);
    }
    public void testAllCases() throws Exception
    {
        var stream_a = Stream.of(
             new D365(0, "berni")
            ,new D365(1, "1")
            ,new D365(3, "3")
        );
        var stream_b = Stream.of(
             new Nmp("0", "neuer Name")
            ,new Nmp("2", "Neue Org")
            ,new Nmp("3", "3")
        );

        var r = run_delta(stream_a, stream_b);
            
        assertEquals(1, r.only_a.size());
        assertEquals(1, r.only_b.size());
        assertEquals(1, r.modified.size());
        assertEquals(1, r.same.size());

        assertEquals( new D365(1,"1"), r.only_a.get(0));
        assertEquals(new Nmp("2", "Neue Org"), r.only_b.get(0));

        var modified = r.modified.get(0);
        assertEquals(new D365(0, "berni"), modified.a);
        assertEquals(new Nmp("0", "neuer Name"), modified.b);

        var same = r.same.get(0);
        assertEquals(new D365(3, "3"), same.a);
        assertEquals(new Nmp("3", "3"), same.b);
    }
}
