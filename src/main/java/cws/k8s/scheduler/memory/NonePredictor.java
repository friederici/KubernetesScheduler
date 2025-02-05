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

import cws.k8s.scheduler.model.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * NonePredictor will not provide predictions at all. Which results in no
 * changes to tasks in consequence. This is useful as baseline.
 * 
 * @author Florian Friederici
 *
 */
@Slf4j
public class NonePredictor implements MemoryPredictor {

    @Override
    public void addObservation(Observation o) {
        log.debug("NonePredictor.addObservation({})", o);
        if (!TaskScaler.checkObservationSanity(o)) {
            log.warn("dismiss observation {}", o);
            return;
        }
    }

    @Override
    public BigDecimal queryPrediction(Task task) {
        log.debug("NonePredictor.queryPrediction({})", task);
        return null;
    }

}
