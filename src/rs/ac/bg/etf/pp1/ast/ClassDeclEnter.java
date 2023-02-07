// generated with ast extension for cup
// version 0.8
// 7/1/2023 3:18:55


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclEnter extends ClassName {

    private String clName;

    public ClassDeclEnter (String clName) {
        this.clName=clName;
    }

    public String getClName() {
        return clName;
    }

    public void setClName(String clName) {
        this.clName=clName;
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
        buffer.append("ClassDeclEnter(\n");

        buffer.append(" "+tab+clName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDeclEnter]");
        return buffer.toString();
    }
}
