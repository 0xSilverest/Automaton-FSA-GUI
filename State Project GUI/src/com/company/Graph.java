package com.company;

import java.util.*;

public class Graph {

    private LinkedList<String> acceptance;
    private LinkedList<String> initial;

    private Map<String, Map<Character, LinkedList<String>>> transitions;

    public Graph(){
        this.transitions = new HashMap<String, Map<Character, LinkedList<String>>>();
        this.acceptance = new LinkedList<>();
        this.initial = new LinkedList<String>();
    }
    public void addTransition(String src, String dest, Character weight){
        if(!transitions.containsKey(src)) {
            transitions.put(src, new HashMap<Character, LinkedList<String>>());
        }
        if(!this.transitions.get(src).containsKey(weight)){
            transitions.get(src).put(weight, new LinkedList<String>());
        }
        transitions.get(src).get(weight).add(dest);
    }

    public void addInitial(String dest){
        initial.add(dest);
    }

    public void addAcceptance(String src){
        acceptance.add(src);
    }

    public Map<String, Map<Character, LinkedList<String>>> getGraph(){
        return this.transitions;
    }

    public Boolean isInitEmpty(){
        if(this.initial.isEmpty()){
            return true;
        }
        return false;
    }

    public Boolean isAcceptEmpty(){
        if(this.acceptance.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(String src :initial){
            builder.append( "\nS -> " + src + " ;\n");
        }
        for(String src : transitions.keySet()) {
            for(Character weight : transitions.get(src).keySet()) {
                for(String dest : transitions.get(src).get(weight)){
                    builder.append(src + " -> " + dest + " [label = \"" + weight + "\"];\n");
                }
            }
        }
        for(String src : acceptance){
            builder.append(src  + " -> E;\n");
        }
        return (builder.toString());
    }

    public String Output(){
        StringBuilder builder = new StringBuilder();

        builder.append("digraph {\n\trankdir=LR;\n\tS [style = invis ];\n\tnode [shape = doublecircle];");
        for(String acc : acceptance){
            builder.append(" " + acc);
        }
        builder.append(";\n\tnode [shape = circle];\n");
        for(String init : initial){
            builder.append( "\tS -> " + init + " ;\n");
        }
        for(String src : transitions.keySet()) {
            for(Character weight : transitions.get(src).keySet()) {
                for(String dest : transitions.get(src).get(weight)){
                    builder.append(src + " -> " + dest + " [label = \"" + weight + "\"];\n");
                }
            }
        }
        builder.append("\t}");
        return (builder.toString());
    }

    public LinkedList<String> merge(LinkedList<String> first, LinkedList<String> Second){
        for(String s : Second){
            if(!first.contains(s))
            first.add(s);
        }
        return first;
    }

    private LinkedList<String> getClosureTable(String src){
        LinkedList<String> e_closure = new LinkedList<>();
        if(transitions.containsKey(src) && transitions.get(src).containsKey('0')) {
            for (String dest : transitions.get(src).get('0')) {
                e_closure.add(dest);
                if(transitions.get(dest).containsKey('0')){
                    e_closure = merge(e_closure, getClosureTable(dest));
                }
            }
        }
        return e_closure;
    }

    public void e_NFAtoNFA(){
        LinkedList<LinkedList<String>> e_closure = new LinkedList<>();
        for(String i : transitions.keySet()){
            LinkedList<String> clos = new LinkedList<>();
            if(transitions.get(i).containsKey('0')){
                for (String k : transitions.get(i).get('0')) {
                    clos.add(k);
                    if (transitions.keySet().contains(k)) {
                        merge(clos, getClosureTable(k));
                    }
                }
            }
            e_closure.add(clos);
        }

        int i = 0;

        for(String src : transitions.keySet()){
            for(String dest : e_closure.get(i)){
                if(transitions.containsKey(dest)){
                    for(char weight : transitions.get(dest).keySet()){
                        if(!transitions.get(src).containsKey(weight)){
                            transitions.get(src).put(weight, new LinkedList<String>());
                        }
                        for(String newDest : transitions.get(dest).get(weight)) {
                            transitions.get(src).get(weight).add(newDest);
                        }
                    }
                }
                if(acceptance.contains(dest) && !acceptance.contains(src) || acceptance.contains(src)){
                    acceptance.add(src);
                }
            }
            i++;
        }
        this.unreachedNodeRemoval();
    }

    public void unreachedNodeRemoval(){
        LinkedList<String> Checker = new LinkedList<>();
        LinkedList<String> Remover = new LinkedList<>();
        LinkedList<String> foundAcc = new LinkedList<>();
        for(String initState : initial){
            Checker.add(initState);
        }
        for(String src : transitions.keySet()) {
            if(Checker.contains(src)) {
                if (acceptance.contains(src) && !foundAcc.contains(src)) {
                    foundAcc.add(src);
                }
                for (char weight : transitions.get(src).keySet()) {
                    if (weight != '0') {
                        for (String dest : transitions.get(src).get(weight)) {
                            Checker.add(dest);
                            if (acceptance.contains(dest) && !foundAcc.contains(dest)) {
                                foundAcc.add(dest);
                            }
                        }
                    }
                }
            }
            if(transitions.get(src).containsKey('0')){
                transitions.get(src).remove('0');
            }
        }

        for(String src : transitions.keySet()) {
            if(Checker.contains(src)) {
                if (acceptance.contains(src) && !foundAcc.contains(src)) {
                    foundAcc.add(src);
                }
                for (char weight : transitions.get(src).keySet()) {
                    if (weight != '0') {
                        for (String dest : transitions.get(src).get(weight)) {
                            Checker.add(dest);
                            if (acceptance.contains(dest) && !foundAcc.contains(dest)) {
                                foundAcc.add(dest);
                            }
                        }
                    }
                }
            }
            if(transitions.get(src).containsKey('0')){
                transitions.get(src).remove('0');
            }
        }

        for(String state : transitions.keySet()) {
            if(!Checker.contains(state)){
                Remover.add(state);
            }
            for(char weight : transitions.get(state).keySet()){
                for(String dest : transitions.get(state).get(weight)){
                    if(!Checker.contains(state) && !Remover.contains(state)){
                        Remover.add(dest);
                    }
                }
            }
        }

        for(String src : Remover){
            transitions.remove(src);
            for(String state : transitions.keySet()){
                for(char weight : transitions.get(state).keySet()){
                    for(String dest : transitions.get(state).get(weight)){
                        if(transitions.get(state).get(weight).contains(src)){
                            transitions.get(state).get(weight).remove(src);
                        }
                    }
                }
            }
        }

        acceptance = foundAcc;
    }

    public String getIndex(LinkedList<LinkedList<String>> list, LinkedList<String> find){
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(find)){
                return Integer.toString(i);
            }
        }
        return "-1";
    }

    public void NFAtoDFA(){
        LinkedList<LinkedList<String>> Verified = new LinkedList<>();
        Queue<LinkedList<String>> Unverified = new LinkedList<>();
        Map<LinkedList<String> , Map<Character, LinkedList<String>>> DFA_table = new HashMap<>();
        LinkedList<LinkedList<String>>  Remover = new LinkedList<>();
        Unverified.add(initial);
        while(!Unverified.isEmpty()) {
            LinkedList<String> Current = Unverified.remove();
            Collections.sort(Current);
            for (String state : Current) {
                Map<Character, LinkedList<String>> temp = new HashMap<>();
                if(transitions.containsKey(state)) {
                    for (Character weight : transitions.get(state).keySet()) {
                        if (!temp.containsKey(weight)) {
                            temp.put(weight, new LinkedList<>());
                        }
                        for (String dest : transitions.get(state).get(weight)) {
                            temp.get(weight).add(dest);
                        }
                        Collections.sort(temp.get(weight));
                        if(!DFA_table.containsKey(Current)){
                            DFA_table.put(Current, new LinkedHashMap<>());
                        }
                        if(DFA_table.get(Current).isEmpty()){
                            DFA_table.put(Current, temp);
                        }
                        if(!temp.get(weight).isEmpty()
                                    && transitions.containsKey(state)
                                    && transitions.get(state).containsKey(weight)) {
                            Remover.add(transitions.get(state).get(weight));
                            if(!DFA_table.get(Current).containsKey(weight)){
                                DFA_table.get(Current).put(weight, new LinkedList<>());
                            }
                            for (String dest : transitions.get(state).get(weight)) {
                                if(!DFA_table.get(Current).get(weight).contains(dest)) {
                                    DFA_table.get(Current).get(weight).add(dest);
                                }
                            }
                        }
                        Collections.sort(DFA_table.get(Current).get(weight));
                        if (!Verified.contains(temp.get(weight)) && !Unverified.contains(temp.get(weight))){
                                Unverified.add(temp.get(weight));
                        }
                        if(DFA_table.containsKey(Current)) {
                            if (!Verified.contains(DFA_table.get(Current).get(weight))
                                    && !Unverified.contains(DFA_table.get(Current).get(weight))) {
                                Unverified.add(DFA_table.get(Current).get(weight));
                            }
                        }
                    }
                }

                if(!Verified.contains(Current))
                    Verified.add(Current);
            }

            if(!Remover.isEmpty()){
                if(DFA_table.containsKey(Current)) {
                    for (Character weight : DFA_table.get(Current).keySet()) {
                        for (LinkedList rm : Remover) {
                            if (DFA_table.get(Current).get(weight).contains(rm)) {
                                DFA_table.get(Current).get(weight).remove(rm);
                                if(Verified.contains(rm) && !Unverified.contains(rm)){
                                    Verified.remove(rm);
                                }
                            }
                        }
                    }
                }
            }
            Remover.clear();
        }

        Map<String, Map<Character, LinkedList<String>>> DFA = new HashMap<>();
        LinkedList<String> newAcceptance = new LinkedList<>();

        for(LinkedList<String> oldState : DFA_table.keySet()){
            for(char weight : DFA_table.get(oldState).keySet()) {
                String oldId = getIndex(Verified, oldState);
                String newState = getIndex(Verified, DFA_table.get(oldState).get(weight));
                for(String src : oldState) {
                    if (acceptance.contains(src) && !newAcceptance.contains(oldId)) {
                        newAcceptance.add(oldId);
                    }
                }
                if (!DFA.containsKey(oldId)) {
                    DFA.put(oldId, new HashMap<>());
                }

                if (!DFA.get(oldId).containsKey(weight)) {
                    DFA.get(oldId).put(weight, new LinkedList<>());
                }

                DFA.get(oldId).get(weight).add(newState);

                for(String dest : DFA_table.get(oldState).get(weight)){
                    if(acceptance.contains(dest) && !newAcceptance.contains(newState)){
                        newAcceptance.add(newState);
                    }
                }
            }
        }

        this.acceptance = newAcceptance;
        this.initial.clear();
        this.initial.add("0");
        this.transitions = DFA;
        this.unreachedNodeRemoval();
    }

    void reverse(String s,Graph result,LinkedList<String> checked){
        if(!checked.contains(s)) {
            checked.add(s);
            if(transitions.containsKey(s)){
                for (char c : transitions.get(s).keySet()) {
                    for (String state : transitions.get(s).get(c)) {
                        result.addTransition(state, s, c);
                        reverse(state, result, checked);
                    }
                }
            }
        }
    }

    void revGraph(){
        Graph result=new Graph();
        LinkedList<String> Checked = new LinkedList<>();
        reverse("0",result, Checked);
        LinkedList<String> Temp = initial;
        initial = acceptance;
        acceptance = Temp;
        transitions = result.transitions;
    }

    void minimize(){
        this.revGraph();
        this.NFAtoDFA();
        this.revGraph();
        this.NFAtoDFA();
    }
}
