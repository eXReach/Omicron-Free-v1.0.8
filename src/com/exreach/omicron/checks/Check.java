package com.exreach.omicron.checks;

import com.exreach.omicron.logger.*;
import org.bukkit.event.*;

public abstract class Check
{
    public abstract String getEventCall();
    
    public abstract CheckResult performCheck(final User p0, final Event p1);
    
    public CheckResult performCheck(final User u) {
        return this.performCheck(u, null);
    }
}
