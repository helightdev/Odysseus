package dev.helight.odysseus.entity;

import com.google.common.collect.Streams;
import dev.helight.odysseus.database.UnspecificLocation;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArmorStandAnimation {


    public void apply(ArmorStand stand) {

    }

    @Data
    public static class ArmorStandData {

        public static final int BYTE_SIZE = 172;

        private int frame;
        private UnspecificLocation location;
        private float rotation;
        private EulerAngle body;
        private EulerAngle head;
        private EulerAngle leftArm;
        private EulerAngle rightArm;
        private EulerAngle leftLeg;
        private EulerAngle rightLeg;

        public void apply(ArmorStand stand) {
            stand.teleport(location.toLocation(stand.getLocation().getWorld()));
            stand.setRotation(rotation,0);
            stand.setBodyPose(body);
            stand.setHeadPose(head);
            stand.setLeftArmPose(leftArm);
            stand.setRightArmPose(rightArm);
            stand.setLeftLegPose(leftLeg);
            stand.setRightLegPose(rightArm);
        }

        public static ArmorStandData from(ArmorStand armorStand) {
            ArmorStandData armorStandData = new ArmorStandData();
            armorStandData.setLocation(UnspecificLocation.from(armorStand.getLocation()));
            armorStandData.setRotation(armorStand.getLocation().getYaw());
            armorStandData.setBody(armorStand.getBodyPose());
            armorStandData.setHead(armorStand.getHeadPose());
            armorStandData.setLeftArm(armorStand.getLeftArmPose());
            armorStandData.setRightArm(armorStand.getRightArmPose());
            armorStandData.setLeftLeg(armorStand.getLeftLegPose());
            armorStandData.setRightLeg(armorStand.getRightLegPose());
            return armorStandData;
        }

        @Override
        public String toString() {
            return "ArmorStandData{" +
                    "location=" + location +
                    ", rotation=§d" + rotation +
                    "§r, body=" + new UnspecificLocation(body.getX(), body.getY(), body.getZ()) +
                    ", head=" + new UnspecificLocation(head.getX(), head.getY(), head.getZ()) +
                    ", leftArm=" + new UnspecificLocation(leftArm.getX(), leftArm.getY(), leftArm.getZ()) +
                    ", rightArm=" + new UnspecificLocation(rightArm.getX(), rightArm.getY(), rightArm.getZ()) +
                    ", leftLeg=" + new UnspecificLocation(leftLeg.getX(), leftLeg.getY(), leftLeg.getZ()) +
                    ", rightLeg=" + new UnspecificLocation(rightLeg.getX(), rightLeg.getY(), rightLeg.getZ()) +
                    '}';
        }
    }


    public static ArmorStandData readLine(String s) {
        System.out.println(s);
        ArmorStandData data = new ArmorStandData();
        data.setLocation(readPosition(s));
        Matcher m = Pattern.compile("\\[(.*?)]").matcher(s);
        System.out.println(m.find());
        data.setFrame(Integer.parseInt(m.group(1).replace("[","").replace("]","").replace("Tick ","")));
        System.out.println(m.find());
        data.setRotation(Float.parseFloat(m.group(1).replace("[","").replace("]","").replaceAll("f","")));
        System.out.println(m.find());data.setHead(parseAngle(m.group(1)));
        data.setBody(parseAngle(m.group(1)));
        System.out.println(m.find());
        data.setRightArm(parseAngle(m.group(1)));
        System.out.println(m.find());
        data.setLeftArm(parseAngle(m.group(1)));
        System.out.println(m.find());
        data.setRightLeg(parseAngle(m.group(1)));
        System.out.println(m.find());
        data.setLeftLeg(parseAngle(m.group(1)));
        return data;
    }

    @SneakyThrows
    public void animate(List<ArmorStandData> data, ArmorStand armorStand) {
        for (int i = 0; i < data.size(); i++) {
            ArmorStandData d1 = data.get(i);
            d1.apply(armorStand);
            if (i + 1 < data.size()) {
                ArmorStandData d2 = data.get(i + 1);
                Thread.sleep((1000 / 20) * (d2.frame - d1.frame));
            }
        }
    }

    private static EulerAngle parseAngle(String origin) {
        String[] split = origin.replace("[","").replace("]","").replaceAll("f","").split(",");
        return new EulerAngle(
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2])
        );
    }

    private static UnspecificLocation readPosition(String origin) {
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(origin);
        if(m.find()) {
            String posString = m.group(1).replace("(","").replace(")","");
            String[] split = posString.split(", ");
            return new UnspecificLocation(
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2])
            );
        }
        return null;
    }

}
