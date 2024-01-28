// generated with ast extension for cup
// version 0.8
// 16/0/2024 19:59:21


package rs.ac.bg.etf.pp1.ast;

public class DesignatorElemWithNamespace extends Designator {

    private String nName;
    private String dName;
    private Expr Expr;

    public DesignatorElemWithNamespace (String nName, String dName, Expr Expr) {
        this.nName=nName;
        this.dName=dName;
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
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

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorElemWithNamespace(\n");

        buffer.append(" "+tab+nName);
        buffer.append("\n");

        buffer.append(" "+tab+dName);
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorElemWithNamespace]");
        return buffer.toString();
    }
}
