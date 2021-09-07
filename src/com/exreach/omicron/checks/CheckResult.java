package com.exreach.omicron.checks;

public class CheckResult
{
    private String name;
    private Boolean pf;
    
    public CheckResult(final String CheckName, final Boolean passed) {
        this.name = CheckName;
        this.pf = passed;
    }
    
    public boolean passed() {
        return this.pf;
    }
    
    public String getCheckName() {
        return this.name;
    }
}
