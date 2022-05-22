package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class RuleSet implements Serializable {

	private static final long serialVersionUID = 1546154681057412710L;

	public static RuleSet load(File folder) throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(new File(folder, GameMap.RULESFILE));
		ObjectInputStream ois = new ObjectInputStream(fis);
		RuleSet ruleSet = (RuleSet) ois.readObject();
		fis.close();
		ois.close();
		return ruleSet;
	}

	public static RuleSet emptyRuleSet() {
		return new RuleSet();
	}

	public RuleSet() {}

}
