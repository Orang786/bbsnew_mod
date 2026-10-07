package org.mineland.bbs.common.data.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    public String name;
    public int duration; // длина в кадрах
    public float fps = 30.0f;
    public List<AnimationTrack> tracks = new ArrayList<>();

    public Animation(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public void addTrack(AnimationTrack track) {
        tracks.add(track);
    }

    public void removeTrack(AnimationTrack track) {
        tracks.remove(track);
    }

    public AnimationTrack getTrackByProperty(String property) {
        for (AnimationTrack track : tracks) {
            if (track.property.equals(property)) {
                return track;
            }
        }
        return null;
    }

    public float getLength() {
        return duration / fps;
    }

    public double getCurrentTime(int frame) {
        return frame / fps;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("duration", duration);
        json.addProperty("fps", fps);

        JsonArray tracksArray = new JsonArray();
        for (AnimationTrack track : tracks) {
            tracksArray.add(track.toJson());
        }
        json.add("tracks", tracksArray);

        return json;
    }

    public static Animation fromJson(JsonObject json) {
        Animation anim = new Animation(
                json.get("name").getAsString(),
                json.get("duration").getAsInt()
        );
        anim.fps = json.get("fps").getAsFloat();

        JsonArray tracksArray = json.getAsJsonArray("tracks");
        for (int i = 0; i < tracksArray.size(); i++) {
            AnimationTrack track = AnimationTrack.fromJson(tracksArray.get(i).getAsJsonObject());
            anim.addTrack(track);
        }

        return anim;
    }
}