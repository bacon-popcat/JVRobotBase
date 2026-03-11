public class Intake {
    static boolean isDown = false;
    static boolean intakeMotorActivated = false;
    static boolean isInAutonomous = false;
    static double SlapDownMotorPower = 0.9;
    static double IntakeMotorPower = 0.5;
    /* when you want to begin outtaking (like if it gets jammed or something)
     reverse the motor powers to reverse the spin of the motors */ 

    public static void beginIntakeMotor() {
        // The sign may vary based on design
        topIntakeMotor.setPower(IntakeMotorPower);
        bottomIntakeMotor.setPower(-IntakeMotorPower);
    }

    public static void beginOuttakeMotor() {
        // The sign may vary based on design
        topIntakeMotor.setPower(-IntakeMotorPower);
        bottomIntakeMotor.setPower(IntakeMotorPower);
    }

    public static void stopMotor() {
        topIntakeMotor.setPower(0.0);
        bottomIntakeMotor.setPower(0.0);
    }

    public static void TriggerSlapdown() {
        if (isDown) {
            // if slap-down is down then gears will activate backwards
            slapDownMotor.setPower(-SlapDownMotorPower);
            isDown = false;
        } else {
            // If the intake isn’t already down, then activate motors to turn it down.
            slapDownMotor.setPower(SlapDownMotorPower);
            isDown = true;
        } 
    }

    public static void TriggerOutTake() {
         if (intakeMotorActivated) {
            stopMotor();
         } else {
            beginOuttakeMotor();
         }
    }
}

