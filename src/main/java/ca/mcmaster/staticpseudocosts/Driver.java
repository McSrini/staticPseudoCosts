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
    private static Map < String, Double > pseudoCostMap;
    private static Map < String, Integer > priorityMap= new HashMap < String, Integer > ();;
    
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
        
        if (! USE_PURE_CPLEX){        
            //read in the MIP
            cplex =  new IloCplex();
            cplex.importModel(  MIP_FILENAME);

            //strong branch till ready to branch root node, use single thread for this "ramp-up"
            cplex.setParam( IloCplex.Param.Threads,  ONE);
            cplex.setParam( IloCplex.Param.MIP.Strategy.VariableSelect  ,  THREE);
            cplex.setParam( IloCplex.Param.MIP.Limits.StrongCand  , BILLION );
            cplex.setParam( IloCplex.Param.MIP.Limits.StrongIt ,  BILLION );
            //disable heuristics
            cplex.setParam( IloCplex.Param.MIP.Strategy.HeuristicFreq , -ONE);

            //using callback to find   pseudo costs
            BranchHandler branchHandler = new BranchHandler (getVariables(cplex).values());
            cplex.use( branchHandler);        ;
            logger.info ("Starting strong branching ..." ) ;
            System.out.println ("Starting strong branching ...") ;
            double startTime =  System.currentTimeMillis();
            cplex.solve ( );
            logger.info ("Completed strong branching. Took minutes " + (System.currentTimeMillis()-startTime )/ (TWO*THREE*TEN*THOUSAND)) ;
            System.out.println ("Completed strong branching. Took minutes " + (System.currentTimeMillis()-startTime )/ (TWO*THREE*TEN*THOUSAND)) ;
            pseudoCostMap=branchHandler.pseudoCostMap ;

            //prepare variable priorities
            initializePriorities();
            cplex.end();
        }
                        
        
        //reload the MIP, attach information callback and variable priorities
        //solve for some time with static pseudo-costs, then clear priorities
        //stop test after 24 hours, print progress after every hour
        //
        //if pure cplex then no priorities are set
        //
        
        cplex =  new IloCplex();
        cplex.importModel(  MIP_FILENAME); 
        cplex.setParam(IloCplex.Param.RandomSeed,  CPLEX_RANDOM_SEED);
        //cplex.setParam( IloCplex.Param.Threads, MAX_THREADS);
        cplex.setParam(IloCplex.Param.MIP.Strategy.File,  FILE_STRATEGY);   
        Map<String, IloNumVar> newVars = getVariables (  cplex);
        if (! USE_PURE_CPLEX){        
            for ( IloNumVar newVar : newVars.values()) {
                cplex.setPriority(  newVar , priorityMap.get (newVar.getName()) );
            }
        }
        
        for (int hours=ZERO; hours <  TEST_DURATION_HOURS; hours ++){            
            if (isHaltFilePresent()) break;
            cplex.setParam( IloCplex.Param.TimeLimit, SIXTY *SIXTY);
            cplex.solve ( );
            logger.info ((hours + ONE)+"," + cplex.getBestObjValue() +
                         ","  + cplex.getObjValue() + 
                         ","  +cplex.getNnodes64() + 
                         ","  +cplex.getNnodesLeft64()) ;
            
            if (cplex.getStatus().equals( IloCplex.Status.Infeasible)) break;
            if (cplex.getStatus().equals( IloCplex.Status.Optimal)) break;
            
            if (USE_VAR_PRIORITY_LIST_FOR_HOURS==hours && !USE_PURE_CPLEX) {
                //remove var priority list
                final IloNumVar[] emptyVarArray = new IloNumVar[]{};
                cplex.delPriorities(  newVars.values().toArray(emptyVarArray));
                logger.info ("var priority list has been reset") ;
            }
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
        logger.info ("USE_PRIORITY_LIST_FOR_HOURS "+ USE_VAR_PRIORITY_LIST_FOR_HOURS) ;  
        logger.info (" TEST_DURATION_HOURS "+  TEST_DURATION_HOURS) ;
        logger.info (" CPLEX_RANDOM_SEED "+ CPLEX_RANDOM_SEED ) ;
        logger.info (" ALPHA "+  ALPHA) ;
        //logger.info (" MAX_THREADS  "+ MAX_THREADS  ) ;
        logger.info ("  FILE_STRATEGY "+  FILE_STRATEGY ) ;
       
    }
    
    private static void  initializePriorities (){
       
        //map stores lowest pseudo cost first
        TreeMap <  Double, List<String> > invertedPseudoCostMap = new TreeMap <  Double, List<String> >();
        
        for (Map.Entry < String, Double > entry :pseudoCostMap .entrySet()){
            double thisVal =   entry.getValue();
            List<String> currentList = invertedPseudoCostMap.get (thisVal) ;
            if (currentList==null)  currentList = new ArrayList<String> ();
            currentList.add (entry.getKey());
            invertedPseudoCostMap.put (thisVal, currentList) ;
        }
        
        //higher number = higher priority
        int currentPriority = ONE;
        for (Map.Entry<  Double, List<String> > entry : invertedPseudoCostMap.entrySet()){
            for (String varName : entry.getValue()){
                priorityMap.put (varName, currentPriority) ;
            }
            currentPriority++;
        }
        
        
                    
    }
}
