package org.mineland.bbs.common.data.animation;

import com.google.gson.JsonObject;

public class Keyframe {

    public int frame;
    public float value;
    public InterpolationType interpolation;

    public enum InterpolationType {
        LINEAR("linear"),
        EASE_IN("ease_in"),
        EASE_OUT("ease_out"),
        EASE_IN_OUT("ease_in_out"),
        CUBIC("cubic"),
        SMOOTH_STEP("smooth_step");

        public final String id;

        InterpolationType(String id) {
            this.id = id;
        }

        public static InterpolationType fromId(String id) {
            for (InterpolationType type : values()) {
                if (type.id.equals(id)) return type;
            }
            return LINEAR;
        }
    }

    public Keyframe(int frame, float value, InterpolationType interpolation) {
        this.frame = frame;
        this.value = value;
        this.interpolation = interpolation;
    }

    public Keyframe copy() {
        return new Keyframe(frame, value, interpolation);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("frame", frame);
        json.addProperty("value", value);
        json.addProperty("interpolation", interpolation.id);
        return json;
    }

    public static Keyframe fromJson(JsonObject json) {
        return new Keyframe(
                json.get("frame").getAsInt(),
                json.get("value").getAsFloat(),
                InterpolationType.fromId(json.get("interpolation").getAsString())
        );
    }
}