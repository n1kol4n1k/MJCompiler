// generated with ast extension for cup
// version 0.8
// 31/0/2023 0:0:49


package src.rs.ac.bg.etf.pp1.ast;

public class Minusop extends Addop {

    public Minusop () {
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
        buffer.append("Minusop(\n");

        buffer.append(tab);
        buffer.append(") [Minusop]");
        return buffer.toString();
    }
}
