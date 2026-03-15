package org.sciborgs1155.robot;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/* TO DO
- Link button to do stuff (after cad is finished)
*/

public class Intake extends SubsystemBase {
  // Booleans
  boolean isDown = false;
  boolean intakeMotorActivated = false;
  boolean isInAutonomous = false;

  // Constants
  double SlapDownMotorPower = 0.9;
  double IntakeMotorPower = 0.5;
  double DownPosition = 10; // *Note*: This is just a placeholder value that needs to be tested
  double UpPosition = 0;

  // Motor variables
  private final SparkMax Top_IntakeMotor =
      new SparkMax(
          1 /*CHANGE THIS LATER WITH THE ACTUAL PORT (Ports.Intake.Top_Intake)*/,
          MotorType.kBrushless);
  private final SparkMax Bottom_IntakeMotor = new SparkMax(2, MotorType.kBrushless);
  private final SparkMax SlapDown_Motor = new SparkMax(3, MotorType.kBrushless);
  /* when you want to begin outtaking (like if it gets jammed or something)
  reverse the motor powers to reverse the spin of the motors */
  private final RelativeEncoder slapDownEncoder =
      SlapDown_Motor
          .getEncoder(); // saves the distance that the slapdown motor spun as 'slapDownEncoder'

  public void beginIntakeMotor() {
    // The sign may vary based on design
    Top_IntakeMotor.set(IntakeMotorPower);
    Bottom_IntakeMotor.set(-IntakeMotorPower);
  }

  public void reverseIntakeMotor() {
    // The sign may vary based on design
    Top_IntakeMotor.set(-IntakeMotorPower);
    Bottom_IntakeMotor.set(IntakeMotorPower);
  }

  public void stopIntakeMotor() {
    Top_IntakeMotor.stopMotor();
    Bottom_IntakeMotor.stopMotor();
  }

  public void TriggerSlapdown() {
    if (isDown) {
      // if slap-down is down then gears will activate backwards

      SlapDown_Motor.getClosedLoopController().setReference(UpPosition, ControlType.kPosition);
      isDown = false;
    } else {
      // If the intake isn’t already down, then activate motors to turn it down.
      SlapDown_Motor.getClosedLoopController().setReference(DownPosition, ControlType.kPosition);
      isDown = true;
    }
  }

  public void
      TriggerIntake() { // togggle intake, if its activated, stop it, if its not activated, start it
    if (intakeMotorActivated) {
      stopIntakeMotor();
      intakeMotorActivated = false;
    } else {
      beginIntakeMotor();
      intakeMotorActivated = true;
    }
  }

  public Intake() {
    slapDownEncoder.setPosition(
        0); // sets the current position of the slapdown motor to 0, or fully up (like the starting
    // position)
    // doesnt actually move it just declares the current position as zero

    // PID stuff
    SparkMaxConfig config = new SparkMaxConfig();

    ClosedLoopConfig pid = config.closedLoop;
    pid.p(0.1);
    // Proportional: How strong the motor reacts to error (Error is the difference between
    // current and target position).
    // Higher P = fast response
    // Very high P = May cause shaking/oscillation (Basically, do not set it high)

    pid.i(0);
    /*  Integral: Controls how fast the motor corrects its position;
    This is because friction and gravity cause the motor to have trouble rotate to the correct orientation
    */

    // Examples:
    // 0 = no correction
    // Very low I = correction may fall short
    // High I = correction may overshoot/oscillate

    pid.d(0.01); // Derivative: Controls how fast the motor position changes. Slows down near target
    // position
    // Examples:
    // With D = More damping (decrease overtime)
    // High D = May have sluggish/slow movement
    // May need to change persistMode based on preference

    SlapDown_Motor.configure(config, null, null);
  }

  public boolean sensorGamePieceDetected() {
    return false;
    // placeholder, return true if the sensor detects a game piece
  }

  public Command autoIntakeSequence() {
    return Commands.sequence(
        Commands.runOnce(() -> beginIntakeMotor()), // starts the intake motor
        Commands.runOnce(() -> TriggerSlapdown()), // activates slapdown arm
        Commands.waitSeconds(
            0.5), // random value, making sure that the ball is fully intaken before lifitng arm
        // back up
        Commands.runOnce(() -> TriggerSlapdown()) // lifts the arm back up
        );
  }

  public void
      autoIntakeLoop() { // continuously checks if the sensor detects a game piece, then triggers
    // intake and slapdown
    if (sensorGamePieceDetected()) {
      autoIntakeSequence().schedule();
      /*
      when sensor detects game piece, this runs the autoIntakeSequence method above
       */
    }
  }

  public Command autoIntake() { // when this is eventually called (when the robot is in
    // autonomous),continuously loop the autoIntakeLoop method
    return run(this::autoIntakeLoop);
  }
}
