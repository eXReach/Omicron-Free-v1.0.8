package com.exreach.omicron.fwk;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    
    String permission() default "";
    
    String noPerm() default "§8[§6OMICRON§8] §7У вас нет прав на использование данной команды!";
    
    String[] aliases() default {};
    
    String description() default "";
    
    String usage() default "";
}
