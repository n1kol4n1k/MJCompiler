// generated with ast extension for cup
// version 0.8
// 4/1/2023 19:35:20


package rs.ac.bg.etf.pp1.ast;

public class Less extends Relop {

    public Less () {
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
        buffer.append("Less(\n");

        buffer.append(tab);
        buffer.append(") [Less]");
        return buffer.toString();
    }
}
