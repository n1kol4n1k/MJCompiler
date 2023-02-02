package rs.ac.bg.etf.pp1;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.AddExpr;
import rs.ac.bg.etf.pp1.ast.Assignment;
import rs.ac.bg.etf.pp1.ast.BoolConstIdentValue;
import rs.ac.bg.etf.pp1.ast.CharConstIdentValue;
import rs.ac.bg.etf.pp1.ast.ClassDecl;
import rs.ac.bg.etf.pp1.ast.ClassDeclEnter;
import rs.ac.bg.etf.pp1.ast.ConstDecl;
import rs.ac.bg.etf.pp1.ast.ConstType;
//import rs.ac.bg.etf.pp1.ast.Const;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.FuncCall;
import rs.ac.bg.etf.pp1.ast.IntConstIdentValue;
import rs.ac.bg.etf.pp1.ast.MethodDecl;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.PrintStmt;
//import rs.ac.bg.etf.pp1.ast.ProcCall;
import rs.ac.bg.etf.pp1.ast.ProgName;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.ast.ReturnStmt;
//import rs.ac.bg.etf.pp1.ast.ReturnExpr;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.Term;
import rs.ac.bg.etf.pp1.ast.TermExpr;
import rs.ac.bg.etf.pp1.ast.Type;
import rs.ac.bg.etf.pp1.ast.Var;
import rs.ac.bg.etf.pp1.ast.VarArray;
import rs.ac.bg.etf.pp1.ast.VarDecl;
import rs.ac.bg.etf.pp1.ast.VarIdent;
import rs.ac.bg.etf.pp1.ast.VarSingle;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticPass extends VisitorAdaptor {
	
	//Symbol Table Expansion
	Struct boolType = new Struct(Struct.Bool);
	
	//Helper structures
	class VarInfo
	{
		public String m_Name;
		public boolean m_IsArray;
		public VarInfo(String name, boolean isArray) { m_Name=name; m_IsArray = isArray;}
	}

	boolean errorDetected = false;
	Obj m_currentMethod = null;
	boolean m_returnFound = false;
	//to be written in obj file header
	int nVars;
	Struct m_CurrentConstType = null;

	Logger log = Logger.getLogger(getClass());

	//Lists
	ArrayList<VarInfo> innerVarList = new ArrayList<VarInfo>();
	
	//Symbol Table expansion with bool standard type
	public void ExpandSymTable()
	{
		Tab.insert(Obj.Type, "bool", boolType);
	}
	
	//Entering program
	public void visit(ProgName progName) 
	{
		progName.obj = Tab.insert(Obj.Prog, progName.getPName(), Tab.noType);
		Tab.openScope();     	
	}
	
	//Exiting program
	public void visit(Program program) 
	{		
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	//Declaration of Variable(s)
	public void visit(VarDecl varDecl) {
		if(varDecl.getType().struct == Tab.noType)
		{
			report_error("Greska: tip deklarisane promenljive ne postoji", null);
			return;
		}
		for(VarInfo varInfo : innerVarList)
		{
			report_info("Deklarisana promenljiva "+ varInfo.m_Name, varDecl);
			if(varInfo.m_IsArray == true)
			{
				//TODO: List of new types arrays, to prevent multiple instances of same type
				Tab.insert(Obj.Var, varInfo.m_Name, new Struct(Struct.Array, varDecl.getType().struct));
			}
			else
			{
				Tab.insert(Obj.Var, varInfo.m_Name, varDecl.getType().struct);
			}
			//TODO: Declaring an existing variable?
		}
		innerVarList.clear();
	}
	
	public void visit(VarSingle varSingle)
	{
		AddVarInfo(varSingle.getVName(), false);
	}
	
	public void visit(VarArray varArray)
	{
		AddVarInfo(varArray.getVName(), true);
	}
	
	//Type visit
	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola", null);
			type.struct = Tab.noType;
		} 
		else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} 
			else {
				report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip ", type);
				type.struct = Tab.noType;
			}
		}  
	}
	
	//Const declarations 
	public void visit(ConstType constType)
	{
		m_CurrentConstType = constType.getType().struct;
	}
	
	public void visit(ConstDecl constDecl)
	{
		m_CurrentConstType = null;
	}
	
	public void visit(IntConstIdentValue intConstIdentValue)
	{
		if(m_CurrentConstType == null || m_CurrentConstType != Tab.intType)
		{
			report_error("Greska: tip i vrednost konstante se slazu", intConstIdentValue);
			return;
		}
		
		Obj newConst = Tab.insert(Obj.Con, intConstIdentValue.getCName(), Tab.intType);
		newConst.setAdr(intConstIdentValue.getNumValue());
	}
	
	public void visit(BoolConstIdentValue boolConstIdentValue)
	{
		if(m_CurrentConstType == null || m_CurrentConstType != boolType)
		{
			report_error("Greska: tip i vrednost konstante se slazu", boolConstIdentValue);
			return;
		}

		Obj newConst = Tab.insert(Obj.Con, boolConstIdentValue.getCName(), boolType);
		newConst.setAdr(boolConstIdentValue.getBoolValue() ? 1 : 0);
	}
	
	public void visit(CharConstIdentValue charConstIdentValue)
	{
		if(m_CurrentConstType == null || m_CurrentConstType != Tab.charType)
		{
			report_error("Greska: tip i vrednost konstante se slazu", charConstIdentValue);
			return;
		}

		Obj newConst = Tab.insert(Obj.Con, charConstIdentValue.getCName(), Tab.charType);
		newConst.setAdr(charConstIdentValue.getCharValue());
	}
	
	//Methods
	//Enter method declaration
	public void visit(MethodTypeName methodTypeName) {
		m_currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethName(), methodTypeName.getType().struct);
		methodTypeName.obj = m_currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getMethName(), methodTypeName);
	}

	//Exit method declaration
	public void visit(MethodDecl methodDecl) {
		if (!m_returnFound && m_currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funcija " + m_currentMethod.getName() + " nema return iskaz!", null);
		}
		
		Tab.chainLocalSymbols(m_currentMethod);
		Tab.closeScope();
		
		m_returnFound = false;
		m_currentMethod = null;
		
		//TODO: return is of different type than curr method return type
	}
	
	public void visit(ReturnStmt returnStmt)
	{
		m_returnFound = true;
	}
