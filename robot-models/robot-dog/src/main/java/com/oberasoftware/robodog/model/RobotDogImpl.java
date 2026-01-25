package com.oberasoftware.robodog.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RobotDogImpl implements RobotDog {
    private final Map<String, Leg> legs;
    private final Gait gait = new TrotGait();
    private volatile boolean interrupted = false;

    public RobotDogImpl(List<Leg> legs) {
        this.legs = legs.stream().collect(Collectors.toMap(Leg::getName, l -> l));
    }

    @Override
    public void moveBody(Posture posture) {
        double height = switch (posture) {
            case UP -> 0.20; // 20cm
            case DOWN -> 0.10; // 10cm
            case WALK -> 0.15; // 15cm
        };

        legs.values().forEach(l -> l.moveFootTo(new Vector3D(0, 0, height)));
    }

    @Override
    public void walk(double x, double y) {
        interrupted = false;
        double currentX = 0;
        double currentY = 0;
        double stepLength = 0.05; // 5cm per step
        double totalDistance = Math.sqrt(x * x + y * y);
        Vector3D direction = new Vector3D(x / totalDistance * stepLength, y / totalDistance * stepLength, 0);

        while ((Math.abs(currentX) < Math.abs(x) || Math.abs(currentY) < Math.abs(y)) && !interrupted) {
            for (double p = 0; p <= 1.0 && !interrupted; p += 0.05) {
                gait.step(legs, direction, 0.05, 0.15, p);
                sleep(20);
            }
            currentX += direction.getX();
            currentY += direction.getY();
        }

        if (interrupted) {
            stop();
        }
    }

    @Override
    public void stop() {
        interrupted = true;
        moveBody(Posture.WALK);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            interrupted = true;
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public List<Leg> getLegs() {
        return Collections.unmodifiableList(List.copyOf(legs.values()));
    }

    @Override
    public Leg getLeg(String name) {
        return legs.get(name);
    }
}
