// generated with ast extension for cup
// version 0.8
// 8/0/2024 17:3:14


package rs.ac.bg.etf.pp1.ast;

public class GreaterEquals extends Relop {

    public GreaterEquals () {
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
        buffer.append("GreaterEquals(\n");

        buffer.append(tab);
        buffer.append(") [GreaterEquals]");
        return buffer.toString();
    }
}
