package com.qualcomm.robotcore.eventloop.opmode;

public @interface Autonomous {
    String name() default "";
    String group() default "";
    String preselectTeleOp() default "";
}
