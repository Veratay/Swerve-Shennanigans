package com.qualcomm.robotcore.hardware;

public interface DcMotor extends DcMotorSimple
{
    /**
     * ZeroPowerBehavior provides an indication as to a motor's behavior when a power level of zero
     * is applied.
     * @see #setZeroPowerBehavior(ZeroPowerBehavior)
     * @see #setPower(double)
     */
    enum ZeroPowerBehavior
    {
        /** The behavior of the motor when zero power is applied is not currently known. This value
         * is mostly useful for your internal state variables. It may not be passed as a parameter
         * to {@link #setZeroPowerBehavior(ZeroPowerBehavior)} and will never be returned from
         * {@link #getZeroPowerBehavior()}*/
        UNKNOWN,
        /** The motor stops and then brakes, actively resisting any external force which attempts
         * to turn the motor. */
        BRAKE,
        /** The motor stops and then floats: an external force attempting to turn the motor is not
         * met with active resistence. */
        FLOAT
    }

    /**
     * Sets the behavior of the motor when a power level of zero is applied.
     * @param zeroPowerBehavior the new behavior of the motor when a power level of zero is applied.
     * @see ZeroPowerBehavior
     * @see #setPower(double)
     */
    void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior);

    /**
     * Returns the current behavior of the motor were a power level of zero to be applied.
     * @return the current behavior of the motor were a power level of zero to be applied.
     */
    ZeroPowerBehavior getZeroPowerBehavior();

    /**
     * Sets the desired encoder target position to which the motor should advance or retreat
     * and then actively hold thereat. This behavior is similar to the operation of a servo.
     * The maximum speed at which this advance or retreat occurs is governed by the power level
     * currently set on the motor. While the motor is advancing or retreating to the desired
     * taget position, {@link #isBusy()} will return true.
     *
     * <p>Note that adjustment to a target position is only effective when the motor is in
     * {@link RunMode#RUN_TO_POSITION RUN_TO_POSITION}
     * RunMode. Note further that, clearly, the motor must be equipped with an encoder in order
     * for this mode to function properly.</p>
     *
     * @param position the desired encoder target position
     * @see #getCurrentPosition()
     * @see #setMode(RunMode)
     * @see RunMode#RUN_TO_POSITION
     * @see #getTargetPosition()
     * @see #isBusy()
     */
    void setTargetPosition(int position);

    /**
     * Returns the current target encoder position for this motor.
     * @return the current target encoder position for this motor.
     * @see #setTargetPosition(int)
     */
    int getTargetPosition();

    /**
     * Returns true if the motor is currently advancing or retreating to a target position.
     * @return true if the motor is currently advancing or retreating to a target position.
     * @see #setTargetPosition(int)
     */
    boolean isBusy();

    /**
     * Returns the current reading of the encoder for this motor. The units for this reading,
     * that is, the number of ticks per revolution, are specific to the motor/encoder in question,
     * and thus are not specified here.
     * @return the current reading of the encoder for this motor
     * @see #getTargetPosition()
     * @see RunMode#STOP_AND_RESET_ENCODER
     */
    int getCurrentPosition();

    /**
     * The run mode of a motor {@link RunMode} controls how the motor interprets the
     * it's parameter settings passed through power- and encoder-related methods.
     * Some of these modes internally use <a href="https://en.wikipedia.org/wiki/PID_controller">PID</a>
     * control to achieve their function, while others do not. Those that do are referred
     * to as "PID modes".
     */
    enum RunMode
    {
        /** The motor is simply to run at whatever velocity is achieved by apply a particular
         * power level to the motor.
         */
        RUN_WITHOUT_ENCODER,

        /** The motor is to do its best to run at targeted velocity. An encoder must be affixed
         * to the motor in order to use this mode. This is a PID mode.
         */
        RUN_USING_ENCODER,

        /** The motor is to attempt to rotate in whatever direction is necessary to cause the
         * encoder reading to advance or retreat from its current setting to the setting which
         * has been provided through the {@link #setTargetPosition(int) setTargetPosition()} method.
         * An encoder must be affixed to this motor in order to use this mode. This is a PID mode.
         */
        RUN_TO_POSITION,

        /** The motor is to set the current encoder position to zero. In contrast to
         * {@link com.qualcomm.robotcore.hardware.DcMotor.RunMode#RUN_TO_POSITION RUN_TO_POSITION},
         * the motor is not rotated in order to achieve this; rather, the current rotational
         * position of the motor is simply reinterpreted as the new zero value. However, as
         * a side effect of placing a motor in this mode, power is removed from the motor, causing
         * it to stop, though it is unspecified whether the motor enters brake or float mode.
         *
         * Further, it should be noted that setting a motor to{@link RunMode#STOP_AND_RESET_ENCODER
         * STOP_AND_RESET_ENCODER} may or may not be a transient state: motors connected to some motor
         * controllers will remain in this mode until explicitly transitioned to a different one, while
         * motors connected to other motor controllers will automatically transition to a different
         * mode after the reset of the encoder is complete.
         */
        STOP_AND_RESET_ENCODER,

        /** @deprecated Use {@link #RUN_WITHOUT_ENCODER} instead */
        @Deprecated RUN_WITHOUT_ENCODERS,

        /** @deprecated Use {@link #RUN_USING_ENCODER} instead */
        @Deprecated RUN_USING_ENCODERS,

        /** @deprecated Use {@link #STOP_AND_RESET_ENCODER} instead */
        @Deprecated RESET_ENCODERS;

        /** Returns the new new constant corresponding to old constant names.
         * @deprecated Replace use of old constants with new */
        @Deprecated
        public RunMode migrate()
        {
            switch (this)
            {
                case RUN_WITHOUT_ENCODERS: return RUN_WITHOUT_ENCODER;
                case RUN_USING_ENCODERS: return RUN_USING_ENCODER;
                case RESET_ENCODERS: return STOP_AND_RESET_ENCODER;
                default: return this;
            }
        }

        /**
         * Returns whether this RunMode is a PID-controlled mode or not
         * @return whether this RunMode is a PID-controlled mode or not
         */
        public boolean isPIDMode()
        {
            return this==RUN_USING_ENCODER || this==RUN_USING_ENCODERS || this==RUN_TO_POSITION;
        }
    }

    /**
     * Sets the current run mode for this motor
     * @param mode the new current run mode for this motor
     * @see RunMode
     * @see #getMode()
     */
    void setMode(RunMode mode);

    /**
     * Returns the current run mode for this motor
     * @return the current run mode for this motor
     * @see RunMode
     * @see #setMode(RunMode)
     */
    RunMode getMode();
}
