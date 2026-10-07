package org.mineland.bbs.common.data.scene;

import com.google.gson.JsonObject;
import org.joml.Vector3f;
import org.joml.Quaternionf;

public class Transform {

    public Vector3f position = new Vector3f(0, 0, 0);
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1, 1, 1);

    public Transform() {}

    public Transform(Vector3f pos, Quaternionf rot, Vector3f scl) {
        this.position = new Vector3f(pos);
        this.rotation = new Quaternionf(rot);
        this.scale = new Vector3f(scl);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        JsonObject posJson = new JsonObject();
        posJson.addProperty("x", position.x);
        posJson.addProperty("y", position.y);
        posJson.addProperty("z", position.z);
        json.add("position", posJson);

        JsonObject rotJson = new JsonObject();
        rotJson.addProperty("x", rotation.x);
        rotJson.addProperty("y", rotation.y);
        rotJson.addProperty("z", rotation.z);
        rotJson.addProperty("w", rotation.w);
        json.add("rotation", rotJson);

        JsonObject scaleJson = new JsonObject();
        scaleJson.addProperty("x", scale.x);
        scaleJson.addProperty("y", scale.y);
        scaleJson.addProperty("z", scale.z);
        json.add("scale", scaleJson);

        return json;
    }

    public static Transform fromJson(JsonObject json) {
        Transform transform = new Transform();

        JsonObject posJson = json.getAsJsonObject("position");
        transform.position.set(
                posJson.get("x").getAsFloat(),
                posJson.get("y").getAsFloat(),
                posJson.get("z").getAsFloat()
        );

        JsonObject rotJson = json.getAsJsonObject("rotation");
        transform.rotation.set(
                rotJson.get("x").getAsFloat(),
                rotJson.get("y").getAsFloat(),
                rotJson.get("z").getAsFloat(),
                rotJson.get("w").getAsFloat()
        );

        JsonObject scaleJson = json.getAsJsonObject("scale");
        transform.scale.set(
                scaleJson.get("x").getAsFloat(),
                scaleJson.get("y").getAsFloat(),
                scaleJson.get("z").getAsFloat()
        );

        return transform;
    }

    public Transform copy() {
        return new Transform(position, rotation, scale);
    }
}