// generated with ast extension for cup
// version 0.8
// 31/0/2023 0:0:49


package src.rs.ac.bg.etf.pp1.ast;

public class VarDeclInnerList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private VarDeclInnerList VarDeclInnerList;
    private VarIdent VarIdent;

    public VarDeclInnerList (VarDeclInnerList VarDeclInnerList, VarIdent VarIdent) {
        this.VarDeclInnerList=VarDeclInnerList;
        if(VarDeclInnerList!=null) VarDeclInnerList.setParent(this);
        this.VarIdent=VarIdent;
        if(VarIdent!=null) VarIdent.setParent(this);
    }

    public VarDeclInnerList getVarDeclInnerList() {
        return VarDeclInnerList;
    }

    public void setVarDeclInnerList(VarDeclInnerList VarDeclInnerList) {
        this.VarDeclInnerList=VarDeclInnerList;
    }

    public VarIdent getVarIdent() {
        return VarIdent;
    }

    public void setVarIdent(VarIdent VarIdent) {
        this.VarIdent=VarIdent;
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
        if(VarDeclInnerList!=null) VarDeclInnerList.accept(visitor);
        if(VarIdent!=null) VarIdent.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclInnerList!=null) VarDeclInnerList.traverseTopDown(visitor);
        if(VarIdent!=null) VarIdent.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclInnerList!=null) VarDeclInnerList.traverseBottomUp(visitor);
        if(VarIdent!=null) VarIdent.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclInnerList(\n");

        if(VarDeclInnerList!=null)
            buffer.append(VarDeclInnerList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarIdent!=null)
            buffer.append(VarIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclInnerList]");
        return buffer.toString();
    }
}
