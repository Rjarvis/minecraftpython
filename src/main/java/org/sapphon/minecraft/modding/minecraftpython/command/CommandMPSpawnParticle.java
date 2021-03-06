package org.sapphon.minecraft.modding.minecraftpython.command;

import java.util.Random;

import net.minecraft.client.Minecraft;

public class CommandMPSpawnParticle extends CommandMPClient {
	
	private double x;
	private double y;
	private double z;
	private int amountToSpawn;
	private String particleType;
	private String targetName;

	public CommandMPSpawnParticle(double x, double y, double z, int amountToSpawn, String particleType){
		this.x = x;
		this.y = y;
		this.z = z;
		this.amountToSpawn = amountToSpawn;
		if(particleType.equals("")){
			this.particleType="enchantmenttable";
		}
		else{
			this.particleType = particleType;
		}
	}
	
	public CommandMPSpawnParticle(String[] commandAndArgsToDeserialize) {
		this(Integer.parseInt(commandAndArgsToDeserialize[1]), Integer.parseInt(commandAndArgsToDeserialize[2]), Integer.parseInt(commandAndArgsToDeserialize[3]), Integer.parseInt(commandAndArgsToDeserialize[4]),commandAndArgsToDeserialize[5]);
	}

	@Override
	public void doWork() {
		for(int i = 0; i < amountToSpawn; i++){
			Random rand = new Random();
			double xPlusOrMinusOneHalf = x -.5 + rand.nextDouble();
			double yPlusOrMinusOneHalf = y -.5 + rand.nextDouble();
			double zPlusOrMinusOneHalf = z -.5 + rand.nextDouble();
			Minecraft.getMinecraft().renderGlobal.spawnParticle(particleType, xPlusOrMinusOneHalf, yPlusOrMinusOneHalf, zPlusOrMinusOneHalf, 0d, 0d, 0d);
		}
	}

	@Override
	public String serialize() {
		return SPAWNPARTICLE_NAME + SERIAL_DIV + x + SERIAL_DIV + y + SERIAL_DIV + z + SERIAL_DIV + this.amountToSpawn + SERIAL_DIV + particleType;
	}

	@Override
	protected String getTargetPlayerName() {
		return this.targetName;
	}
}
