/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;
 
import static ca.mcmaster.staticpseudocosts.Constants.*;
import static ca.mcmaster.staticpseudocosts.Parameters.*;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex; 
import java.io.File;
import static java.lang.System.exit; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author tamvadss
 */
public class Driver {
    
       
    private static Logger logger = Logger.getLogger(Driver.class);
    private  static  IloCplex cplex  ;
     
    static {
        logger.setLevel( LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  RollingFileAppender(layout,LOG_FOLDER+Driver.class.getSimpleName()+ LOG_FILE_EXTENSION);
            rfa.setMaxBackupIndex(SIXTY);
            logger.addAppender(rfa);
            logger.setAdditivity(false);
        } catch (Exception ex) {
            ///
            System.err.println("Exit: unable to initialize logging"+ex);       
            exit(ONE);
        }
    } 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        printParameters();
        
        //read in the MIP
        cplex =  new IloCplex();
        cplex.importModel(  MIP_FILENAME);
        //disable heuristics
        if (DISABLE_HUERISTICS) cplex.setParam( IloCplex.Param.MIP.Strategy.HeuristicFreq , -ONE);
        
               
        if (! USE_PURE_CPLEX){        
            
            //strong branch till ready to branch root node
            cplex.setParam( IloCplex.Param.MIP.Strategy.VariableSelect  ,  THREE);
            cplex.setParam( IloCplex.Param.MIP.Limits.StrongCand  , BILLION );
            cplex.setParam( IloCplex.Param.MIP.Limits.StrongIt ,  BILLION );
            
            //using callback to find   pseudo costs
            BranchHandler branchHandler = new BranchHandler (getVariables(cplex).values());
            NodeHandler nodeHandler = new NodeHandler ();
            cplex.use( branchHandler);        ;
            cplex.use (nodeHandler );
            logger.info ("Starting strong branching ..." ) ;
            System.out.println ("Starting strong branching ...") ;
            double startTime =  System.currentTimeMillis();
            cplex.solve ( );
            logger.info ("Completed strong branching. Took minutes " + (System.currentTimeMillis()-startTime )/ (TWO*THREE*TEN*THOUSAND)) ;
            System.out.println ();
            System.out.println ("Completed strong branching. Took minutes " + (System.currentTimeMillis()-startTime )/ (TWO*THREE*TEN*THOUSAND)) ;
            System.out.println ();
            
            cplex.clearCallbacks();
             
        }
                    
        //cplex.use ( new EmptyBranchCallback ());
        //use pseudo cost based branching strategy
        cplex.setParam( IloCplex.Param.MIP.Strategy.VariableSelect  ,  TWO);
        //solve for 1 hour at a a time, and print progress
        cplex.setParam( IloCplex.Param.TimeLimit, SIXTY *SIXTY);
        //use empty callback
        cplex.use (new EmptyBranchHandler ()) ;
        cplex.setParam( IloCplex.Param.Threads,  MAX_THREADS);
            
        //now solve in 1 hour time slices
        for (int hours=ZERO; hours <  TEST_DURATION_HOURS; hours ++ ){     

            if (isHaltFilePresent()) break;            
            if (cplex.getStatus().equals( IloCplex.Status.Infeasible)) break;
            if (cplex.getStatus().equals( IloCplex.Status.Optimal)) break;
            
            System.out.println("Iteration " + (ONE+hours) +" Solving for 1 hour. Starting number of leafs is = " + cplex.getNnodesLeft64()) ;
            cplex.solve ( );
             
            boolean isfeasible = cplex.getStatus().equals(IloCplex.Status.Feasible);
            boolean isOptimal = cplex.getStatus().equals(IloCplex.Status.Optimal);
            boolean hasSolution = isfeasible || isOptimal;
                       
            logger.info ((ONE+hours)+"," + cplex.getBestObjValue() +
                         ","  + (hasSolution ? cplex.getObjValue() : BILLION )+ 
                         ","  +cplex.getNnodes64() + 
                         ","  +cplex.getNnodesLeft64()) ;            
        }
            
        logger.info("Solution status : "+ cplex.getStatus()) ;
        System.out.println("Solution status : "+ cplex.getStatus()) ;
        cplex.end();

        logger.info ("Test Completed !") ;
        System.out.println ("Test Completed !") ;
                        
    }
    
    private static Map<String, IloNumVar> getVariables (IloCplex cplex) throws IloException{
        Map<String, IloNumVar> result = new HashMap<String, IloNumVar>();
        IloLPMatrix lpMatrix = (IloLPMatrix)cplex.LPMatrixIterator().next();
        IloNumVar[] variables  =lpMatrix.getNumVars();
        for (IloNumVar var :variables){
            result.put(var.getName(),var ) ;
        }
        return result;
    }
       
    private static boolean isHaltFilePresent (){
        File file = new File(HALT_FILE );         
        return file.exists();
    }
        
    private static void printParameters (){
        
        logger.info (" MIP_FILENAME "+  MIP_FILENAME) ;
        logger.info ("USE_PURE_CPLEX "+ USE_PURE_CPLEX) ;
        logger.info ("DISABLE_HUERISTICS "+ DISABLE_HUERISTICS) ;  
        logger.info (" TEST_DURATION_HOURS "+  TEST_DURATION_HOURS) ;
        logger.info (" CPLEX_RANDOM_SEED "+ CPLEX_RANDOM_SEED ) ;
        logger.info (" MAX_THREADS "+ MAX_THREADS ) ;        
        logger.info ("  FILE_STRATEGY "+  FILE_STRATEGY ) ;
       
    }
        
}
