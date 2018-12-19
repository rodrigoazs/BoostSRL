/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.Transfer;

import edu.wisc.cs.will.FOPC.Term;
import edu.wisc.cs.will.ILP.SingleClauseNode;
import edu.wisc.cs.will.Refine.RefineNode;
import edu.wisc.cs.will.Utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.paukov.combinatorics.Factory;
//import org.paukov.combinatorics.Generator;
//import org.paukov.combinatorics.ICombinatoricsVector;

/**
 *
 * @author Rodrigo Azevedo
 */
public class Transfer {
        //HashMap<String, String> refines = null;
        //int Tree = 0;
        boolean searchArgPermutation = true;
        boolean searchEmpty = true;
        boolean allowSameTargetMap = true;
        HashMap<String, ArrayList<String>> source = null;
        HashMap<String, ArrayList<String>> target = null;
        HashMap<String, Mapping> predsMapping = null;
        HashMap<String, String> typeConstraints = null;
        HashSet<String> targetMapped = null;
        String targetHead = null;
        
        public Transfer() {
            source = new HashMap<String, ArrayList<String>>();
            target = new HashMap<String, ArrayList<String>>();
            predsMapping = new HashMap<String, Mapping>();
            typeConstraints = new HashMap<String, String>();
            targetMapped = new HashSet<String>();
        }
        
        public void readLine(String line) {
            String str = line.replaceAll("\\s+", "");
            Pattern paramPattern = Pattern.compile("^setParam:(\\w*)=(\\w*)\\.$");
            Pattern predPattern = Pattern.compile("^(source|target):(\\w*)\\(([\\w,]*)\\)\\.$");
            Pattern mapPattern = Pattern.compile("^setMap:(\\w*)\\(([\\w,]*)\\)=(\\w*)\\(([\\w,]*)\\)\\.$");
            Matcher paramMatcher = paramPattern.matcher(str);
            Matcher predMatcher = predPattern.matcher(str);
            Matcher mapMatcher = mapPattern.matcher(str);
            
            if (paramMatcher.find() == true) {
                if (paramMatcher.group(1).equals("searchArgPermutation")) {
                    searchArgPermutation = Boolean.parseBoolean(paramMatcher.group(2));
                }
                else if (paramMatcher.group(1).equals("searchEmpty")) {
                    searchEmpty = Boolean.parseBoolean(paramMatcher.group(2));
                }
                else if (paramMatcher.group(1).equals("allowSameTargetMap")) {
                    allowSameTargetMap = Boolean.parseBoolean(paramMatcher.group(2));
                }
            }else if (predMatcher.find() == true) {
                if (predMatcher.group(1).equals("source")) {
                    source.put(predMatcher.group(2), new ArrayList<String>(Arrays.asList(predMatcher.group(3).split(","))));
                }
                else if (predMatcher.group(1).equals("target")) {
                    target.put(predMatcher.group(2), new ArrayList<String>(Arrays.asList(predMatcher.group(3).split(","))));
                }
            }else if (mapMatcher.find() == true) {
                String srcPred = mapMatcher.group(1);
                String tarPred = mapMatcher.group(3);
                ArrayList<String> srcArgs = new ArrayList<String>(Arrays.asList(mapMatcher.group(2).split(",")));
                ArrayList<String> tarArgs = new ArrayList<String>(Arrays.asList(mapMatcher.group(4).split(",")));
                // Check if srcArgs respects alphabetic order
                if (!compareArgs(srcArgs, generateVariables(srcArgs.size()))) {
                    Utils.println("\nSource predicate '" + srcPred + "' has to respect alphabetic order in its variables.");
                    //Utils.reportStackTrace(e);
                    Utils.error("Unable to successfully define transfer from source predicate '" + srcPred + "' to target predicate '" + tarPred + "'.");
                }
                // Check same arity
                if (sameArity(srcArgs, tarArgs)) {
                    // Check if predicate was defined
                    if (!source.containsKey(srcPred)) {
                        Utils.println("\nDefinition for source predicate '" + srcPred + "' not found.");
                        //Utils.reportStackTrace(e);
                        Utils.error("Unable to successfully define transfer from source predicate '" + srcPred + "' to target predicate '" + tarPred + "'.");
                    }
                    if (!target.containsKey(tarPred)) {
                        Utils.println("\nDefinition for target predicate '" + tarPred + "' not found.");
                        //Utils.reportStackTrace(e);
                        Utils.error("Unable to successfully define transfer from source predicate '" + srcPred + "' to target predicate '" + tarPred + "'.");
                    }
                    ArrayList<String> srcArgTypes = source.get(srcPred);
                    ArrayList<String> tarArgTypes = target.get(tarPred);
                    // Check if it is compatible
                    HashMap<String, String> isComp = isCompatible(srcArgTypes, transferVariables(tarArgTypes, tarArgs), typeConstraints);
                    if (isComp == null) {
                        Utils.println("\nTransfer defined for source predicate '" + srcPred + "' to target predicate '" + tarPred + "' is not compatible.");
                        //Utils.reportStackTrace(e);
                        Utils.error("Unable to successfully define transfer from source predicate '" + srcPred + "' to target predicate '" + tarPred + "'.");
                    }else{
                        typeConstraints = isComp;
                        predsMapping.put(srcPred, new Mapping(tarPred, tarArgs));
                        targetMapped.add(tarPred);
                        if (targetHead == null) {
                            targetHead = tarPred;
                        }
                    }
                }else{
                    Utils.println("\nTransfer defined for source predicate '" + srcPred + "' to target predicate '" + tarPred + "' denied due to different arity.");
                    //Utils.reportStackTrace(e);
                    Utils.error("Unable to successfully define transfer from source predicate '" + srcPred + "' to target predicate '" + tarPred + "'.");
                }
            }else if(!str.startsWith("//") && !str.isEmpty()) {
                Utils.println("\nEncountered an exception during parsing '" + str + "' of transfer file:");
                //Utils.reportStackTrace(e);
                Utils.error("Unable to successfully parse transfer file: " + str + ".");
            }
        }
        
