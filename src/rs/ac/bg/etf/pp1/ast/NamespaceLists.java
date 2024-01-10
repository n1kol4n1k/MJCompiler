// generated with ast extension for cup
// version 0.8
// 10/0/2024 23:5:17


package rs.ac.bg.etf.pp1.ast;

public class NamespaceLists extends NamespaceList {

    private NamespaceList NamespaceList;
    private NamespaceDef NamespaceDef;

    public NamespaceLists (NamespaceList NamespaceList, NamespaceDef NamespaceDef) {
        this.NamespaceList=NamespaceList;
        if(NamespaceList!=null) NamespaceList.setParent(this);
        this.NamespaceDef=NamespaceDef;
        if(NamespaceDef!=null) NamespaceDef.setParent(this);
    }

    public NamespaceList getNamespaceList() {
        return NamespaceList;
    }

    public void setNamespaceList(NamespaceList NamespaceList) {
        this.NamespaceList=NamespaceList;
    }

    public NamespaceDef getNamespaceDef() {
        return NamespaceDef;
    }

    public void setNamespaceDef(NamespaceDef NamespaceDef) {
        this.NamespaceDef=NamespaceDef;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(NamespaceList!=null) NamespaceList.accept(visitor);
        if(NamespaceDef!=null) NamespaceDef.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(NamespaceList!=null) NamespaceList.traverseTopDown(visitor);
        if(NamespaceDef!=null) NamespaceDef.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(NamespaceList!=null) NamespaceList.traverseBottomUp(visitor);
        if(NamespaceDef!=null) NamespaceDef.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NamespaceLists(\n");

        if(NamespaceList!=null)
            buffer.append(NamespaceList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(NamespaceDef!=null)
            buffer.append(NamespaceDef.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NamespaceLists]");
        return buffer.toString();
    }
}
