package com.lgc.solutiontool.git.entities;

import com.lgc.solutiontool.git.jgit.BranchType;

/**
 * Created by H185176 on 15.05.2017.
 */
public class Branch {
    private String branchName;

    private BranchType branchType;

    public Branch(String name, BranchType type){
        branchName = name;
        branchType = type;
    }

    public String getBranchName() {
        return branchName;
    }

    public BranchType getBranchType() {
        return branchType;
    }
}