        public Object[] transferHead(Object[] head) {
            String predicate = (String) head[0];
            ArrayList<String> vars = new ArrayList<String>(Arrays.asList((String[])head[1]));
            ArrayList<String> toMap = predsMapping.get(predicate).getTargetArguments();
            ArrayList<String> args = transferVariables(vars, toMap);
            return new Object[] {targetHead, args.toArray(new String[args.size()])};
        }
        
        public ArrayList<Object[]> transferBody(ArrayList<Object[]> body) {
            ArrayList<Object[]> transferredBody = new ArrayList<Object[]>();
            ArrayList<String> vars;
            ArrayList<String> toMap;
            ArrayList<String> args;
            int size = body.size();
            for (int i = 0; i < size; i++) {
                Mapping mapped = predsMapping.get((String) body.get(i)[0]);
                if (mapped != null)
                {
                    vars = new ArrayList<String>(Arrays.asList((String[])body.get(i)[1]));
                    toMap = mapped.getTargetArguments();
                    args = transferVariables(vars, toMap);
                    transferredBody.add(new Object[] {mapped.getTargetPredicate(), args.toArray(new String[args.size()])});
                }
            }
            return transferredBody;
        }
        
        public ArrayList<Object[]> transferBodyGivenMapping(ArrayList<Object[]> body, Object[] mapping) {
            ArrayList<Object[]> transferredBody = new ArrayList<Object[]>();
            ArrayList<String> vars;
            ArrayList<String> toMap;
            ArrayList<String> args;
            HashMap<String, Mapping> map = (HashMap<String, Mapping>) mapping[0];
            int size = body.size();
            for (int i = 0; i < size; i++) {
                Mapping mapped = map.get((String) body.get(i)[0]);
                if (mapped != null)
                {
                    vars = new ArrayList<String>(Arrays.asList((String[])body.get(i)[1]));
                    toMap = mapped.getTargetArguments();
                    args = transferVariables(vars, toMap);
                    transferredBody.add(new Object[] {mapped.getTargetPredicate(), args.toArray(new String[args.size()])});
                }
            }
            return transferredBody;
        }
        
        private HashMap<String, String> isCompatible(ArrayList<String> srcArgs, ArrayList<String> tarArgs, HashMap<String, String> typeCst) {
            if (sameArity(srcArgs, tarArgs)) {
                HashMap<String, String> typeConstraints = cloneTypeConstraints(typeCst);
                for (int i=0; i < srcArgs.size(); i++) {
                    if (typeConstraints.containsKey(srcArgs.get(i)) == true) {
                        if (!typeConstraints.get(srcArgs.get(i)).equals(tarArgs.get(i))) {
                            return null;
                        }
                    } else {
                        typeConstraints.put(srcArgs.get(i), tarArgs.get(i));
                    }
                }
                return typeConstraints;
            } else {
                return null;
            }
        }
        
