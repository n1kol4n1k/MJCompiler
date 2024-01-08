// generated with ast extension for cup
// version 0.8
// 8/0/2024 17:3:14


package rs.ac.bg.etf.pp1.ast;

public class ConstBool extends Factor {

    private Boolean boolVal;

    public ConstBool (Boolean boolVal) {
        this.boolVal=boolVal;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(Boolean boolVal) {
        this.boolVal=boolVal;
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
        buffer.append("ConstBool(\n");

        buffer.append(" "+tab+boolVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstBool]");
        return buffer.toString();
    }
}
