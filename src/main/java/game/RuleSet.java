package game;

import java.io.Serializable;

public class RuleSet implements Serializable {

	private static final long serialVersionUID = 1546154681057412710L;

	public static RuleSet emptyRuleSet() {
		return new RuleSet();
	}

	public RuleSet() {}

}
