/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.Refine;

import edu.wisc.cs.will.FOPC.Term;
import edu.wisc.cs.will.ILP.SingleClauseNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author 317005
 */
public class Refine {
        HashMap<String, RefineNode> refines = null;
        int Tree = 0;
        
        public Refine() {
                refines = new HashMap<String, RefineNode>();
        }
        
        public void addRefine(int model, String key, String node, boolean leftBranch, boolean rightBranch) {
                RefineNode rule = refines.get(model + ";" + key);
                
                if(rule == null) {
                    rule = new RefineNode(node, leftBranch, rightBranch);
                    refines.put(model + ";" + key, rule);
                }
        }
        
        public RefineNode getRefineNode(boolean[] tree) {
                ArrayList<String> str = new ArrayList<String>();
                for (boolean bool : tree) {
                    str.add(String.valueOf(bool));
                }
                if (str.size() == 0)
                {
                    return refines.get(this.Tree + ";");
                }
                return refines.get(this.Tree + ";" + String.join(",", str));
        }
        
        public boolean containsRefineNode(boolean[] tree) {
                ArrayList<String> str = new ArrayList<String>();
                for (boolean bool : tree) {
                    str.add(String.valueOf(bool));
                }
                if (str.size() == 0)
                {
                    return refines.containsKey(this.Tree + ";");
                }
                return refines.containsKey(this.Tree + ";" + String.join(",", str));
        }
        
        public RefineNode getParentRefineNode(boolean[] tree) {
            boolean[] newTree = Arrays.copyOfRange(tree, 0, tree.length-1);
            return getRefineNode(newTree);
        }
        
        public SingleClauseNode getExtensionNode(boolean[] tree) {
            if (tree.length == 0)
            {
                return null;
            }
            RefineNode parent = getParentRefineNode(tree);
            if (tree[tree.length-1] == true)
            {
                return parent.getSingleNode();
            }else{
                return parent.getSingleNoNode();
            }
        }
        
        public HashMap<String, Term> getExtensionNodeVariables(boolean[] tree) {
            if (tree.length == 0)
            {
                return null;
            }
            RefineNode parent = getParentRefineNode(tree);
            if (tree[tree.length-1] == true)
            {
                return parent.getSingleNodeVariables();
            }else{
                return parent.getSingleNoNodeVariables();
            }
        }
        
        public void setTree(int model) {
            this.Tree = model;
        }
        
//        public RefineNode getParentValidRefineNode(boolean[] tree) {
//                boolean[] newTree = Arrays.copyOfRange(tree, 0, tree.length-1);
//                while(newTree.length > 0 && newTree[newTree.length-1] == false)
//                {
//                    newTree = Arrays.copyOfRange(newTree, 0, newTree.length-1);
//                }
//        }
}
