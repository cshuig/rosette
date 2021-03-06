package se.leafcoders.rosette.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.leafcoders.rosette.comparator.PosterComparator;
import se.leafcoders.rosette.model.Poster;
import se.leafcoders.rosette.service.PosterService;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Controller
public class PosterController extends AbstractController {
	@Autowired
	private PosterService posterService;

	@RequestMapping(value = "posters/{id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Poster getPoster(@PathVariable String id) {
		return posterService.read(id);
	}

	@RequestMapping(value = "posters", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Poster> getPosters(@RequestParam(defaultValue = "false") boolean onlyActive, HttpServletResponse response) {
		Query query = new Query();
		if (onlyActive) {
			query.addCriteria(activeCriteria());
		}
		List<Poster> posters = posterService.readMany(query);
        Collections.sort(posters, new PosterComparator());
		return posters;
	}

	@RequestMapping(value = "posters", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Poster postPoster(@RequestBody Poster poster, HttpServletResponse response) {
		return posterService.create(poster, response);
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public void putPoster(@PathVariable String id, @RequestBody Poster poster, HttpServletResponse response) {
		posterService.update(id, poster, response);
	}

	@RequestMapping(value = "posters/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public void deletePoster(@PathVariable String id, HttpServletResponse response) {
		posterService.delete(id, response);
	}
	
	private Criteria activeCriteria() {
		final Calendar now = Calendar.getInstance();
		return Criteria.where("endTime").gt(now.getTime()).and("startTime").lt(now.getTime());
	}
}
