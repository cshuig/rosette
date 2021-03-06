package se.leafcoders.rosette.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.leafcoders.rosette.model.event.Event;
import se.leafcoders.rosette.model.resource.Resource;
import se.leafcoders.rosette.service.EventService;

@Controller
public class EventController extends AbstractController {
	@Autowired
	private EventService eventService;
	@Autowired
	private MongoTemplate mongoTemplate;

	@RequestMapping(value = "events/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Event getEvent(@PathVariable String id) {
		return eventService.read(id);
	}

	@RequestMapping(value = "events", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Event> getEvents(
			@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date from, 
			@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date before, 
			HttpServletResponse response) {
		return eventService.readMany(from, before);
	}

	@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Event postEvent(@RequestBody Event event, HttpServletResponse response) {
		return eventService.create(event, response);
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEvent(@PathVariable String id, @RequestBody Event event, HttpServletResponse response) {
		eventService.update(id, event, response);
	}

	@RequestMapping(value = "events/{eventId}/resources/{resourceTypeId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void assignResource(@PathVariable String eventId, @PathVariable String resourceTypeId, @RequestBody Resource resource, HttpServletResponse response) {
		eventService.assignResource(eventId, resourceTypeId, resource, response);
	}

	@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
		eventService.delete(id, response);
	}
}
