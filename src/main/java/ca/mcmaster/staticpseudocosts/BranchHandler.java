/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.staticpseudocosts;

import static ca.mcmaster.staticpseudocosts.Constants.*;
import static ca.mcmaster.staticpseudocosts.Parameters.ALPHA;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex.BranchCallback;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class BranchHandler extends BranchCallback {
    
    private Collection<IloNumVar> variableList ;
    public Map < String, Double > pseudoCostMap= new HashMap < String, Double > ( );
     
    public BranchHandler ( Collection<IloNumVar> vars) {
        variableList= vars;
    }

    @Override
    protected void main() throws IloException {
        if (getNbranches() > ZERO) {
         
            for (IloNumVar var : variableList ){
                
                double downPseudoCost =  getDownPseudoCost(  var);
                if (downPseudoCost < ZERO) downPseudoCost*=-ONE;
                
                double upPseudoCost =   getUpPseudoCost(  var);
                if (upPseudoCost<ZERO)  upPseudoCost *=-ONE;
                                
                double pseudoCostMax =  downPseudoCost>upPseudoCost ? downPseudoCost:upPseudoCost;
                double pseudoCostMin =  downPseudoCost<upPseudoCost ? downPseudoCost:upPseudoCost;
                
                double pseudoCost = ALPHA*pseudoCostMin + (ONE- ALPHA) * pseudoCostMax;
                pseudoCostMap.put (var.getName(), pseudoCost ) ;               

            }

            //done
            abort ();
        }
    }
    
}
