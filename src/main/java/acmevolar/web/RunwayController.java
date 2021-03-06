/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package acmevolar.web;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import acmevolar.model.Airport;
import acmevolar.model.Runway;
import acmevolar.model.RunwayType;
import acmevolar.service.AirportService;
import acmevolar.service.FlightService;
import acmevolar.service.RunwayService;
import acmevolar.service.exceptions.NonDeletableException;

@Controller
public class RunwayController {

	private final RunwayService	runwayService;
	private final FlightService	flightService;

	private static final String	VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM	= "runways/createOrUpdateRunwaysForm";
	private static final String	RUNWAY_CONSTANT						= "runway";


	@Autowired
	public RunwayController(final RunwayService runwayService, final AirportService airportService, final FlightService flightService) {
		this.runwayService = runwayService;
		this.flightService = flightService;
	}

	//LIST
	@PreAuthorize("hasAuthority('airline') || hasAuthority('client')")
	@GetMapping(value = {
		"/airports/{airportId}/runways"
	})
	public String showRunwayList(final Map<String, Object> model, @PathVariable("airportId") final int airportId) {

		List<Runway> runways = this.runwayService.findRunwaysByAirportId(airportId);
		Airport airport = this.runwayService.findAirportById(airportId);
		model.put("runways", runways);
		model.put("airport", airport);
		return "runways/runwayList";
	}

	public void insertData(final Map<String, Object> model) {
		List<RunwayType> runwayTypes = this.runwayService.findRunwaysTypes();
		model.put("runwayTypes", runwayTypes);
	}

	//CREATE
	@PreAuthorize("hasAuthority('airline')")
	@GetMapping(value = "/airports/{airportId}/runways/new")
	public String initCreationForm(final Map<String, Object> model, @PathVariable("airportId") final int airportId) {
		Runway runway = new Runway();

		Airport airport = this.runwayService.findAirportById(airportId);
		airport.addRunway(runway);
		runway.setAirport(airport);

		this.insertData(model);

		model.put("airport", airport);
		model.put(RunwayController.RUNWAY_CONSTANT, runway);

		return RunwayController.VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM;
	}

	@PreAuthorize("hasAuthority('airline')")
	@PostMapping(value = "/airports/{airportId}/runways/new")
	public String processCreationForm(final Map<String, Object> model, @Valid final Runway runway, final BindingResult result, @PathVariable("airportId") final int airportId) {
		Airport airport = this.runwayService.findAirportById(airportId);
		airport.addRunway(runway);
		runway.setAirport(airport);
		if (result.hasErrors()) {
			this.insertData(model);
			model.put(RunwayController.RUNWAY_CONSTANT, runway);
			return RunwayController.VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM;

		} else if (!this.runwayService.findRunwaysByName(runway.getName()).isEmpty()) {

			result.rejectValue("name", "NameIsAlreadyUsed", "Name is already used");
			return RunwayController.VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM;

		} else {
			try {
				this.runwayService.saveRunway(runway);
			} catch (DataAccessException e) {
				e.printStackTrace();
			}

			return "redirect:/airports/" + airportId + "/runways";
		}
	}

	//UPDATE
	@PreAuthorize("hasAuthority('airline')")
	@GetMapping(value = "/airports/{airportId}/runways/{runwayId}/edit")
	public String initUpdateForm(@PathVariable("runwayId") final int runwayId, final ModelMap model) {
		Runway runway = this.runwayService.findRunwayById(runwayId);

		this.insertData(model);

		model.put(RunwayController.RUNWAY_CONSTANT, runway);
		return RunwayController.VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM;
	}

	@PreAuthorize("hasAuthority('airline')")
	@PostMapping(value = "/airports/{airportId}/runways/{runwayId}/edit")
	public String processUpdateForm(@Valid final Runway runway, final BindingResult result, @PathVariable("runwayId") final int runwayId, @PathVariable("airportId") final int airportId, final ModelMap model) {
		if (result.hasErrors()) {
			this.insertData(model);
			return RunwayController.VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM;

		} else if (this.runwayService.findRunwaysByName(runway.getName()).size() != 0 && runwayId != runway.getId().intValue()) {
			this.insertData(model);
			result.rejectValue("name", "NameIsAlreadyUsed", "Name is already used");
			return RunwayController.VIEWS_RUNWAYS_CREATE_OR_UPDATE_FORM;

		} else {
			Runway runwayToUpdate = this.runwayService.findRunwayById(runwayId);
			BeanUtils.copyProperties(runwayToUpdate, runway, "name", "runwayType");
			try {
				this.runwayService.saveRunway(runway);
			} catch (DataAccessException e) {
				e.printStackTrace();
			}
			return "redirect:/airports/{airportId}/runways";
		}
	}

	@PreAuthorize("hasAuthority('airline')")
	@GetMapping(value = "/airports/{airportId}/runways/{runwayId}/delete")
	public String deleteRunway(@PathVariable("runwayId") final int runwayId, @PathVariable("airportId") final int airportId) throws NonDeletableException {
		Runway runway = this.runwayService.findRunwayById(runwayId);
		boolean authorized = this.flightService.findFlights().stream().noneMatch(f -> f.getDepartes().equals(runway) || f.getLands().equals(runway));

		if (runway != null && authorized) {

			this.runwayService.deleteRunwayById(runway.getId());
		} else {
			throw new NonDeletableException();
		}
		return "redirect:/airports/{airportId}/runways";
	}

	@InitBinder("runway")
	public void initFlightBinder(final WebDataBinder dataBinder) {
		dataBinder.setValidator(new RunwayValidator());
	}

}
