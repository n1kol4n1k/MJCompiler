// generated with ast extension for cup
// version 0.8
// 2/1/2023 1:5:6


package rs.ac.bg.etf.pp1.ast;

public class Percop extends Mulop {

    public Percop () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Percop(\n");

        buffer.append(tab);
        buffer.append(") [Percop]");
        return buffer.toString();
    }
}
