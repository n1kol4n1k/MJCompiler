// generated with ast extension for cup
// version 0.8
// 10/0/2024 23:5:17


package rs.ac.bg.etf.pp1.ast;

public class NamespaceName implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private String nName;

    public NamespaceName (String nName) {
        this.nName=nName;
    }

    public String getNName() {
        return nName;
    }

    public void setNName(String nName) {
        this.nName=nName;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
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
        buffer.append("NamespaceName(\n");

        buffer.append(" "+tab+nName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NamespaceName]");
        return buffer.toString();
    }
}
