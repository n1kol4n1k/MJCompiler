package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.Assignment;
import rs.ac.bg.etf.pp1.ast.BlankReturnStmt;
import rs.ac.bg.etf.pp1.ast.ClassDecl;
import rs.ac.bg.etf.pp1.ast.ClassDeclEnter;
import rs.ac.bg.etf.pp1.ast.ConstBool;
import rs.ac.bg.etf.pp1.ast.ConstChar;
import rs.ac.bg.etf.pp1.ast.ConstNum;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.FuncCall;
import rs.ac.bg.etf.pp1.ast.MethodBasicTypeName;
import rs.ac.bg.etf.pp1.ast.MethodDecl;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MethodVoidName;
import rs.ac.bg.etf.pp1.ast.SpecNumConst;
import rs.ac.bg.etf.pp1.ast.PrintStmt;
import rs.ac.bg.etf.pp1.ast.ReturnStmt;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.Var;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import java.util.ArrayList;
import java.util.HashMap;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private int mainPc;
	public int getMainPc() { return mainPc; }
	
	HashMap<Obj, ArrayList<Struct>> m_formParmsMap = null;
	public void SetFormParmsMap(HashMap<Obj, ArrayList<Struct>> fpmap) { m_formParmsMap = fpmap; }
	Struct boolType = null;
	public void SetBoolType(Struct t) { boolType = t; }
	
	boolean m_IsClassScope = false;
	int m_NumOfPrints = 1;
	
	//Classes scope detection, to ignore generating code for it
	public void visit(ClassDeclEnter classDeclEnter)
	{
		m_IsClassScope = true;
	}
	public void visit(ClassDecl classDecl)
	{
		m_IsClassScope = false;
	}
	
	public void visit(MethodBasicTypeName methodBasicTypeName) {
		if(m_IsClassScope == true) { return; }
		GenerateMethod(methodBasicTypeName);
	}
	
	public void visit(MethodVoidName methodVoidName)
	{
		if(m_IsClassScope == true) { return; }
		if ("main".equalsIgnoreCase(methodVoidName.getMethName())) {
			mainPc = Code.pc;
		}
		GenerateMethod(methodVoidName);
	}
	
	private void GenerateMethod(MethodTypeName methodTypeName)
	{
		methodTypeName.obj.setAdr(Code.pc);
		
		// Collect arguments and local variables.
		int totalParams = methodTypeName.obj.getLocalSymbols().size();
		int formalParams = m_formParmsMap.get(methodTypeName.obj).size();
		
		// Generate the entry.
		Code.put(Code.enter);
		Code.put(formalParams);
		Code.put(totalParams);
	}
	
	public void visit(ReturnStmt returnStmt) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(BlankReturnStmt blankReturnStmt) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(MethodDecl methodDecl){
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(Assignment Assignment) {
		Code.store(Assignment.getDesignator().obj);
	}
	
	public void visit(Designator Designator) {
		SyntaxNode parent = Designator.getParent();
		if (Assignment.class != parent.getClass() && FuncCall.class != parent.getClass()) {
			Code.load(Designator.obj);
		}
	}
	
	public void visit(FuncCall FuncCall) {
		Obj functionObj = FuncCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc; 
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	//Print statement
	public void visit(PrintStmt printStmt) {
		if(printStmt.getExpr().struct == Tab.intType || printStmt.getExpr().struct == boolType)
		{
			Code.loadConst(5);
			Code.put(Code.print);
			for(int i = 1; i < m_NumOfPrints; i++)
			{
				Code.put(Code.dup2);
				Code.put(Code.print);
			}
		}
		else
		{
			Code.loadConst(1);
			Code.put(Code.bprint);
			for(int i = 1; i < m_NumOfPrints; i++)
			{
				Code.put(Code.dup2);
				Code.put(Code.bprint);
			}
		}
		m_NumOfPrints = 1;
	}
	public void visit(SpecNumConst specNumConst)
	{
		m_NumOfPrints = specNumConst.getNumVal();
	}
	
	//Aritmetics
	public void visit(ConstNum constNum)
	{
		Obj dummyObj = new Obj(Obj.Con, "dummy", Tab.intType);
		dummyObj.setAdr(constNum.getNumVal());
		Code.load(dummyObj);
	}
	public void visit(ConstChar constChar)
	{
		Obj dummyObj = new Obj(Obj.Con, "dummy", Tab.charType);
		dummyObj.setAdr(constChar.getCharVal());
		Code.load(dummyObj);
	}
	public void visit(ConstBool constBool)
	{
		Obj dummyObj = new Obj(Obj.Con, "dummy", boolType);
		dummyObj.setAdr(constBool.getBoolVal() ? 1 : 0);
		Code.load(dummyObj);
	}
	public void visit(Var var)
	{
		Code.load(var.getDesignator().obj);
	}
}