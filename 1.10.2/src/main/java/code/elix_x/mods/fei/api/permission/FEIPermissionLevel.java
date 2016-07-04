package code.elix_x.mods.fei.api.permission;

public enum FEIPermissionLevel {

	USER(0), MODERATOR(1), ADMINISTRATOR(2), OWNER(3);

	public static String[] names(){
		return new String[]{USER.name(), MODERATOR.name(), ADMINISTRATOR.name(), OWNER.name()};
	}

	private int level;

	private FEIPermissionLevel(int level){
		this.level = level;
	}

	public boolean isHigher(FEIPermissionLevel llevel){
		return level > llevel.level;
	}

	public boolean isHigherOrEqual(FEIPermissionLevel llevel){
		return level >= llevel.level;
	}

	public boolean isLower(FEIPermissionLevel llevel){
		return level < llevel.level;
	}

	public boolean isLowerOrEqual(FEIPermissionLevel llevel){
		return level <= llevel.level;
	}

	public boolean isUser(){
		return true;
	}

	public boolean isModerator(){
		return level > 0;
	}

	public boolean isAdmindistrator(){
		return level > 1;
	}

}
