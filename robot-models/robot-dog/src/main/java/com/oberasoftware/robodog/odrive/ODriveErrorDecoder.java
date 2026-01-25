package com.oberasoftware.robodog.odrive;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ODriveErrorDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(ODriveErrorDecoder.class);

    private static final Map<Integer, String> AXIS_ERROR_MAP = new LinkedHashMap<>();
    private static final Map<Integer, String> MOTOR_ERROR_MAP = new LinkedHashMap<>();
    private static final Map<Integer, String> ENCODER_ERROR_MAP = new LinkedHashMap<>();

    static {
        // ---- Axis Errors ----
        AXIS_ERROR_MAP.put(0x00000001, "AXIS_ERROR_INVALID_STATE");
        AXIS_ERROR_MAP.put(0x00000002, "AXIS_ERROR_DC_BUS_UNDER_VOLTAGE");
        AXIS_ERROR_MAP.put(0x00000004, "AXIS_ERROR_DC_BUS_OVER_VOLTAGE");
        AXIS_ERROR_MAP.put(0x00000008, "AXIS_ERROR_DC_BUS_OVER_CURRENT");
        AXIS_ERROR_MAP.put(0x00000010, "AXIS_ERROR_MOTOR_OVER_TEMP");
        AXIS_ERROR_MAP.put(0x00000020, "AXIS_ERROR_INVERTER_OVER_TEMP");
        AXIS_ERROR_MAP.put(0x00000040, "AXIS_ERROR_DRIVER_FAULT");
        AXIS_ERROR_MAP.put(0x00000080, "AXIS_ERROR_WATCHDOG_TIMER_EXPIRED");
        AXIS_ERROR_MAP.put(0x00000100, "AXIS_ERROR_MIN_ENDSTOP_PRESSED");
        AXIS_ERROR_MAP.put(0x00000200, "AXIS_ERROR_MAX_ENDSTOP_PRESSED");
        AXIS_ERROR_MAP.put(0x00000400, "AXIS_ERROR_ESTOP_REQUESTED");
        AXIS_ERROR_MAP.put(0x00000800, "AXIS_ERROR_HOMING_WITHOUT_ENDSTOP");
        AXIS_ERROR_MAP.put(0x00001000, "AXIS_ERROR_OVER_SPEED");
        // add new axis errors if firmware defines them later

        // ---- Motor Errors ----
        MOTOR_ERROR_MAP.put(0x00000001, "MOTOR_ERROR_PHASE_RESISTANCE_OUT_OF_RANGE");
        MOTOR_ERROR_MAP.put(0x00000002, "MOTOR_ERROR_PHASE_INDUCTANCE_OUT_OF_RANGE");
        MOTOR_ERROR_MAP.put(0x00000004, "MOTOR_ERROR_ADC_FAILED");
        MOTOR_ERROR_MAP.put(0x00000008, "MOTOR_ERROR_DRV_FAULT");
        MOTOR_ERROR_MAP.put(0x00000010, "MOTOR_ERROR_CONTROL_DEADLINE_MISSED");
        MOTOR_ERROR_MAP.put(0x00000020, "MOTOR_ERROR_NOT_IMPLEMENTED_MOTOR_TYPE");
        MOTOR_ERROR_MAP.put(0x00000040, "MOTOR_ERROR_BRAKE_RESISTOR_DISARMED");
        MOTOR_ERROR_MAP.put(0x00000080, "MOTOR_ERROR_FETs_FAILED");
        MOTOR_ERROR_MAP.put(0x00000100, "MOTOR_ERROR_TIMER_OVERFLOW");
        MOTOR_ERROR_MAP.put(0x00000200, "MOTOR_ERROR_CURRENT_SENSE_SATURATION");
        MOTOR_ERROR_MAP.put(0x00000400, "MOTOR_ERROR_CURRENT_LIMIT_VIOLATION");
        MOTOR_ERROR_MAP.put(0x00000800, "MOTOR_ERROR_MODULATION_MAGNITUDE");
        MOTOR_ERROR_MAP.put(0x00001000, "MOTOR_ERROR_BRAKE_CURRENT_OUT_OF_RANGE");
        MOTOR_ERROR_MAP.put(0x00002000, "MOTOR_ERROR_UNEXPECTED_TIMER_CALLBACK");
        MOTOR_ERROR_MAP.put(0x00004000, "MOTOR_ERROR_CURRENT_MEASUREMENT_UNAVAILABLE");
        MOTOR_ERROR_MAP.put(0x00008000, "MOTOR_ERROR_CONTROLLER_FAILED");
        MOTOR_ERROR_MAP.put(0x00010000, "MOTOR_ERROR_I_BUS_OUT_OF_RANGE");
        MOTOR_ERROR_MAP.put(0x00020000, "MOTOR_ERROR_BRAKE_DUTY_CYCLE_NAN");
        MOTOR_ERROR_MAP.put(0x00040000, "MOTOR_ERROR_INVALID_CURRENT_COMMAND");
        MOTOR_ERROR_MAP.put(0x00080000, "MOTOR_ERROR_INVERTER_OVER_TEMP");
        MOTOR_ERROR_MAP.put(0x00100000, "MOTOR_ERROR_THERMISTOR_DISCONNECTED");
        MOTOR_ERROR_MAP.put(0x00200000, "MOTOR_ERROR_CALIBRATION_FAULT");

        // ---- Encoder Errors ----
        ENCODER_ERROR_MAP.put(0x00000001, "ENCODER_ERROR_UNSTABLE_GAIN");
        ENCODER_ERROR_MAP.put(0x00000002, "ENCODER_ERROR_CPR_POLEPAIRS_MISMATCH");
        ENCODER_ERROR_MAP.put(0x00000004, "ENCODER_ERROR_NO_RESPONSE");
        ENCODER_ERROR_MAP.put(0x00000008, "ENCODER_ERROR_UNSUPPORTED_ENCODER_MODE");
        ENCODER_ERROR_MAP.put(0x00000010, "ENCODER_ERROR_ILLEGAL_HALL_STATE");
        ENCODER_ERROR_MAP.put(0x00000020, "ENCODER_ERROR_INDEX_NOT_FOUND_YET");
        ENCODER_ERROR_MAP.put(0x00000040, "ENCODER_ERROR_ABS_SPI_TIMEOUT");
        ENCODER_ERROR_MAP.put(0x00000080, "ENCODER_ERROR_ABS_SPI_COM_FAIL");
        ENCODER_ERROR_MAP.put(0x00000100, "ENCODER_ERROR_ABS_SPI_NOT_READY");
        ENCODER_ERROR_MAP.put(0x00000200, "ENCODER_ERROR_HALL_NOT_CALIBRATED");
    }

    public static List<String> decodeAxisError(int errorCode) {
        return decode(errorCode, AXIS_ERROR_MAP);
    }

    public static List<String> decodeMotorError(int errorCode) {
        return decode(errorCode, MOTOR_ERROR_MAP);
    }

    public static List<String> decodeEncoderError(int errorCode) {
        return decode(errorCode, ENCODER_ERROR_MAP);
    }

    private static List<String> decode(int errorCode, Map<Integer, String> map) {
        List<String> errors = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if ((errorCode & entry.getKey()) != 0) {
                errors.add(entry.getValue());
            }
        }
        if (errors.isEmpty()) {
            errors.add("NO_ERROR");
        }
        return errors;
    }

    // Example usage
    public static String printErrors(int errorCode) {
        return String.format("Axis errors: %s Motor error: %s Encoder errors: %s", decodeAxisError(errorCode), decodeMotorError(errorCode), decodeEncoderError(errorCode));
    }
}
