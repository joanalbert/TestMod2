package net.fabricmc.example.entities.TestEntity;


import net.minecraft.util.math.Vec3d;

public class TurretData {
    public Vec3d headPosition;
    public Vec3d headRotations;
    public Vec3d nozzlePosition;
    public Vec3d target;

    public TurretData(Vec3d headPos, Vec3d headRot, Vec3d nozzle , Vec3d target){
        this.headRotations = headRot;
        this.headPosition = headPos;
        this.nozzlePosition = nozzle;
        this.target = target;
    }
}
