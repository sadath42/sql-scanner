package com.file.util.vo;

public class SqlComand {
	private String commandName;
	private String commandSqlType; //
	private String commandType;

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public String getCommandSqlType() {
		return commandSqlType;
	}

	public void setCommandSqlType(String commandSqlType) {
		this.commandSqlType = commandSqlType;
	}


	@Override
	public String toString() {
		return "SqlComand [commandName=" + commandName + ", commandSqlType=" + commandSqlType + ", commandType="
				+ commandType + "]";
	}
	
	
	
	

}
