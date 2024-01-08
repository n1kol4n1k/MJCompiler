// generated with ast extension for cup
// version 0.8
// 22/7/2023 22:33:43


package rs.ac.bg.etf.pp1.ast;

public class InheritanceDerived1 extends Inheritance {

    public InheritanceDerived1 () {
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
        buffer.append("InheritanceDerived1(\n");

        buffer.append(tab);
        buffer.append(") [InheritanceDerived1]");
        return buffer.toString();
    }
}
