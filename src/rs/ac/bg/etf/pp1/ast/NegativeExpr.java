// generated with ast extension for cup
// version 0.8
// 7/1/2023 3:18:55


package rs.ac.bg.etf.pp1.ast;

public class NegativeExpr extends Expr {

    private ExprInner ExprInner;

    public NegativeExpr (ExprInner ExprInner) {
        this.ExprInner=ExprInner;
        if(ExprInner!=null) ExprInner.setParent(this);
    }

    public ExprInner getExprInner() {
        return ExprInner;
    }

    public void setExprInner(ExprInner ExprInner) {
        this.ExprInner=ExprInner;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprInner!=null) ExprInner.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprInner!=null) ExprInner.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprInner!=null) ExprInner.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NegativeExpr(\n");

        if(ExprInner!=null)
            buffer.append(ExprInner.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NegativeExpr]");
        return buffer.toString();
    }
}
