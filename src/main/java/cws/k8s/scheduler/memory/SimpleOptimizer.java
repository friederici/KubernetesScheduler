/*
 * Copyright (c) 2023, Florian Friederici. All rights reserved.
 * 
 * This code is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more 
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this work. If not, see <https://www.gnu.org/licenses/>. 
 */

package cws.k8s.scheduler.memory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * SimpleOptimizer will use the following strategy:
 * 
 * - In case task has failed, double memory
 * - In case task was successful: reduce memory according to:
 * 
 *   if no old suggestion exists:
 *    - let the new suggestion be 10% higher, then the peakRss was
 *   else:
 *    - new suggestion = (new peakRss + last suggestion) / 2
 * 
 * @author Florian Friederici
 *
 */
@Slf4j
class SimpleOptimizer implements MemoryOptimizer {

	List<Observation> observations;
	Map<String, BigDecimal> suggestions;

	public SimpleOptimizer() {
		observations = new ArrayList<>();
		suggestions = new HashMap<>();
	}
	
	@Override
	public void addObservation(Observation o) {
		log.debug("SimpleOptimizer.addObservation");
		this.observations.add(o);
		
		if (o.success) {
			// decrease suggestion
			if (suggestions.containsKey(o.task)) {
				BigDecimal sug = ( o.peakRss.add(suggestions.get(o.task)) ).divide(new BigDecimal(2));
				suggestions.replace(o.task, sug.setScale(0, RoundingMode.CEILING));
			} else {
				suggestions.put(o.task, o.peakRss.multiply(new BigDecimal(1.1)).setScale(0, RoundingMode.CEILING) );
			}
		} else {
			// increase suggestion
			if (suggestions.containsKey(o.task)) {
				BigDecimal sug = suggestions.get(o.task).multiply(new BigDecimal(2));
				suggestions.replace(o.task, sug.setScale(0, RoundingMode.CEILING));
			} else {
				suggestions.put(o.task, o.ramRequest.multiply(new BigDecimal(2)).setScale(0, RoundingMode.CEILING) );
			}
		}
		
	}
	
	@Override
	public String querySuggestion(String task) {
		if (suggestions.containsKey(task)) {
			return suggestions.get(task).toPlainString();
		} else {
			return null;
		}
	}
}
