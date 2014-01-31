package hawox.uquest;

import hawox.uquest.enums.EnchantNames;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
@SuppressWarnings("deprecation")
public class UQuestUtils {

	public static int checkBlock(Block block) {

		int bID = block.getTypeId();
    	int bDur = block.getData();
		int toreturn = bDur;
    	//Checks blocks and changes them if the placed id is different then the item id.
		if( bID == 43 || bID == 47 || bID == 55 || bID == 63 || bID == 64 || bID == 68 || bID == 71 ||  bID == 93 ||  bID == 123 || bID == 132) {
			if(bID == 43) toreturn = 44; //Slabs *Double Slabs check*
			if(bID == 63 || bID == 68) toreturn = 323;//Signs
			if(bID == 47) toreturn = 46;//TnT
			if(bID == 64) toreturn = 324;//Wooden Door
			if(bID == 71) toreturn = 330;//Iron Door
		    if(bID == 132) toreturn = 131;//Trip Wire Hook
		    if(bID == 123) toreturn = 124;//Redstone Lamp
		    if(bID == 93) toreturn = 356;//Redstone Repeater
		    if(bID == 55) toreturn = 331;//Redstone Dust
		} else {
			toreturn = bID;
		}
		return toreturn;
	}
	public static int checkDurability(Block block) {
    	int bID = block.getTypeId();
    	int bDur = block.getData();
		int toreturn = bDur;
    	//Logs, Leaves, Half Slabs, Vines.
    	if(bID == 17 || bID == 18 || bID == 44 || bID == 126 || bID == 106) {
    		if(bDur == 8) toreturn = 0;
    		if(bDur == 9)  toreturn = 1;
    		if(bDur == 10) toreturn = 2;
    		if(bDur == 11) toreturn = 3;
    		if(bDur == 12) toreturn = 4;
    		if(bDur == 13) toreturn = 5;
    		if(bID == 17) {//For sideways logs.
    			if(bDur == 4) toreturn = 0;
    			if(bDur == 5) toreturn = 1;
    			if(bDur == 6) toreturn = 2;
    			if(bDur == 7) toreturn = 3;
    		}
    	} else {
        /*
         * There are lots of blocks that have different durability when placed and depending on the direction placed.
         * So quests don't have to tell them what direction, they just have to put 0 for durability and
         * this has an array with all those blocks and just sets the durability check to 0 no matter what direction they
         * are placed. *Croyd*
         * 
         * Block List
         * 23 Dispenser
         * 27 Powered Rail
         * 28 Detector Rail
         * 50 Torch
         * 53 Wooden Stairs
         * 54 Chest
		 * 65 Ladder
		 * 66 Rail
         * 67 Stone Stairs
         * 69 Lever
         * 76 Redstone Torch
         * 77 Stone Button
         * 86 Pumpkin
         * 91 Jack 'o' Lantern
         * 96 Trap Door
         * 108 Brick Stairs
         * 109 Stone Brick Stairs
         * 114 Nether Brick Stairs
         * 128 Sandstone Stairs
         * 130 Ender Chest
         * 134 Spruce Stairs
         * 135 Birch Stairs
         * 136 Jungle Stairs
         * 323 Sign *Usually 63 or 68 when placed but the block check before this will change that.*
         * 324 Wooden Door *Usually 64 when placed but the block check before this will change that.*
         * 330 Iron Door *Usually 71 but the block check before this will change that.
         * 356 Redstone Repeater *Usually 93 but the block check before this will change that.
         */
    		int blockDurZero[] = {23,27,28,50,53,54,65,66,67,69,76,77,86,91,96,108,109,114,128,130,134,135,136,323,324,330,356};
    		for(int i = 0; i < blockDurZero.length; i++) {
        		if(bID == blockDurZero[i]) {
        			toreturn = 0;
        		}
        	}
    	}    		
		return toreturn;
	}
	public static String formatName(String name) {
		String iName = name.toLowerCase();
		if(iName.contains("_") || iName.contains(" ")){
			String[] cName = null;
			if(iName.contains("_"))
				cName = iName.split("_");
			if(iName.contains(" "))
				cName = iName.split(" ");
			String fName = cName[0].substring(0,1).toUpperCase() + cName[0].substring(1);
			String sName = cName[1].substring(0,1).toUpperCase() + cName[1].substring(1);
			iName = fName + " " + sName;
		} else {
			iName = iName.substring(0,1).toUpperCase() + iName.substring(1);
		}
		return iName;
	}
	//Formats the auto update message.
	public static String formatUpdateMessage(String qtype, String oname, int amount, int needed){
		String theString = "";
		String theSlash = ChatColor.WHITE + "/";
		String colorAmount;
		String colorNeeded = ChatColor.GREEN + Integer.toString(needed);
		String name = formatName(oname.toLowerCase());
		if(amount == needed) {
			colorAmount = ChatColor.GREEN + Integer.toString(amount);
		} else {
			colorAmount = ChatColor.AQUA + Integer.toString(amount);
		}
		switch(qtype){
		case "gather":
			theString += ChatColor.GREEN;
			theString += "Gathered  ";
			break;
		case "shear":
			theString += ChatColor.YELLOW;
			theString += "Sheared a ";
			break;
		case "kill":
			theString += ChatColor.RED;
			theString += "Killed ";
			break;
		case "blockdestroy":
			theString += ChatColor.DARK_RED;
			theString += "Destroyed ";
			break;
		case "blockplace":
			theString += ChatColor.DARK_GREEN;
			theString += "Placed ";
			break;
		case "blockdamage":
			theString += ChatColor.DARK_AQUA;
			theString += "Damaged a ";
			break;
		case "fish":
			theString += ChatColor.BLUE;
			theString += "Hooked a ";
			break;
		case "fillbucket":
			theString += ChatColor.DARK_BLUE;
			if(name.equals("Mushroom Soup")) {
				theString += "Filled bowl with ";
			} else {
				theString += "Filled bucket with ";	
			}
			break;
		case "switch":
			theString += ChatColor.GREEN;
			if(name.equals("Lever"))
				theString += "Toggled a ";
			if(name.equals("Button"))
				theString += "Pushed a ";
			if(name.equals("Wooden Door") || name.equals("Iron Door"));
				theString += "Opened/Closed a ";
			if(name.equals("Trap Door"))
				theString += "Opened/Closed a ";
			break;
		case "till":
			theString += ChatColor.DARK_GREEN;
			theString += "Tilled ";
			break;
		case "plant":
			theString += ChatColor.GREEN;
			theString += "Planted some ";
			break;
		case "dye":
			theString += ChatColor.YELLOW;
			theString += "Dyed a ";
		}
		theString += name + " " + colorAmount + theSlash + colorNeeded;
		return theString;
	}
    //This returns the enchantment's in game name, DURABILITY would come back as Unbreaking
    public static String getEnchantName(String name) {
    	String newName = "";
    	for(EnchantNames enchant : EnchantNames.values()) {
    		if(enchant.name().equalsIgnoreCase(name)) {
    			newName = enchant.getName();
    		}
    	}
    	if(newName.equalsIgnoreCase(""))
    		newName = name;
        return newName;
    }
}
