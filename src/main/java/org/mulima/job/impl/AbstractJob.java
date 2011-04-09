/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mulima.job.impl;

import java.util.ArrayList;
import java.util.List;

import org.mulima.job.Context;
import org.mulima.job.Job;
import org.mulima.job.Step;

/**
 * @author Andy
 *
 */
public abstract class AbstractJob implements Job {
	private List<Step> steps = new ArrayList<Step>();
	
	protected void addStep(Step step) {
		steps.add(step);
	}
	
	public Context call() throws Exception {
		Context.pushContext();
		
		Context prevStep = new Context();
		prevStep.setOutputFiles(Context.getCurrent().getInputFiles());
		for (Step step : steps) {
			Context.pushContext();
			Context.getCurrent().setInputFiles(prevStep.getOutputFiles());
			Context.getCurrent().setOutputFiles(null);
			prevStep = step.call();
			Context.popCurrent();
		}
		Context.getCurrent().setOutputFiles(prevStep.getOutputFiles());
		
		return Context.popCurrent();
	}
}