        private boolean sameArity(ArrayList<String> srcArgs, ArrayList<String> tarArgs) {
            return srcArgs.size() == tarArgs.size();
        }
           
        private ArrayList<String> transferVariables(ArrayList<String> variables, ArrayList<String> toMap) {
            HashMap<String, String> read = new HashMap<String, String>();
            ArrayList<String> ret = new ArrayList<String>();
            ArrayList<String> fromMap = generateVariables(variables.size());

            for (int i=0; i < variables.size(); i++) {
                read.put(fromMap.get(i), variables.get(i));
            }
            for (int i=0; i < toMap.size(); i++) {
                ret.add(read.get(toMap.get(i)));
            }
            return ret;
        }
        
        public ArrayList<Object[]> legalMappings(ArrayList<String> srcPreds) {
            return legalMappingsRecursive(srcPreds, predsMapping, typeConstraints, targetMapped, 0);
        }
        
        private ArrayList<Object[]> legalMappingsRecursive(ArrayList<String> srcPreds, HashMap<String, Mapping> predsMap, HashMap<String, String> typeCst, HashSet<String> targetMpd, int i) {
            if (i >= srcPreds.size()) {
                ArrayList<Object[]> temp = new ArrayList<Object[]>();
                temp.add(new Object[] { predsMap, typeCst, targetMpd });
                return temp;
            }else{
                String srcPred = srcPreds.get(i);
                ArrayList<Object[]> ret = new ArrayList<Object[]>();
                
                // if search for empty mapping is allowed
                if (searchEmpty) {
                    HashMap<String, Mapping> newPredsMap = clonePredMappings(predsMap);
                    newPredsMap.put(srcPred, null);
                    HashMap<String, String> newTypeCst = cloneTypeConstraints(typeCst);
                    HashSet<String> newTargetHd = cloneTargetMapped(targetMpd);
                    ret.addAll(legalMappingsRecursive(srcPreds, newPredsMap, newTypeCst, newTargetHd, i+1));
                }
                boolean found = false;
                for (String tarPred : target.keySet()) {
                    if (!tarPred.equals(targetHead) && (allowSameTargetMap || !targetMpd.contains(tarPred))) {
                        ArrayList<ArrayList<String>> permutations = new ArrayList<ArrayList<String>>();
                    
                        if (searchArgPermutation) {
                            /*ICombinatoricsVector<String> vector = Factory.createVector(generateVariables(target.get(tarPred).size()));
                            Generator<String> gen = Factory.createPermutationGenerator(vector);
                            for (ICombinatoricsVector<String> perm : gen) {
                                permutations.add(new ArrayList<String>(perm.getVector()));
                            }*/
                            permutations = permutations(generateVariables(target.get(tarPred).size()));
                        }else{
                            permutations.add(generateVariables(target.get(tarPred).size()));
                        }

                        for (ArrayList<String> permutation: permutations) {
                            HashMap<String, String> isComp = isCompatible(source.get(srcPred), transferVariables(target.get(tarPred), permutation), typeCst);
                            if (isComp != null) {
                                found = true;
                                HashMap<String, Mapping> newPredsMap = clonePredMappings(predsMap);
                                newPredsMap.put(srcPred, new Mapping(tarPred, permutation));
                                HashMap<String, String> newTypeCst = isComp;
                                HashSet<String> newTargetMpd = cloneTargetMapped(targetMpd);
                                newTargetMpd.add(tarPred);
                                ret.addAll(legalMappingsRecursive(srcPreds, newPredsMap, newTypeCst, newTargetMpd, i+1));
                            }
                        }
                    }
                }
                // does not allow empty mapping but no compatible mapping was found
                if (!searchEmpty && !found)
                {
                    HashMap<String, Mapping> newPredsMap = clonePredMappings(predsMap);
                    newPredsMap.put(srcPred, null);
                    HashMap<String, String> newTypeCst = cloneTypeConstraints(typeCst);
                    HashSet<String> newTargetHd = cloneTargetMapped(targetMpd);
                    ret.addAll(legalMappingsRecursive(srcPreds, newPredsMap, newTypeCst, newTargetHd, i+1));
                }
                return ret;
            }
        }
        
