package org.sciborgs1155.robot;


import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.SparkMaxConfig;


import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


/* TO DO
- Auto intake system for autonomous phase (sensors detect game piece then activate the intake)
- Link button to do stuff (after cad is finished)
- 
*/


public class Intake extends SubsystemBase {
    // Booleans
    static boolean isDown = false;
    static boolean intakeMotorActivated = false;
    static boolean isInAutonomous = false;


    // Constants
    static double SlapDownMotorPower = 0.9;
    static double IntakeMotorPower = 0.5;
    static double DownPosition = 10; // *Note*: This is just a placeholder value that needs to be tested
    static double UpPosition = 0;
//
    // Motor variables
    private final static SparkMax Top_IntakeMotor = new SparkMax(Ports.Intake.TOP_INTAKE, MotorType.kBrushless);
    private final static SparkMax Bottom_IntakeMotor = new SparkMax(Ports.Intake.BOTTOM_INTAKE, MotorType.kBrushless);
    private final static SparkMax SlapDown_Motor = new SparkMax(Ports.Intake.SLAPDOWN, MotorType.kBrushless);
    /* when you want to begin outtaking (like if it gets jammed or something)
     reverse the motor powers to reverse the spin of the motors */
   
    public static void beginIntakeMotor() {
        // The sign may vary based on design
        Top_IntakeMotor.set(IntakeMotorPower);
        Bottom_IntakeMotor.set(-IntakeMotorPower);
    }


    public static void reverseIntakeMotor() {
        // The sign may vary based on design
        Top_IntakeMotor.set(-IntakeMotorPower);
        Bottom_IntakeMotor.set(IntakeMotorPower);
    }


    public static void stopIntakeMotor() {
        Top_IntakeMotor.stopMotor();
        Bottom_IntakeMotor.stopMotor();
    }


    public static void TriggerSlapdown() {
        if (isDown) {
            // if slap-down is down then gears will activate backwards
           
            SlapDown_Motor.getClosedLoopController()
                .setReference(UpPosition, ControlType.kPosition);


            isDown = false;
        } else {
            // If the intake isn’t already down, then activate motors to turn it down.


            SlapDown_Motor.getClosedLoopController()
                .setReference(DownPosition, ControlType.kPosition);


            isDown = true;
        }
    }


    public static void TriggerIntake() { //togggle intake, if its
         if (intakeMotorActivated) {
            stopIntakeMotor();
         } else {
            beginIntakeMotor();
         }
    }


    public Intake() {
        // PID stuff
        SparkMaxConfig config = new SparkMaxConfig();


        ClosedLoopConfig pid = config.closedLoop;
        pid.p(0.1); // Proportional: How strong the motor reacts to error (Error is the difference between current and target position).
                    // Higher P = fast response
                    // Very high P = May cause shaking/oscillation (Basically, do not set it high)
       
        pid.i(0); /*  Integral: Controls how fast the motor corrects its position;
                        This is because friction and gravity cause the motor to have trouble rotate to the correct orientation
                    */

                    // Examples:
                    // 0 = no correction
                    // Very low I = correction may fall short
                    // High I = correction may overshoot/oscillate

        pid.d(0.01); //Derivative: Controls how fast the motor position changes. Slows down near target position

                    // Examples:
                    // With D = More damping (decrease overtime)
                    // High D = May have sluggish/slow movement

        // May need to change persistMode based on preference
        SlapDown_Motor.configure(config, null, null);
    };
}