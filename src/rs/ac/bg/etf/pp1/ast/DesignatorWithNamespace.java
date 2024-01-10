// generated with ast extension for cup
// version 0.8
// 10/0/2024 23:5:17


package rs.ac.bg.etf.pp1.ast;

public class DesignatorWithNamespace extends Designator {

    private String nName;
    private String dName;

    public DesignatorWithNamespace (String nName, String dName) {
        this.nName=nName;
        this.dName=dName;
    }

    public String getNName() {
        return nName;
    }

    public void setNName(String nName) {
        this.nName=nName;
    }

    public String getDName() {
        return dName;
    }

    public void setDName(String dName) {
        this.dName=dName;
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
        buffer.append("DesignatorWithNamespace(\n");

        buffer.append(" "+tab+nName);
        buffer.append("\n");

        buffer.append(" "+tab+dName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorWithNamespace]");
        return buffer.toString();
    }
}
