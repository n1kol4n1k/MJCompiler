// generated with ast extension for cup
// version 0.8
// 12/0/2024 0:24:27


package rs.ac.bg.etf.pp1.ast;

public class NotEquals extends Relop {

    public NotEquals () {
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
        buffer.append("NotEquals(\n");

        buffer.append(tab);
        buffer.append(") [NotEquals]");
        return buffer.toString();
    }
}
