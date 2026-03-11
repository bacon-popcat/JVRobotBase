package org.sciborgs1155.robot;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
    // Booleans
    static boolean isDown = false;
    static boolean intakeMotorActivated = false;
    static boolean isInAutonomous = false;

    // Constants
    static double SlapDownMotorPower = 0.9;
    static double IntakeMotorPower = 0.5;
    static double DownPosition = 10; // *Note*: This is just a placeholder value that needs to be tested
    static double UpPosition = 10;

    // Motor variables
    private final static SparkMax Top_IntakeMotor = new SparkMax(Ports.Intake.TOP_INTAKE, MotorType.kBrushless);
    private final static SparkMax Bottom_IntakeMotor = new SparkMax(Ports.Intake.BOTTOM_INTAKE, MotorType.kBrushless);
    private final static SparkMax SlapDown_Motor = new SparkMax(Ports.Intake.SLAPDOWN, MotorType.kBrushless);
    private final RelativeEncoder SlapdownEnconder = SlapDown_Motor.getEncoder();
    /* when you want to begin outtaking (like if it gets jammed or something)
     reverse the motor powers to reverse the spin of the motors */ 

    public static void beginIntakeMotor() {
        // The sign may vary based on design
        Top_IntakeMotor.set(IntakeMotorPower);
        Bottom_IntakeMotor.set(-IntakeMotorPower);
    }

    public static void beginOuttakeMotor() {
        // The sign may vary based on design
        Top_IntakeMotor.set(-IntakeMotorPower);
        Bottom_IntakeMotor.set(IntakeMotorPower);
    }

    public static void stopMotor() {
        Top_IntakeMotor.set(0.0);
        Bottom_IntakeMotor.set(0.0);
    }

    public static void TriggerSlapdown() {
        if (isDown) {
            // if slap-down is down then gears will activate backwards
            SlapDown_Motor.set(-SlapDownMotorPower);

            // Need to add code here to detect when motor reaches fully up

            isDown = false;
        } else {
            // If the intake isn’t already down, then activate motors to turn it down.

            SlapDown_Motor.set(SlapDownMotorPower);

            // Need to add code here to detect when motor reaches fully down

            isDown = true;
        } 
    }

    public static void TriggerIntake() {
         if (intakeMotorActivated) {
            stopMotor();
         } else {
            beginOuttakeMotor();
         }
    }
}

