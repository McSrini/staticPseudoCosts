/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;

import static ca.mcmaster.staticpseudocosts.Constants.*;

/**
 *
 * @author tamvadss
 */
public class Parameters {
       
    public static final boolean USE_PURE_CPLEX = true;
    public static final String MIP_FILENAME = "opm2-z10-s4.pre.lp";  
    
    public static final int  USE_VAR_PRIORITY_LIST_FOR_HOURS = BILLION;
    public static final int  TEST_DURATION_HOURS = 24* 7;
    
    //public static final long PERF_VARIABILITY_RANDOM_SEED = ZERO;
    //public static final java.util.Random  PERF_VARIABILITY_RANDOM_GENERATOR = new  java.util.Random  (PERF_VARIABILITY_RANDOM_SEED);
    public static final int CPLEX_RANDOM_SEED = ZERO;
    //public static final  int MAX_THREADS = 32;
    
    public static final double  ALPHA = 0.833; 
    
      
    //public static final String MIP_FILENAME = "F:\\temporary files here\\lrsa120.pre.lp"; 
    //public static final String MIP_FILENAME = "F:\\temporary files here\\seymour-disj-10.pre.lp"; 
    //public static final String MIP_FILENAME = "F:\\temporary files here\\p6b.pre.lp"; 
    
    
    public static final int FILE_STRATEGY= 3;
     
}
