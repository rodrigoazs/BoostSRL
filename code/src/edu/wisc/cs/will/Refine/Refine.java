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
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Rodrigo Azevedo
 */
public class Refine {
        //HashMap<String, RefineNode> refines = null;
        HashMap<String, RefineTreeNode> refines2 = null;
        int Tree = 0;
        
        public Refine() {
            //refines = new HashMap<String, RefineNode>();
            refines2 = new HashMap<String, RefineTreeNode>();
        }
        
        public void addNode(int model, String key, String node, boolean leftBranch, boolean rightBranch) {
            String treeNumber = Integer.toString(model);
            RefineTreeNode treeNode = new RefineTreeNode();
            RefineNode rule = null;
            
            if (refines2.containsKey(treeNumber)){
                treeNode = refines2.get(treeNumber);
            }else{
                refines2.put(treeNumber, treeNode);
            }
            
            RefineTreeNode current = treeNode;
            String[] path = key.split(",");
            if (key.isEmpty()){
                rule = new RefineNode(node, leftBranch, rightBranch);
                treeNode.setNode(rule);
            }else{
                rule = new RefineNode(node, leftBranch, rightBranch);
                for (String p : path) {
                    if (p.equals("true")) {
                        RefineTreeNode left = current.getLeftBranch();
                        if (left == null) {
                            left = new RefineTreeNode();
                            current.setLeftBranch(left);
                        }
                        current = left;
                    }else{
                        RefineTreeNode right = current.getRightBranch();
                        if (right == null) {
                            right = new RefineTreeNode();
                            current.setRightBranch(right);
                        }
                        current = right;
                    }
                }
                current.setNode(rule);
            }
        }
        
        /*public void addNode_old(int model, String key, String node, boolean leftBranch, boolean rightBranch) {
            RefineNode rule = refines.get(model + ";" + key);

            if(rule == null) {
                rule = new RefineNode(node, leftBranch, rightBranch);
                refines.put(model + ";" + key, rule);
            }
        }*/
        
        public RefineNode getRefineNode(boolean[] tree) {
            ArrayList<String> str = new ArrayList<String>();
            for (boolean bool : tree) {
                str.add(String.valueOf(bool));
            }
            if (str.isEmpty())
            {
                return refines2.get(Integer.toString(this.Tree)).getNode();
            }
            RefineTreeNode current = refines2.get(Integer.toString(this.Tree));
            for (boolean p : tree) {
                if (p) {
                    current = current.getLeftBranch();
                }else{
                    current = current.getRightBranch();
                }
            }
            return current.getNode();
        }
        
        public RefineTreeNode getRefineTreeNode(boolean[] tree) {
            ArrayList<String> str = new ArrayList<String>();
            for (boolean bool : tree) {
                str.add(String.valueOf(bool));
            }
            if (str.isEmpty())
            {
                return refines2.get(Integer.toString(this.Tree));
            }
            RefineTreeNode current = refines2.get(Integer.toString(this.Tree));
            for (boolean p : tree) {
                if (p) {
                    current = current.getLeftBranch();
                }else{
                    current = current.getRightBranch();
                }
            }
            return current;
        }
        
        /*public RefineNode getRefineNode_old(boolean[] tree) {
            ArrayList<String> str = new ArrayList<String>();
            for (boolean bool : tree) {
                str.add(String.valueOf(bool));
            }
            if (str.isEmpty())
            {
                return refines.get(this.Tree + ";");
            }
            return refines.get(this.Tree + ";" + String.join(",", str));
        }*/
        
        public boolean containsRefineNode(boolean[] tree) {
            ArrayList<String> str = new ArrayList<String>();
            for (boolean bool : tree) {
                str.add(String.valueOf(bool));
            }
            if (str.isEmpty())
            {
                return refines2.containsKey(Integer.toString(this.Tree)) && refines2.get(Integer.toString(this.Tree)).getNode() != null;
            }
            RefineTreeNode current = refines2.get(Integer.toString(this.Tree));
            for (boolean p : tree) {
                if (p) {
                    if (current.getLeftBranch() == null) {
                        return false;
                    }
                    current = current.getLeftBranch();
                }else{
                    if (current.getRightBranch() == null) {
                        return false;
                    }
                    current = current.getRightBranch();
                }
            }
            return current.getNode() != null;
        }
        
        /*public boolean containsRefineNode_old(boolean[] tree) {
            ArrayList<String> str = new ArrayList<String>();
            for (boolean bool : tree) {
                str.add(String.valueOf(bool));
            }
            if (str.isEmpty())
            {
                return refines.containsKey(this.Tree + ";");
            }
            return refines.containsKey(this.Tree + ";" + String.join(",", str));
        }*/
        
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
        
        public int getTree() {
            return this.Tree;
        }
        
//        public RefineNode getParentValidRefineNode(boolean[] tree) {
//                boolean[] newTree = Arrays.copyOfRange(tree, 0, tree.length-1);
//                while(newTree.length > 0 && newTree[newTree.length-1] == false)
//                {
//                    newTree = Arrays.copyOfRange(newTree, 0, newTree.length-1);
//                }
//        }
}
