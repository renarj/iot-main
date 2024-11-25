package com.oberasoftware.robot.nao;

/**
 * @author Renze de Vries
 */
@FunctionalInterface
public interface NaoTask {
    void run() throws Exception;
}
