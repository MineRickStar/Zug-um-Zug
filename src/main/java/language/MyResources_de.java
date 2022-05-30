package language;

import application.Application;

public class MyResources_de extends MyResources {

	@Override
	protected Object handleGetObject(LanguageKey languageKey) {
		switch (languageKey) {
		case ADD:
			return "Hinzufügen";
		case AGAINSTCOMPUTER:
			return "Gegen Computer";
		case AIRPLANE:
			return "Flugzeug";
		case AIRPLANES:
			return "Flugzeuge";
		case ATLEASTONECOMPONENT:
			return "Mindestends einen Gegner";
		case BLACK:
			return "Schwarz";
		case BLACKPLURAL:
			return "Schwarze";
		case BLUE:
			return "Blau";
		case BLUEPLURAL:
			return "Blaue";
		case BUY:
			return "Kaufen";
		case BUYCONNECTION:
			return "Verbindung kaufen?";
		case CANCEL:
			return "Abbrechen";
		case CARDRULES:
			return "Kartenregeln";
		case CARDSLEFT:
			return "Karten übrig";
		case CARDSLEFTTODRAW:
			return "Karten übrig zu ziehen";
		case CARRIGECOUNT:
			return "Waggenanzahl:";
		case CHOOSECOLORFORYOU:
			return "Bitte such eine Farbe für dich aus";
		case CLIENTSETTINGS:
			return "Client Einstellungen";
		case COLORCARDCOUNT:
			return "Farbenkarten Anzahl:";
		case COLORMUSTBESELECTED:
			return "Farbe muss ausgewählt werden";
		case COMNAMENOTBLANK:
			return "Com Name darf nicht blank sein";
		case COMNAMENOTEMPTY:
			return "Com Name darf nicht leer sein";
		case COMS:
			return "Coms";
		case CONNECTIONDESCRIPTION:
			return "Verbidung von %s nach %s, Kosten: %d %s %s";
		case CREATENEWGAME:
			return "Neues Spiel erstellen";
		case CREATENEWMAP:
			return "Neue Karte erstellen";
		case CURRENTPLAYER:
			return "Aktueller Spieler";
		case DEFAULTCOLOCARDDRAWING:
			return "Default Farbenkarten ziehen:";
		case DEFAULTMISSIONCARDKEEPING:
			return "Default Missionskarten behalten:";
		case DRAWCARD:
			return "Karten ziehen";
		case DRAWMISSIONCARDS:
			return "Missionkarten ziehen";
		case EASY:
			return "Einfach";
		case EDITMAP:
			return "Karte bearbeiten";
		case EDITMISSIONCARDS:
			return "Missionskarten bearbeiten";
		case EXTRALONG:
			return "Extra lang";
		case EXTREME:
			return "Extrem";
		case FINISHEDMISSIONCARDS:
			return "Fertige Missionskarten";
		case FINISHEDMISSIONS:
			return "Fertige Missionen";
		case FIRSTCOLORCARDDRAWING:
			return "Erstes Farbenkarten ziehen:";
		case FIRSTMISSIONCARDKEEPING:
			return "Erstes Missionkarten behalten:";
		case FOLDERNOTCREATED:
			return Application.NAME + " Ordner konnte nicht erstellt werden";
		case GAME:
			return "Spiel";
		case GAMESETTINGS:
			return "Spieleinstellungen";
		case GRAY:
			return "Grau";
		case GREEN:
			return "Grün";
		case GREENPLURAL:
			return "Grüne";
		case HARD:
			return "Hart";
		case HIDE:
			return "Verstecken";
		case JOINGAME:
			return "Spiel joinen";
		case LOCOMOTIVCARDCOUNT:
			return "Lokomotivkarten Anzahl:";
		case LOCOMOTIVCARDLIMIT:
			return "Lokomitivkarten Limit:";
		case LOCOMOTIVEWORTH:
			return "Lokomotive Wert:";
		case LONG:
			return "Lang";
		case MAP:
			return "Karte";
		case MAPCREATOR:
			return "Karten Ersteller";
		case MAPFOLDERNOTCREATED:
			return "Kartenordner konnte nicht erstellt werden";
		case MAPTOOSMALL:
			return "Karte ist kleiner als die vorgeschlagene Größe 2000 x 2000 Pixel.";
		case MAXLOCOMOTIVES:
			return "Maximale Lokomotiven:";
		case MEDIUM:
			return "Medium";
		case MIDDLE:
			return "Mittel";
		case MISSIONCARDDRAWING:
			return "Missionkarten ziehen:";
		case MISSIONCARDSELECTION:
			return "Missionskartenauswahl";
		case MOVEDOWN:
			return "Runter";
		case MOVEFIRST:
			return "Erste";
		case MOVELAST:
			return "Letzte";
		case MOVELEFT:
			return "Links";
		case MOVERIGHT:
			return "Rechts";
		case MOVEUP:
			return "Hoch";
		case NAME:
			return "Name:";
		case NEWGAME:
			return "Neues Spiel";
		case NEWMAP:
			return "Neue Karte";
		case NORMALCARDLIMIT:
			return "Normale Karten Limit:";
		case NOTALLFILESCREATED:
			return "Nicht alle Dokumente konnte erzeugt werden";
		case OK:
			return "OK";
		case OPENCARDSLAYINGDOWN:
			return "Offene Karten";
		case ORANGE:
		case ORANGEPLURAL:
			return "Orange";
		case PLAYER:
			return "Spieler";
		case PLAYERNAMENOTBLANK:
			return "Spielername darf nicht blank sein";
		case PLAYERNAMENOTEMPTY:
			return "Spielername darf nicht leer sein";
		case PLAYERRULES:
			return "Spielerregeln";
		case PLAYONLINE:
			return "Online Spielen";
		case POINTS:
			return "Punkte";
		case POINTSFORLENGTH:
			return "Punkte für Länge %d:";
		case PURPLE:
			return "Violett";
		case PURPLEPLURAL:
			return "Violette";
		case QUITGAME:
			return "Spiel beenden";
		case RAINBOW:
			return "Regenbogen";
		case RED:
			return "Rot";
		case REDPLURAL:
			return "Rote";
		case REMOVE:
			return "Entfernen";
		case RESET:
			return "Reset";
		case RULES:
			return "Regeln";
		case SAVEFOLDERNOTCREATED:
			return "Gespeicherte Spiele Ordner konnte nicht erstellt werden";
		case SELECTATLEASTMISSION:
			return "Bitte waähle mindestends %d Missionen aus";
		case SELECTMISSION:
			return "Missionen auwählen";
		case SELECTORCANCEL:
			return "Bitte ein Option auswählen oder Abbrechen";
		case SETTINGS:
			return "Einstellungen";
		case SHIP:
			return "Schiff";
		case SHIPS:
			return "Schiffe";
		case SHORT:
			return "Kurz";
		case SHOWFINISHEDMISSIONCARDS:
			return "Zeige fertige Missionkarten";
		case SHOWMISSION:
			return "Zeige Missionen";
		case SHUFFLEWITHLOCOMOTIV:
			return "Mische mit Lokomotiven:";
		case SUMOFMISSIONCARDS:
			return "Die Summe aller Missionskarten muss %d betragen";
		case TRAIN:
			return "Zug";
		case TRAINS:
			return "Züge";
		case VIA:
			return "Via";
		case WHITE:
			return "Weiß";
		case WHITEPLURAL:
			return "Weiße";
		case YELLOW:
			return "Gelb";
		case YELLOWPLURAL:
			return "Gelbe";
		}
		return super.handleGetObject(languageKey);
	}

}
