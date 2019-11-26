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
       
    public static final boolean USE_PURE_CPLEX = false;
    
    //public static final String MIP_FILENAME = "opm2-z10-s4.pre.lp";  
    //public static final String MIP_FILENAME = "b1c1s1.pre.lp";  
    public static final String MIP_FILENAME = "comp212idx.pre.lp";  
    //public static final String MIP_FILENAME = "bnatt500.pre.lp";  
    //public static final String MIP_FILENAME = "F:\\temporary files here\\b1c1s1.pre.lp"; 
    
    public static final int  TEST_DURATION_HOURS = 24* 7;
    
    public static final int CPLEX_RANDOM_SEED = ZERO;
    
    public static final boolean  DISABLE_HUERISTICS = true; 
    
    public static final int FILE_STRATEGY= 3;
    
    public static final int MAX_THREADS= 32;
     
}
