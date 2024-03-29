package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.AddExpr;
import rs.ac.bg.etf.pp1.ast.Addop;
import rs.ac.bg.etf.pp1.ast.Assignment;
import rs.ac.bg.etf.pp1.ast.BlankReturnStmt;
import rs.ac.bg.etf.pp1.ast.ClassDecl;
import rs.ac.bg.etf.pp1.ast.ClassDeclEnter;
import rs.ac.bg.etf.pp1.ast.ConstBool;
import rs.ac.bg.etf.pp1.ast.ConstChar;
import rs.ac.bg.etf.pp1.ast.ConstNum;
import rs.ac.bg.etf.pp1.ast.Decrement;
import rs.ac.bg.etf.pp1.ast.DesignatorBasic;
import rs.ac.bg.etf.pp1.ast.DesignatorElem;
import rs.ac.bg.etf.pp1.ast.DesignatorMatrixElem;
import rs.ac.bg.etf.pp1.ast.Divop;
import rs.ac.bg.etf.pp1.ast.EmptyDes;
import rs.ac.bg.etf.pp1.ast.FactExpr;
import rs.ac.bg.etf.pp1.ast.FindAnyStmt;
import rs.ac.bg.etf.pp1.ast.FuncCall;
import rs.ac.bg.etf.pp1.ast.Increment;
import rs.ac.bg.etf.pp1.ast.MethodBasicTypeName;
import rs.ac.bg.etf.pp1.ast.MethodDecl;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MethodVoidName;
import rs.ac.bg.etf.pp1.ast.Minusop;
import rs.ac.bg.etf.pp1.ast.Mulop;
import rs.ac.bg.etf.pp1.ast.MultiAssignment;
import rs.ac.bg.etf.pp1.ast.NegativeExpr;
import rs.ac.bg.etf.pp1.ast.NewArray;
import rs.ac.bg.etf.pp1.ast.NewMatrix;
import rs.ac.bg.etf.pp1.ast.NonEmptyDes;
import rs.ac.bg.etf.pp1.ast.Percop;
import rs.ac.bg.etf.pp1.ast.SpecNumConst;
import rs.ac.bg.etf.pp1.ast.StatementFuncCall;
import rs.ac.bg.etf.pp1.ast.PrintStmt;
import rs.ac.bg.etf.pp1.ast.ReadStmt;
import rs.ac.bg.etf.pp1.ast.ReturnStmt;
import rs.ac.bg.etf.pp1.ast.SingleDesignator;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.Var;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.mj.runtime.Code;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

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
	Obj m_Counter = null;
	
	boolean m_IsClassScope = false;
	int m_NumOfPrints = 1;
	
	enum DataType
	{
		Invalid,
		Variable, 
		Array, 
		Matrix
	}
	DataType m_NewType = DataType.Invalid;
	
	//For multi assignment
	class AssignInfo
	{
		public Obj des;
		public int pos;
		public Obj arrayObj;
		public AssignInfo(Obj o, int p) { des=o; pos = p; }
	}
	ArrayList<AssignInfo> m_MultiAssignmentInfos = new ArrayList<AssignInfo>();
	int m_totalElemInMultiAssignment = 0;
	//int m_lastArrayAddress = 0;
	//HashMap<Obj, Integer> m_ArrayAddress = new HashMap<Obj, Integer>();
	Obj m_LastArray = null;
	public HashMap<String, Obj> m_ArrayObjs = new HashMap<String, Obj>();
	int m_numOfElemsInMulti = 0;
	
	//Enums for operations - DEPRECATED
	enum MulopType
	{
		None, 
		Mulop, 
		Divop, 
		Percop
	}
	enum AddopType
	{
		None, 
		Addop, 
		Minusop
	}
	MulopType m_Mulop = MulopType.None;
	AddopType m_Addop = AddopType.None;
	
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
	
	public void visit(ReturnStmt returnStmt) 
	{
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(BlankReturnStmt blankReturnStmt)
	{
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(MethodDecl methodDecl)
	{
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(Assignment assignment) 
	{
		Obj dest = assignment.getDesignator().obj;
		//Expand stack for element
		if(dest.getKind() == Obj.Elem)
		{
			Code.load(m_LastArray);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.store(dest);
			return;
		}
		
		DataType destDataType = GetDataType(dest);
		
		switch(destDataType)
		{
		
		case Array:
		case Matrix:
			m_ArrayObjs.put(dest.getName(), dest);
		case Variable:
			Code.store(dest);
			break;
		default:	
		}
		if(dest.getType().getKind() == Struct.Array)
		{
			if(m_NewType == DataType.Array)
			{
				//m_ArrayObjs.put(dest.getName(), dest);
				//Code.store(dest);
			}
			else
			{
				//MODIFIKACIJA FEBRUAR - kopiranje niza, niz1 = niz2;
				//srcAdr
				/*Code.put(Code.dup);

				
				Code.put(Code.dup); //srcAdr, srcAdr
				Code.put(Code.arraylength); //srcAdr, length(srcAdr)
				Code.store(m_Counter);

				Code.put(Code.dup); //ovde se vratiti
				Code.load(m_Counter);
				Code.loadConst(1);
				Code.put(Code.sub);
				Code.put(Code.dup);
				Code.loadConst(-1);
				Code.put(Code.jcc + Code.eq);
				Code.put2(24);
				Code.store(m_Counter); //m_counter = length
				
				Code.load(m_Counter);
				
				Code.put(Code.aload); // srcAdr[cnt];
				Code.load(m_Counter); // srcAdr[cnt], index
				Code.put(Code.dup_x1);
				Code.put(Code.pop); // index, srcAdr[int]
				Code.load(dest);
				Code.put(Code.dup_x2);
				Code.put(Code.pop); // destAdr, index, srcAdr[int]
				Code.put(Code.astore);
				Code.put(Code.jmp);
				Code.put2(-29);
				*/
			}
			return;
		}
	
		
		
	}
	
	public void visit(DesignatorBasic designator) {
		SyntaxNode parent = designator.getParent();
		if (	Assignment.class != parent.getClass() && 
				FuncCall.class != parent.getClass() && 
				StatementFuncCall.class != parent.getClass() &&
				SingleDesignator.class != parent.getClass() &&
				NonEmptyDes.class != parent.getClass() &&
				MultiAssignment.class != parent.getClass()) 
		{
			Code.load(designator.obj);
		}
	}
	
	public void visit(DesignatorElem designator) {
		m_LastArray = m_ArrayObjs.get(designator.getName());
		
		SyntaxNode parent = designator.getParent();
		if (	Assignment.class != parent.getClass() && 
				FuncCall.class != parent.getClass() && 
				StatementFuncCall.class != parent.getClass() &&
				SingleDesignator.class != parent.getClass() &&
				NonEmptyDes.class != parent.getClass() &&
				MultiAssignment.class != parent.getClass()) 
		{
			//Expand stack for element
			if(designator.obj.getKind() == Obj.Elem)
			{
				Code.load(m_LastArray);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
			}
			Code.load(designator.obj);
		}
	}
	
	public void visit(DesignatorMatrixElem designator) {
		m_LastArray = m_ArrayObjs.get(designator.getName());
		
		SyntaxNode parent = designator.getParent();
		if (	Assignment.class != parent.getClass() && 
				FuncCall.class != parent.getClass() && 
				StatementFuncCall.class != parent.getClass() &&
				SingleDesignator.class != parent.getClass() &&
				NonEmptyDes.class != parent.getClass() &&
				MultiAssignment.class != parent.getClass()) 
		{
			//Expand stack for element
			if(designator.obj.getKind() == Obj.Elem)
			{
				//on stack: ^ e2 e1
				Code.load(m_LastArray);
				//on stack: ^ e2 e1 addrMatrix
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				//on stack: ^ e2 addrMatrix e1
				Code.put(Code.aload);
				//on stack: ^ e2 addrArray
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				//on stack: ^ addrArray e2
			}
			Code.load(designator.obj);
		}
	}
	
	//Print statement
	public void visit(PrintStmt printStmt) {
		if(printStmt.getExpr().struct == Tab.intType || printStmt.getExpr().struct == boolType)
		{
			Code.loadConst(5);
			for(int i = 1; i < m_NumOfPrints; i++)
			{
				Code.put(Code.dup2);
				Code.put(Code.print);
			}
			Code.put(Code.print);
		}
		else
		{
			Code.loadConst(1);
			for(int i = 1; i < m_NumOfPrints; i++)
			{
				Code.put(Code.dup2);
				Code.put(Code.bprint);
			}
			Code.put(Code.bprint);
		}
		m_NumOfPrints = 1;
	}
	public void visit(SpecNumConst specNumConst)
	{
		m_NumOfPrints = specNumConst.getRepeatVal();
	}
	
	//Read statement
	public void visit(ReadStmt readStmt)
	{
		Struct desType = readStmt.getDesignator().obj.getType();
		if(desType == Tab.intType || desType == boolType)
		{
			Code.put(Code.read);
		}
		else
		{
			Code.put(Code.bread);
		}
		//Expand stack for element
		if(readStmt.getDesignator().obj.getKind() == Obj.Elem)
		{
			Code.load(m_LastArray);
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
		}
		Code.store(readStmt.getDesignator().obj);
	}
	
	//FindAny
	public void visit(FindAnyStmt findAnyStmt)
	{
		Obj boolForStore = findAnyStmt.getDesignator().obj;		
		Obj arrayForSearch = findAnyStmt.getDesignator1().obj;
		
		//bSt = aSr.findAny(exp);
		//------------------------------
		//m_counter = size(aSr);
		//while (m_counter > 0) // if m_counter == 0 jmp -> end_of_loop
		//{
		//	if (aSr[m_counter] == exp)
		//	{
		//		bSr = true;
		//		return;
		//	}
		//	m_counter--;
		//}
		//bSr = false;
		
		//Initial stack: ^ bSt aSr exp  
		
		//Initialise counter
		Code.put(Code.dup_x1); //^ bSt exp aSr exp
		Code.put(Code.pop); //^ bSt exp aSr
		Code.put(Code.dup); //^ bSt exp aSr aSr 
		Code.put(Code.arraylength); //^ bSt exp aSr length(aSr)
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(m_Counter); //^ bSt exp aSr ; m_Counter = length(aSr) - 1;
		
		//Begin loop
		int loopStart = Code.pc;
		Code.load(m_Counter); //^ bSt exp aSr m_Counter
		Code.loadConst(0); //^ bSt exp aSr m_Counter 0
		Code.putFalseJump(Code.ge, 0); //^ bSt exp aSr
		int fixup1 = Code.pc - 2;
		
		//Body of loop
		Code.put(Code.dup_x1); //^ bSt aSr exp aSr
		
		Code.load(m_Counter); //^ bSt aSr exp aSr m_Counter
		int instrCode = Code.aload;
		if(arrayForSearch.getType().getElemType() == Tab.charType)
		{
			instrCode = Code.baload;
		}
		Code.put(instrCode); //^ bSt aSr exp aSr[m_Counter]
		
		//Stack shenanigans
		Code.put(Code.dup2);
		Code.put(Code.pop); //^ bSt aSr exp aSr[m_Counter] exp
		
		
		//Condition inside loop
		Code.putFalseJump(Code.eq, 0); //^ bSt aSr exp
		int fixup2 = Code.pc - 2;
		//Code.put(Code.dup_x2); //^ exp bSt aSr exp
		//Code.put(Code.pop); //^ exp bSt aSr
		//Code.put(Code.dup_x2); //^aSr exp bSt aSr
		//Code.put(Code.pop); //^aSr exp bSt
		Code.loadConst(1);
		Code.store(boolForStore); //bSt = true
		Code.putJump(0); //return
		int fixup3 = Code.pc - 2;
		
		//End loop
		Code.fixup(fixup2);
		//Fix stack for next iteration
		//Should be: ^ exp bSt aSr
		//Right now: ^ bSt aSr exp
		Code.put(Code.dup_x2);
		Code.put(Code.pop);
		//Decrement counter
		Code.load(m_Counter);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(m_Counter);
		Code.putJump(loopStart);
		Code.fixup(fixup1);
		
		Code.loadConst(0);
		Code.store(boolForStore); //bSt = false
		
		Code.fixup(fixup3);
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
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
		//Code.load(var.getDesignator().obj);
	}
	public void visit(FuncCall FuncCall) {
		Obj functionObj = FuncCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc; 
		Code.put(Code.call);
		Code.put2(offset);
		//Return will put return value on stact via expr!
	}
	public void visit(NewArray newArray)
	{
		Code.put(Code.newarray);
		Struct elemType = newArray.getType().struct;
		if(elemType.equals(Tab.intType) == true)
		{
			Code.put(1);
		}
		else
		{
			Code.put(0);
		}
		m_NewType = DataType.Array;
		//This will leave address of array on stack!
	}
	public void visit(NewMatrix newMatrix)
	{
		/*
		int loopStart;
		int fixupAddr;
		//on stack: ^ e2 e1 
		Code.put(Code.newarray);
		Struct elemType = newMatrix.getType().struct;
		Code.put(1);
		//This will leave address of array (size e1) of adresses of arrays on stack!
		//on stack: ^ e2 addrMatrix
		Code.put(Code.dup);
		//on stack: ^ e2 addrMatrix addrMatrix
		Code.put(Code.arraylength);
		//on stack: ^ e2 addrMatrix e1
		Code.store(m_Counter); //m_Counter = e1
		//on stack: ^ e2 addrMatrix
		loopStart = Code.pc;
		Code.load(m_Counter);
		Code.loadConst(0);
		Code.put(Code.jcc + Code.eq);
		Code.put2(0);
		fixupAddr = Code.pc - 2;
		//do smt..
		//on stack: ^ e2 addrMatrix
		Code.put(Code.dup2);
		Code.put(Code.pop);
		//on stack: ^ e2 addrMatrix e2
		Code.put(Code.newarray);
		if(elemType.equals(Tab.intType) == true)
		{
			Code.put(1);
		}
		else
		{
			Code.put(0);
		}
		//on stack: ^ e2 addrMatrix addrArray
		//we need: ^ e2 addrMatrix m_counter addrArray
		Code.load(m_Counter);
		//on stack: ^ e2 addrMatrix addrArray m_Counter
		Code.put(Code.dup_x1);
		//on stack: ^ e2 addrMatrix m_Counter addrArray m_Counter
		Code.put(Code.pop);
		//on stack: ^ e2 addrMatrix m_Counter addrArray
		Code.put(Code.astore);
		//on stack: ^ e2 addrMatrix
		//loop exit
		Code.load(m_Counter);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(m_Counter);
		//better fixup
		Code.put2(fixupAddr, (Code.pc-fixupAddr + 1) + 3);
		Code.putJump(loopStart);
		//on stack: ^ e2 addrMatrix
		Code.put(Code.dup_x1);
		//on stack: ^ addrMatrix e2 addrMatrix
		Code.put(Code.pop);
		Code.put(Code.pop);
		//on stack: ^ addrMatrix
		
		m_NewType = DataType.Matrix;
		//This will leave address of array on stack!
		 
		 */
	}
	
	//Save operation - DEPRECATED
	public void visit(Addop addop)
	{
		m_Addop = AddopType.Addop;
	}
	public void visit(Minusop minusop)
	{
		m_Addop = AddopType.Minusop;
	}
	public void visit(Mulop mulop)
	{
		m_Mulop = MulopType.Mulop;
	}
	public void visit(Divop divop)
	{
		m_Mulop = MulopType.Divop;
	}
	public void visit(Percop percop)
	{
		m_Mulop = MulopType.Percop;
	}
	//Code operations
	public void visit(FactExpr factExpr)
	{
		//Operands already on stack
		if(factExpr.getMulop() instanceof Divop)
		{
			Code.put(Code.div);
		}
		else if (factExpr.getMulop() instanceof Percop)
		{
			Code.put(Code.rem);
		}
		else
		{
			Code.put(Code.mul);
		}
	}
	public void visit(AddExpr addExpr)
	{
		//Operands already on stack
		if(addExpr.getAddop() instanceof Minusop)
		{
			Code.put(Code.sub);
		}
		else
		{
			Code.put(Code.add);
		}
	}
	public void visit(NegativeExpr negativeExpr)
	{
		Code.put(Code.neg);
	}
	
	//Increment
	public void visit(Increment increment)
	{
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(increment.getDesignator().obj);
	}
	//Decrement
	public void visit(Decrement decrement)
	{
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(decrement.getDesignator().obj);
	}
	
	//MultiAssignment 0_0
	public void visit(MultiAssignment multiAssignment)
	{
		Obj rightDesignator = multiAssignment.getDesignator().obj;
		int instrCode = Code.aload;
		if(rightDesignator.getType().getElemType() == Tab.charType)
		{
			instrCode = Code.baload;
		}
		Stack<AssignInfo> elems = new Stack<AssignInfo>();
		for(AssignInfo aInfo : m_MultiAssignmentInfos)
		{
			if(aInfo.des.getKind() != Obj.Elem)
			{
				Code.load(rightDesignator);
				Code.loadConst(aInfo.pos);
				Code.put(instrCode);
				Code.store(aInfo.des);
			}
			else
			{
				elems.push(aInfo);
			}
		}
		
		while(!elems.empty()) {

			AssignInfo temp = elems.pop();
			
			//Expand stack for element
			Code.load(temp.arrayObj);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			
			Code.load(rightDesignator);
			Code.loadConst(temp.pos);
			Code.put(instrCode);
			
			Code.store(temp.des);
		}

		m_MultiAssignmentInfos.clear();
		m_totalElemInMultiAssignment = 0;
		m_numOfElemsInMulti = 0;
	}
	//Count actual designators, including blanks, store info
	public void visit(EmptyDes emptyDes)
	{
		m_totalElemInMultiAssignment++;
	}
	public void visit(NonEmptyDes nonEmptyDes)
	{
		AssignInfo info = new AssignInfo(nonEmptyDes.getDesignator().obj, m_totalElemInMultiAssignment);
		if(nonEmptyDes.getDesignator().obj.getKind() == Obj.Elem)
		{
			info.arrayObj = m_LastArray;
			m_numOfElemsInMulti++;
		}
		m_MultiAssignmentInfos.add(info);
		m_totalElemInMultiAssignment++;
	}
	
	//HELPERS
	private DataType GetDataType(Obj obj)
	{
		Struct objStruct = obj.getType();
		if(objStruct.getKind() == Struct.Array)
		{
			if(objStruct.getElemType().getKind() == Struct.Array)
			{
				return DataType.Matrix;
			}
			return DataType.Array;
		}
		return DataType.Variable;
	}

}