        public ArrayList<String> predicatesUnmapped(ArrayList<Object[]> body) {
            int size = body.size();
            HashSet<String> preds = new HashSet<String>();
            for (int i = 0; i < size; i++) {
                String pred = (String) body.get(i)[0];
                if (!predsMapping.containsKey(pred)) {
                    preds.add(pred);
                }
            }
            return new ArrayList<String>(preds);
        }
        
        private ArrayList<String> generateVariables(int size) {
            ArrayList<String> vars = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                vars.add(Character.toString((char)(i+65)));
            }
            return vars;
        }
        
        private boolean compareArgs(ArrayList<String> a, ArrayList<String> b) {
            if (a.size() != b.size()) {
                return false;
            }
            for (int i=0; i< a.size(); i++) {
                if (!a.get(i).equals(b.get(i))) {
                    return false;
                }
            }
            return true;
        }
        
        public void setMapping(Object[] obj) {
            predsMapping = (HashMap<String, Mapping>) obj[0];
            typeConstraints = (HashMap<String, String>) obj[1];
            targetMapped = (HashSet<String>) obj[2];
        }
        
        public void addNullMapping(ArrayList<String> preds)
        {
            for (String pred: preds){
                predsMapping.put(pred, null);
                targetMapped.add(pred);
            }
        }
        
        public void promoteNode(RefineNode node) {
            if (node.getLeftNode() == null)
            {
                RefineNode right = node.getRightNode();
                if (right == null){
                    node.setNode(null);
                }else{
                    node.setNode(right.getNode());
                    node.setLeftBranch(right.getLeftBranch());
                    node.setRightBranch(right.getRightBranch());
                    node.setLeftNode(right.getLeftNode());
                    node.setRightNode(right.getRightNode());
                }
            }else{
                RefineNode right = node.getRightNode();
                RefineNode left = node.getLeftNode();
                node.setNode(left.getNode());
                node.setLeftBranch(left.getLeftBranch());
                node.setRightBranch(left.getRightBranch());
                node.setLeftNode(left.getLeftNode());
                node.setRightNode(left.getRightNode());
                if (right != null) {
                    sendNodeToFalse(node, right);
                }
            }
        }
        
        private void sendNodeToFalse(RefineNode node, RefineNode send)
        {
            RefineNode current = node;
            while (current.getRightNode() != null)
            {
                current = current.getRightNode();
            }
            current.setRightNode(send);
            current.setRightBranch(true);
        }
        
        private HashMap<String, String> cloneTypeConstraints(HashMap<String, String> t) {
            HashMap<String, String> ret = new HashMap<String, String>();
            for (String key : t.keySet()) {
                ret.put(key, t.get(key));
            }
            return ret;
        }
        
        private HashMap<String, Mapping> clonePredMappings(HashMap<String, Mapping> t) {
            HashMap<String, Mapping> ret = new HashMap<String, Mapping>();
            for (String key : t.keySet()) {
                if (t.get(key) == null) {
                    ret.put(key, null);
                }else{
                    ArrayList<String> vars = new ArrayList<String>();
                    for (String var : t.get(key).getTargetArguments()) {
                        vars.add(var);
                    }
                    ret.put(key, new Mapping(t.get(key).getTargetPredicate(), vars));
                }
            }
            return ret;
        }
       
        private HashSet<String> cloneTargetMapped(HashSet<String> t) {
            HashSet<String> ret = new HashSet<String>();
            for (String str : t) {
                ret.add(str);
            }
            return ret;
	}
        
        private ArrayList<ArrayList<String>> permutations(ArrayList<String> vector){
            return permute(vector.toArray(new String[vector.size()]), 0, vector.size()-1);
        } 
        
        private ArrayList<ArrayList<String>> permute(String[] str, int l, int r) 
        { 
            if (l == r){
                ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
                temp.add(new ArrayList<String>(Arrays.asList(str)));
                return temp;
            }else
            { 
                ArrayList<ArrayList<String>>  temp = new ArrayList<ArrayList<String>>();
                for (int i = l; i <= r; i++) 
                { 
                    str = swap(str,l,i); 
                    ArrayList<ArrayList<String>> t = permute(str, l+1, r);
                    temp.addAll(t);
                    str = swap(str,l,i);
                }
                return temp;
            } 
        }
        
        public static String[] swap(String[] a, int i, int j) 
        { 
            String temp; 
            //char[] charArray = a.toCharArray(); 
            temp = a[i] ; 
            a[i] = a[j]; 
            a[j] = temp; 
            return a; 
        }
}