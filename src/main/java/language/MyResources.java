package language;

import application.Application;

public class MyResources extends MyResourceBundle {

	@Override
	protected Object handleGetObject(LanguageKey languageKey) {
		switch (languageKey) {
		case ADD:
			return "Add";
		case AGAINSTCOMPUTER:
			return "Against Computer";
		case AIRPLANE:
			return "Airplane";
		case AIRPLANES:
			return "Airplanes";
		case ATLEASTONECOMPONENT:
			return "Must have at least one Opponent";
		case BLACK:
		case BLACKPLURAL:
			return "Black";
		case BLUE:
		case BLUEPLURAL:
			return "Blue";
		case BUY:
			return "Buy";
		case BUYCONNECTION:
			return "Buy Connection?";
		case CANCEL:
			return "Cancel";
		case CARDRULES:
			return "Cardrules";
		case CARDSLEFT:
			return "Cards left";
		case CARDSLEFTTODRAW:
			return "Cards left to draw";
		case CARRIGECOUNT:
			return "Carrige Count:";
		case CHOOSECOLORFORYOU:
			return "Please choose a Color for you";
		case CLIENTSETTINGS:
			return "Client Settings";
		case COLORCARDCOUNT:
			return "Colorcard Count:";
		case COLORMUSTBESELECTED:
			return "Color must be selected";
		case COMNAMENOTBLANK:
			return " Com Name must not be blank";
		case COMNAMENOTEMPTY:
			return "Com Name must not be emtpy";
		case COMS:
			return "Coms";
		case CONNECTIONDESCRIPTION:
			return "Connection: From %s to %s, Cost: %d %s %s";
		case CREATENEWGAME:
			return "Create new Game";
		case CREATENEWMAP:
			return "Create new Map";
		case CURRENTPLAYER:
			return "Current Player";
		case DEFAULTCOLOCARDDRAWING:
			return "Default Color Cards Drawing:";
		case DEFAULTMISSIONCARDKEEPING:
			return "Default Missioncard Keeping:";
		case DRAWCARD:
			return "Draw Card";
		case DRAWMISSIONCARDS:
			return "Draw Mission Cards";
		case EASY:
			return "Easy";
		case EDITMAP:
			return "Edit Map";
		case EDITMISSIONCARDS:
			return "Edit Mission Cards";
		case EXTRALONG:
			return "Extra Long";
		case EXTREME:
			return "Extreme";
		case FINISHEDMISSIONCARDS:
			return "Finished Missioncards";
		case FINISHEDMISSIONS:
			return "Finished Missions";
		case FIRSTCOLORCARDDRAWING:
			return "First Color Cards Drawing:";
		case FIRSTMISSIONCARDKEEPING:
			return "First Missioncard Keeping:";
		case FOLDERNOTCREATED:
			return Application.NAME + " Folder could not be created";
		case GAME:
			return "Game";
		case GAMESETTINGS:
			return "Game Settings";
		case GRAY:
			return "Gray";
		case GREEN:
		case GREENPLURAL:
			return "Green";
		case HARD:
			return "Hard";
		case HIDE:
			return "Hide";
		case JOINGAME:
			return "Join Game";
		case LOCOMOTIVCARDCOUNT:
			return "Locomotivcard Count:";
		case LOCOMOTIVCARDLIMIT:
			return "Locomotiv Card Limit:";
		case LOCOMOTIVEWORTH:
			return "Locomotive Worth:";
		case LONG:
			return "Long";
		case MAP:
			return "Map";
		case MAPCREATOR:
			return "Map Creator";
		case MAPFOLDERNOTCREATED:
			return "Mapfolder could not be created";
		case MAPTOOSMALL:
			return "Map is smaller than the recommended 2000 x 2000 Pixels.";
		case MAXLOCOMOTIVES:
			return "Max Locomotives:";
		case MEDIUM:
			return "Medium";
		case MIDDLE:
			return "Middle";
		case MISSIONCARDDRAWING:
			return "Missioncard Drawing:";
		case MISSIONCARDSELECTION:
			return "Missioncard Selection";
		case MOVEDOWN:
			return "Move Down";
		case MOVEFIRST:
			return "Move First";
		case MOVELAST:
			return "Move Last";
		case MOVELEFT:
			return "Move Left";
		case MOVERIGHT:
			return "Move Right";
		case MOVEUP:
			return "Move Up";
		case NAME:
			return "Name:";
		case NEWGAME:
			return "New Game";
		case NEWMAP:
			return "New Map";
		case NORMALCARDLIMIT:
			return "Normal Card Limit:";
		case NOTALLFILESCREATED:
			return "Not all Files could be created";
		case OK:
			return "OK";
		case OPENCARDSLAYINGDOWN:
			return "Open Cards Laying Down:";
		case ORANGE:
		case ORANGEPLURAL:
			return "Orange";
		case PLAYER:
			return "Player";
		case PLAYERNAMENOTBLANK:
			return "Playername must not be blank";
		case PLAYERNAMENOTEMPTY:
			return "Playername must not be empty";
		case PLAYERRULES:
			return "Playerrules";
		case PLAYONLINE:
			return "Play Online";
		case POINTS:
			return "Points";
		case POINTSFORLENGTH:
			return "Points for length %d:";
		case PURPLE:
		case PURPLEPLURAL:
			return "Purple";
		case QUITGAME:
			return "Quit Game";
		case RAINBOW:
			return "Rainbow";
		case RED:
		case REDPLURAL:
			return "Red";
		case REMOVE:
			return "Remove";
		case RESET:
			return "Reset";
		case RULES:
			return "Rules";
		case SAVEFOLDERNOTCREATED:
			return "Saved Game Folder could not be created";
		case SELECTATLEASTMISSION:
			return "Please select at least %d Missions";
		case SELECTMISSION:
			return "Select Mission";
		case SELECTORCANCEL:
			return "Please select an Option or Cancel";
		case SETTINGS:
			return "Settings";
		case SHIP:
			return "Ship";
		case SHIPS:
			return "Ships";
		case SHORT:
			return "Short";
		case SHOWFINISHEDMISSIONCARDS:
			return "Show Finished Mission Cards";
		case SHOWMISSION:
			return "Show Mission";
		case SHUFFLEWITHLOCOMOTIV:
			return "Shuffle With Locomotives:";
		case SUMOFMISSIONCARDS:
			return "The sum of all Mission Cards must be %d";
		case TRAIN:
			return "Train";
		case TRAINS:
			return "Trains";
		case VIA:
			return "Via";
		case WHITE:
		case WHITEPLURAL:
			return "White";
		case YELLOW:
		case YELLOWPLURAL:
			return "Yellow";
		}
		return "NO RESOURCE";
	}

}
