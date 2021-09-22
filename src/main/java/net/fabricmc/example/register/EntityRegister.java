package net.fabricmc.example.register;



import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.entities.ProjectileEntity.ProjectileEntity;
import net.fabricmc.example.entities.SkeletonEntity.MySkeletonEntity;
import net.fabricmc.example.entities.TestEntity.TestEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegister {
    
    public static EntityType<TestEntity> TEST_ENTITY;
	public static EntityType<MySkeletonEntity> MY_SKELETON;
	public static EntityType<ProjectileEntity> MY_PROJECTILE;
 
    public static void init(){
        registerTestEntity();
        registerSkeletonEntity();
        registerMyProjectile();
    }

    public static void registerTestEntity(){

		/*
		* Registers our TestEntity under the ID modid:test_entity
		* 
		* The Entity is registered under the SpawnGroup#CREATURE category, which is what most animals and passive/neutral mobs use.
		* It has a hitbox size of 0.75x0.75, or 12 pixels wide (3/4 of a block)
		*/
		TEST_ENTITY = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(ExampleMod.MODID, "test_entity"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TestEntity::new).dimensions(EntityDimensions.fixed(1f, 4f)).build()
		);

		//ATTRIBUTES
	
		/*
			* Register our Entity's default attributes.
			* Attributes are properties or stats of the mobs, including things like attack damage and health.
			* The game will crash if the entity doesn't have the proper attributes registered in time.
			* 
			* In 1.15 this was done by a method override inside the entity class.
			* Most vanilla entities have a static method (eg. ZombieEntity#createZombieAttributes) for initializing their attributes.
		*/
		FabricDefaultAttributeRegistry.register(TEST_ENTITY, TestEntity.createMobAttributes().add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100f));
	}

    public static void registerSkeletonEntity(){

		//entity
		MY_SKELETON = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(ExampleMod.MODID, "my_skeleton_entity"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MySkeletonEntity::new).dimensions(EntityDimensions.fixed(0.6f,1.99f)).build()
		);

		//attributes
		FabricDefaultAttributeRegistry.register(MY_SKELETON, MySkeletonEntity.createMobAttributes());
	}

    public static void registerMyProjectile(){
        MY_PROJECTILE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(ExampleMod.MODID, "my_projectile"),
			FabricEntityTypeBuilder.<ProjectileEntity>create(SpawnGroup.MISC, ProjectileEntity::new)
													  .dimensions(EntityDimensions.fixed(.25f, .25f))
													  .trackRangeBlocks(20)
													  .trackedUpdateRate(20)
													  .build() //important
		);
    }
}
