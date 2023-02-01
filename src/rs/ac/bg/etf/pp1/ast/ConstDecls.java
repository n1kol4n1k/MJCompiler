// generated with ast extension for cup
// version 0.8
// 31/0/2023 23:26:14


package rs.ac.bg.etf.pp1.ast;

public class ConstDecls extends ConstDeclList {

    private ConstDeclList ConstDeclList;
    private ConstIdentValue ConstIdentValue;

    public ConstDecls (ConstDeclList ConstDeclList, ConstIdentValue ConstIdentValue) {
        this.ConstDeclList=ConstDeclList;
        if(ConstDeclList!=null) ConstDeclList.setParent(this);
        this.ConstIdentValue=ConstIdentValue;
        if(ConstIdentValue!=null) ConstIdentValue.setParent(this);
    }

    public ConstDeclList getConstDeclList() {
        return ConstDeclList;
    }

    public void setConstDeclList(ConstDeclList ConstDeclList) {
        this.ConstDeclList=ConstDeclList;
    }

    public ConstIdentValue getConstIdentValue() {
        return ConstIdentValue;
    }

    public void setConstIdentValue(ConstIdentValue ConstIdentValue) {
        this.ConstIdentValue=ConstIdentValue;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstDeclList!=null) ConstDeclList.accept(visitor);
        if(ConstIdentValue!=null) ConstIdentValue.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstDeclList!=null) ConstDeclList.traverseTopDown(visitor);
        if(ConstIdentValue!=null) ConstIdentValue.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstDeclList!=null) ConstDeclList.traverseBottomUp(visitor);
        if(ConstIdentValue!=null) ConstIdentValue.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDecls(\n");

        if(ConstDeclList!=null)
            buffer.append(ConstDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstIdentValue!=null)
            buffer.append(ConstIdentValue.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDecls]");
        return buffer.toString();
    }
}