/*
	

	
	public void visit(Assignment assignment) {
		if (!assignment.getExpr().struct.assignableTo(assignment.getDesignator().obj.getType()))
			report_error("Greska na liniji " + assignment.getLine() + " : " + " nekompatibilni tipovi u dodeli vrednosti ", null);
	}

*/
	/*public void visit(ReturnExpr returnExpr){
		returnFound = true;
		Struct currMethType = currentMethod.getType();
		if (!currMethType.compatibleWith(returnExpr.getExpr().struct)) {
			report_error("Greska na liniji " + returnExpr.getLine() + " : " + "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije " + currentMethod.getName(), null);
		}			  	     	
	}

	public void visit(ProcCall procCall){
		Obj func = procCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) { 
			report_info("Pronadjen poziv funkcije " + func.getName() + " na liniji " + procCall.getLine(), null);
			//RESULT = func.getType();
		} 
		else {
			report_error("Greska na liniji " + procCall.getLine()+" : ime " + func.getName() + " nije funkcija!", null);
			//RESULT = Tab.noType;
		}     	
	}    

	public void visit(AddExpr addExpr) {
		Struct te = addExpr.getExpr().struct;
		Struct t = addExpr.getTerm().struct;
		if (te.equals(t) && te == Tab.intType)
			addExpr.struct = te;
		else {
			report_error("Greska na liniji "+ addExpr.getLine()+" : nekompatibilni tipovi u izrazu za sabiranje.", null);
			addExpr.struct = Tab.noType;
		} 
	}

	public void visit(TermExpr termExpr) {
		termExpr.struct = termExpr.getTerm().struct;
	}

	public void visit(Term term) {
		term.struct = term.getFactor().struct;    	
	}

	public void visit(Const cnst){
		cnst.struct = Tab.intType;    	
	}
	
	public void visit(Var var) {
		var.struct = var.getDesignator().obj.getType();
	}

	public void visit(FuncCall funcCall){
		Obj func = funcCall.getDesignator().obj;
		if (Obj.Meth == func.getKind()) { 
			report_info("Pronadjen poziv funkcije " + func.getName() + " na liniji " + funcCall.getLine(), null);
			funcCall.struct = func.getType();
		} 
		else {
			report_error("Greska na liniji " + funcCall.getLine()+" : ime " + func.getName() + " nije funkcija!", null);
			funcCall.struct = Tab.noType;
		}

	}

	public void visit(Designator designator){
		Obj obj = Tab.find(designator.getName());
		if (obj == Tab.noObj) { 
			report_error("Greska na liniji " + designator.getLine()+ " : ime "+designator.getName()+" nije deklarisano! ", null);
		}
		designator.obj = obj;
	}*/
	
	public boolean passed() {
		return !errorDetected;
	}
	
	//Helpers
	private void AddVarInfo(String name, boolean isArray)
	{
		innerVarList.add(new VarInfo(name, isArray));
	}
	
	//Reports
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
}

