package org.mineland.bbs.common.data.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.TreeMap;

public class AnimationTrack {

    public String name;
    public String property;
    public TreeMap<Integer, Keyframe> keyframes = new TreeMap<>();

    public AnimationTrack(String name, String property) {
        this.name = name;
        this.property = property;
    }

    public void addKeyframe(int frame, float value, Keyframe.InterpolationType interpolation) {
        keyframes.put(frame, new Keyframe(frame, value, interpolation));
    }

    public void removeKeyframe(int frame) {
        keyframes.remove(frame);
    }

    public boolean hasKeyframe(int frame) {
        return keyframes.containsKey(frame);
    }

    public Keyframe getKeyframe(int frame) {
        return keyframes.get(frame);
    }

    public float getValue(int frame) {
        if (keyframes.isEmpty()) return 0;
        if (keyframes.size() == 1) {
            return keyframes.values().iterator().next().value;
        }

        Integer floorKey = keyframes.floorKey(frame);
        Integer ceilKey = keyframes.ceilingKey(frame);

        if (floorKey == null) {
            return keyframes.firstEntry().getValue().value;
        }
        if (ceilKey == null || floorKey.equals(ceilKey)) {
            return keyframes.get(floorKey).value;
        }

        Keyframe kf1 = keyframes.get(floorKey);
        Keyframe kf2 = keyframes.get(ceilKey);

        float t = (float) (frame - kf1.frame) / (kf2.frame - kf1.frame);

        return interpolate(kf1.value, kf2.value, t, kf1.interpolation);
    }

    private float interpolate(float a, float b, float t, Keyframe.InterpolationType type) {
        t = Math.max(0, Math.min(1, t));

        return switch (type) {
            case LINEAR -> a + (b - a) * t;
            case EASE_IN -> a + (b - a) * t * t;
            case EASE_OUT -> a + (b - a) * (1 - (1 - t) * (1 - t));
            case EASE_IN_OUT -> a + (b - a) * (t < 0.5f ?
                    2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2);
            case CUBIC -> a + (b - a) * t * t * t;
            case SMOOTH_STEP -> a + (b - a) * (3 * t * t - 2 * t * t * t);
        };
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("property", property);

        JsonArray keyframeArray = new JsonArray();
        for (Keyframe kf : keyframes.values()) {
            keyframeArray.add(kf.toJson());
        }
        json.add("keyframes", keyframeArray);

        return json;
    }

    public static AnimationTrack fromJson(JsonObject json) {
        AnimationTrack track = new AnimationTrack(
                json.get("name").getAsString(),
                json.get("property").getAsString()
        );

        JsonArray keyframeArray = json.getAsJsonArray("keyframes");
        for (int i = 0; i < keyframeArray.size(); i++) {
            Keyframe kf = Keyframe.fromJson(keyframeArray.get(i).getAsJsonObject());
            track.keyframes.put(kf.frame, kf);
        }

        return track;
    }
}