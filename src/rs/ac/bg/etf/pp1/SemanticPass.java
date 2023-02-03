package rs.ac.bg.etf.pp1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.ActualParam;
import rs.ac.bg.etf.pp1.ast.AddExpr;
import rs.ac.bg.etf.pp1.ast.Assignment;
import rs.ac.bg.etf.pp1.ast.BlankReturnStmt;
import rs.ac.bg.etf.pp1.ast.BoolConstIdentValue;
import rs.ac.bg.etf.pp1.ast.CharConstIdentValue;
import rs.ac.bg.etf.pp1.ast.ClassDecl;
import rs.ac.bg.etf.pp1.ast.ClassDeclEnter;
import rs.ac.bg.etf.pp1.ast.ConstBool;
import rs.ac.bg.etf.pp1.ast.ConstChar;
import rs.ac.bg.etf.pp1.ast.ConstDecl;
import rs.ac.bg.etf.pp1.ast.ConstNum;
import rs.ac.bg.etf.pp1.ast.ConstType;
//import rs.ac.bg.etf.pp1.ast.Const;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.ExprBrackets;
import rs.ac.bg.etf.pp1.ast.FactExpr;
import rs.ac.bg.etf.pp1.ast.FactSingle;
import rs.ac.bg.etf.pp1.ast.FormalParamDecl;
import rs.ac.bg.etf.pp1.ast.FuncCall;
import rs.ac.bg.etf.pp1.ast.IntConstIdentValue;
import rs.ac.bg.etf.pp1.ast.MethodBasicTypeName;
import rs.ac.bg.etf.pp1.ast.MethodDecl;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MethodVoidName;
import rs.ac.bg.etf.pp1.ast.NegativeExpr;
import rs.ac.bg.etf.pp1.ast.NewArray;
import rs.ac.bg.etf.pp1.ast.NewSingle;
import rs.ac.bg.etf.pp1.ast.PositiveExpr;
import rs.ac.bg.etf.pp1.ast.PrintStmt;
//import rs.ac.bg.etf.pp1.ast.ProcCall;
import rs.ac.bg.etf.pp1.ast.ProgName;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.ast.ReturnStmt;
import rs.ac.bg.etf.pp1.ast.StatementFuncCall;
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
	Obj m_MainMethod = null;
	boolean m_IsClassScope = false;

	Logger log = Logger.getLogger(getClass());

	//Lists
	ArrayList<VarInfo> m_innerVarList = new ArrayList<VarInfo>();
	HashMap<Obj, ArrayList<Struct>> m_formParmsMap = new HashMap<Obj, ArrayList<Struct>>();
	ArrayList<Struct> m_currentActualParamsTypes = new ArrayList<Struct>();
	
	//Symbol Table expansion with bool standard type
	public void ExpandSymTable()
	{
		Tab.insert(Obj.Type, "bool", boolType);
		Tab.insert(Obj.Type, "void", Tab.noType);
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
		if(m_MainMethod == null)
		{
			report_error("Nije pronadjen main metod!", null);
		}
	}

	//Declaration of Variable(s)
	public void visit(VarDecl varDecl) {
		if(m_IsClassScope == true) { return; }
		if(varDecl.getType().struct == Tab.noType)
		{
			report_error("Greska: tip deklarisane promenljive ne postoji", null);
			return;
		}
		for(VarInfo varInfo : m_innerVarList)
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
		m_innerVarList.clear();
	}
	
	public void visit(VarSingle varSingle)
	{
		if(m_IsClassScope == true) { return; }
		AddVarInfo(varSingle.getVName(), false);
	}
	
	public void visit(VarArray varArray)
	{
		if(m_IsClassScope == true) { return; }
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
	
	//Classes scope detection
	public void visit(ClassDeclEnter classDeclEnter)
	{
		m_IsClassScope = true;
	}
	public void visit(ClassDecl classDecl)
	{
		m_IsClassScope = false;
	}
	
	//Methods
	//Enter method declaration
	public void visit(MethodBasicTypeName methodBasicTypeName) {
		if(m_IsClassScope == true) { return; }
		
		//Check if it is main method
		if(methodBasicTypeName.getMethName().equals("main"))
		{
			report_error("Greska: main() funkcija ne sme da vraca vrednost!", methodBasicTypeName);
		}
		
		m_currentMethod = Tab.insert(Obj.Meth, methodBasicTypeName.getMethName(), methodBasicTypeName.getType().struct);
		methodBasicTypeName.obj = m_currentMethod;
		m_formParmsMap.put(m_currentMethod, new ArrayList<Struct>());
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodBasicTypeName.getMethName(), methodBasicTypeName);
	}
	//Enter void method declaration
	public void visit(MethodVoidName methodVoidName) {
		if(m_IsClassScope == true) { return; }
		
		m_currentMethod = Tab.insert(Obj.Meth, methodVoidName.getMethName(), Tab.noType);
		methodVoidName.obj = m_currentMethod;
		//Check if it is main method
		if(methodVoidName.getMethName().equals("main"))
		{
			m_MainMethod = m_currentMethod;
		}
		
		m_formParmsMap.put(m_currentMethod, new ArrayList<Struct>());
		Tab.openScope();
		report_info("Obradjuje se funkcija " + methodVoidName.getMethName(), methodVoidName);
	}

	//Exit method declaration
	public void visit(MethodDecl methodDecl) {
		if(m_IsClassScope == true) { return; }
		if (!m_returnFound && m_currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funcija " + m_currentMethod.getName() + " nema return iskaz!", null);
		}
		
		Tab.chainLocalSymbols(m_currentMethod);
		Tab.closeScope();
		
		m_returnFound = false;
		m_currentMethod = null;
	}
	
	//Return with value
	public void visit(ReturnStmt returnStmt)
	{
		m_returnFound = true;
		Struct currMethType = m_currentMethod.getType();
		if (!currMethType.compatibleWith(returnStmt.getExpr().struct)) {
			report_error("Greska na liniji " + returnStmt.getLine() + " : " + "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije " + m_currentMethod.getName(), null);
		}	
	}
	//Blank return
	public void visit(BlankReturnStmt blankReturnStmt)
	{
		if(m_currentMethod.getType() != Tab.noType)
		{
			report_error("Greska: metoda " + m_currentMethod.getName() + " mora da vrati povratnu vrednost", blankReturnStmt);
		}
	}
	
	//Formal params 
	public void visit(FormalParamDecl formalParamDecl)
	{
		if(m_MainMethod == m_currentMethod)
		{
			report_error("Greska: main metod NE sme imati parametre!", formalParamDecl);
		}
		
		Struct paramType = formalParamDecl.getType().struct;
		Tab.insert(Obj.Var, formalParamDecl.getFpName(), paramType);
		ArrayList<Struct> currMethodFormParams = m_formParmsMap.get(m_currentMethod);
		currMethodFormParams.add(paramType);
	}
	//Actual params
	public void visit(ActualParam actualParam)
	{
		m_currentActualParamsTypes.add(actualParam.getExpr().struct);
	}
	public void visit(FuncCall funcCall)
	{
		Obj funcObj = funcCall.getDesignator().obj;
		CheckFuncArgs(funcObj, funcCall);
		m_currentActualParamsTypes.clear();
		PropagateFuncType(funcCall);
	}
	public void visit(StatementFuncCall statementFuncCall)
	{
		Obj funcObj = statementFuncCall.getDesignator().obj;
		CheckFuncArgs(funcObj, statementFuncCall);
		m_currentActualParamsTypes.clear();
	}
	
	//Helper function to check if actual params match formal
	private void CheckFuncArgs(Obj funcObj, SyntaxNode nodeInfo)
	{
		if(funcObj.getKind() != Obj.Meth)
		{
			report_error("Greska: " + funcObj.getName() + " NIJE funkcija", nodeInfo);
		}
		else
		{
			ArrayList<Struct> formParams = m_formParmsMap.get(funcObj);
			if(formParams.size() != m_currentActualParamsTypes.size())
			{
				report_error("Greska: pogresan broj parametara u funkciji" + funcObj.getName(), nodeInfo);
			}
			else
			{
				Iterator<Struct> formParamIt = formParams.iterator();
				Iterator<Struct> actParamIt = m_currentActualParamsTypes.iterator();
				while(formParamIt.hasNext())
				{
					Struct formStruct = formParamIt.next();
					Struct actStruct = actParamIt.next();
					if(formStruct.compatibleWith(actStruct) == false)
					{
						report_error("Greska: pogresan tip parametara u funkciji" + funcObj.getName(), nodeInfo);
						break;
					}
				}
			}
		}
	}
	
	//Assignment
	public void visit(Assignment assignment) 
	{
		if (!assignment.getExpr().struct.assignableTo(assignment.getDesignator().obj.getType()))
		{
			report_error("Greska na liniji " + assignment.getLine() + " : " + " nekompatibilni tipovi u dodeli vrednosti ", null);
		}
	}

	//MultiAssignment 0_0
	
	
	//PROPAGATE TYPES IN EXPRESSIONS
	//Factor
	public void visit(ConstNum constNum)
	{
		constNum.struct = Tab.intType;
	}
	public void visit(ConstChar constChar)
	{
		constChar.struct = Tab.charType;
	}
	public void visit(ConstBool constBool)
	{
		constBool.struct = boolType;
	}
	public void visit(Var var)
	{
		var.struct = var.getDesignator().obj.getType();
	}
	public void PropagateFuncType(FuncCall funcCall)
	{
		funcCall.struct = funcCall.getDesignator().obj.getType();
	}
	public void visit(ExprBrackets exprBrackets)
	{
		exprBrackets.struct = exprBrackets.getExpr().struct;
	}
	public void visit(NewArray newArray)
	{
		if(newArray.getExpr().struct != Tab.intType)
		{
			report_error("Greska: broj elemenata niza pri kreiranju mora da bude tipa int", newArray);
		}

		newArray.struct = new Struct(Struct.Array, newArray.getType().struct);
	}
	public void visit(NewSingle newSingle)
	{
		newSingle.struct = newSingle.getType().struct;
	}
	//Term
	public void visit(FactSingle factSingle) 
	{
		factSingle.struct = factSingle.getFactor().struct;
	}
	public void visit(FactExpr factExpr) 
	{
		Struct te = factExpr.getTerm().struct;
		Struct f = factExpr.getFactor().struct;
		if (te.equals(f) && te == Tab.intType)
			factExpr.struct = te;
		else {
			report_error("Greska na liniji "+ factExpr.getLine()+" : nekompatibilni tipovi u izrazu za mnozenje.", null);
			factExpr.struct = Tab.noType;
		}
	}
	//Expression
	public void visit(TermExpr termExpr) 
	{
		termExpr.struct = termExpr.getTerm().struct;
	}
	public void visit(AddExpr addExpr) 
	{
		Struct te = addExpr.getExprInner().struct;
		Struct t = addExpr.getTerm().struct;
		if (te.equals(t) && te == Tab.intType)
			addExpr.struct = te;
		else {
			report_error("Greska na liniji "+ addExpr.getLine()+" : nekompatibilni tipovi u izrazu za sabiranje.", null);
			addExpr.struct = Tab.noType;
		}
	}
	//(Minus) Expression
	public void visit(NegativeExpr negativeExpr) 
	{
		negativeExpr.struct = negativeExpr.getExprInner().struct;
	}
	public void visit(PositiveExpr positiveExpr) 
	{
		positiveExpr.struct = positiveExpr.getExprInner().struct;
	}

	public void visit(Designator designator){
		Obj obj = Tab.find(designator.getName());
		if (obj == Tab.noObj) { 
			report_error("Greska na liniji " + designator.getLine()+ " : ime "+designator.getName()+" nije deklarisano! ", null);
		}
		designator.obj = obj;
	}
	
	public boolean passed() {
		return !errorDetected;
	}
	
	//Helpers
	private void AddVarInfo(String name, boolean isArray)
	{
		m_innerVarList.add(new VarInfo(name, isArray));
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

