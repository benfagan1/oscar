/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.tools.ant.util.DateUtils;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.managers.AppointmentManager;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.ScheduleManager;
import org.oscarehr.web.PatientListApptBean;
import org.oscarehr.web.PatientListApptItemBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/schedule")
@Component("scheduleService")
public class ScheduleService extends AbstractServiceImpl {

	@Autowired
	private ScheduleManager scheduleManager;
	@Autowired
	private AppointmentManager appointmentManager;
	@Autowired
	private DemographicManager demographicManager;
	
	@GET
	@Path("/{providerNo}/day/{date}")
	@Produces("application/json")
	public PatientListApptBean getAppointmentsForDay(@PathParam("providerNo") String providerNo, @PathParam("date") String date) {
		SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");
		
		PatientListApptBean response = new PatientListApptBean();
		
		try {
			Date dateObj = null;
			if("today".equals(date)) {
				dateObj = new Date(); 
			} else {
				dateObj = DateUtils.parseIso8601Date(date);
			}
			List<Appointment> appts = scheduleManager.getDayAppointments(providerNo, dateObj);
			for(Appointment appt:appts) {
				PatientListApptItemBean item = new PatientListApptItemBean();
				item.setDemographicNo(appt.getDemographicNo());
				item.setName(demographicManager.getDemographicFormattedName(appt.getDemographicNo()));
				item.setStartTime(timeFormatter.format(appt.getStartTime()));
				item.setReason(appt.getReason());
				item.setStatus(appt.getStatus());
				response.getPatients().add(item);
			}
		}catch(ParseException e) {
			throw new RuntimeException("Invalid Date sent, use yyyy-MM-dd format");
		}
		return response;
	}
	
}