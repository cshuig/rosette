package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.model.EventType;
import se.leafcoders.rosette.service.EventTypeService;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class EventTypeController extends AbstractController {
	@Autowired
	private EventTypeService eventTypeService;

	@RequestMapping(value = "eventTypes/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public EventType getEventType(@PathVariable String id) {
		return eventTypeService.read(id);
	}

	@RequestMapping(value = "eventTypes", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EventType> getEventTypes() {
		return eventTypeService.readMany(new Query().with(new Sort(Sort.Direction.ASC, "name")));
	}

	@RequestMapping(value = "eventTypes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public EventType postEventType(@RequestBody EventType eventType, HttpServletResponse response) {
		return eventTypeService.create(eventType, response);
	}

	@RequestMapping(value = "eventTypes/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putEventType(@PathVariable String id, @RequestBody EventType eventType, HttpServletResponse response) {
		eventTypeService.update(id, eventType, response);
	}

	@RequestMapping(value = "eventTypes/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteGroup(@PathVariable String id, HttpServletResponse response) {
		eventTypeService.delete(id, response);
	}
}
