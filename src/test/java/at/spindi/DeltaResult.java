package at.spindi;

import java.util.ArrayList;

public class DeltaResult {
    public ArrayList<D365> only_a;
    public ArrayList<Nmp> only_b;
    public ArrayList<Pair<D365, Nmp>> modified;
    public ArrayList<Pair<D365, Nmp>> same;
    public DeltaResult() {
        this.only_a = new ArrayList<>();
        this.only_b = new ArrayList<>();
        this.modified = new ArrayList<>();
        this.same = new ArrayList<>();
    }
}
