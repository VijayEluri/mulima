ruleset {
	ruleset('rulesets/basic.xml')
	ruleset('rulesets/braces.xml')
	ruleset('rulesets/concurrency.xml')
	ruleset('rulesets/design.xml')
	ruleset('rulesets/exceptions.xml')
	ruleset('rulesets/generic.xml') {
		'StatelessClass' {
			doNotApplyToFileNames = '*Test.groovy'
		}
	}
	ruleset('rulesets/imports.xml')
	ruleset('rulesets/junit.xml')
	ruleset('rulesets/logging.xml')
	ruleset('rulesets/naming.xml') {
		'FieldName' {
			ignoreFieldNames = 'logger'
		}
	}
	ruleset('rulesets/size.xml')
	ruleset('rulesets/unused.xml')
}