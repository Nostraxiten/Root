package com.nox.menu.gui.util;

public class AnimationUtil {
    public static float lerp(float start, float end, float delta) {
        return start + (end - start) * delta;
    }

    public static float smooth(float start, float end, float delta) {
        float t = delta * delta * (3.0f - 2.0f * delta);
        return start + (end - start) * t;
    }

    public static float easeOut(float start, float end, float delta) {
        float t = 1.0f - (1.0f - delta) * (1.0f - delta);
        return start + (end - start) * t;
    }

    public static float easeIn(float start, float end, float delta) {
        float t = delta * delta;
        return start + (end - start) * t;
    }

    public static float animate(float current, float target, float speed) {
        float diff = target - current;
        if (Math.abs(diff) < 0.001f) {
            return target;
        }
        return current + diff * Math.min(speed, 1.0f);
    }

    public static float animateDelta(float current, float target, float speed, float deltaTime) {
        float factor = 1.0f - (float)Math.pow(1.0f - speed, deltaTime * 60.0f);
        return current + (target - current) * factor;
    }
}
