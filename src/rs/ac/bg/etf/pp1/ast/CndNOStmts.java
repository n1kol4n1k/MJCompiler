// generated with ast extension for cup
// version 0.8
// 31/0/2023 19:4:4


package rs.ac.bg.etf.pp1.ast;

public class CndNOStmts extends ConditionStatements {

    public CndNOStmts () {
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
        buffer.append("CndNOStmts(\n");

        buffer.append(tab);
        buffer.append(") [CndNOStmts]");
        return buffer.toString();
    }
}
