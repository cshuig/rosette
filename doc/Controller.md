# Controller

A controller handles requests and returns a response to the caller.

## CRUD

The controller shall handle Create, Read, Update and Delete requests. The request shall follow this conventions:

* `POST /events`
  * Creates a new event
  * Needs permission `events:create:2`, `events:create` or `create`
* `GET /events/2`
  * Gets event with id 2
  * Needs permission `events:read:2`, `events:read` or `read`
* `GET /events`
  * Gets all events
  * Needs permission `events:read` or `read`
* `PUT /events/2`
  * Updates event with id 2
  * Needs permission `events:update:2`, `events:update` or `update`
* `DELETE /events/2`
  * Delete event with id 2
  * Needs permission `events:delete:2`, `events:delete` or `delete`
* `DELETE /events`
  * Delete all events
  * Needs permission `events:delete` or `delete`

## Permission check

### Create one
```java
@RequestMapping(value = "events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
@ResponseBody
public Booking postEvent(@RequestBody Event event, HttpServletResponse response) {
  checkPermission("events:create");
  ...
}
```

### Get one
```java
@RequestMapping(value = "events/{id}", method = RequestMethod.GET, produces = "application/json")
@ResponseBody
public Booking getEvent(@PathVariable String id) {
  checkPermissions("events:create:" + id);
  ...
}
```

### Get all
```java
@RequestMapping(value = "events", method = RequestMethod.GET, produces = "application/json")
@ResponseBody
public List<Booking> getEvents(HttpServletResponse response) {
  ...
  for (Event event : eventsInDatabase) {
    if (isPermitted("events:read:" + event.getId())) {
      ...
    }
  }
  ...
}
```

### Update one
```java
@RequestMapping(value = "events/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
public void putEvent(@PathVariable String id, @RequestBody Event event, HttpServletResponse response) {
  checkPermission("events:update:" + id);
  ...
}
```

### Delete one
```java
@RequestMapping(value = "events/{id}", method = RequestMethod.DELETE, produces = "application/json")
public void deleteEvent(@PathVariable String id, HttpServletResponse response) {
  checkPermission("events:delete:" + id);
  ...
}
```

### Delete all
```java
@RequestMapping(value = "events", method = RequestMethod.DELETE, produces = "application/json")
public void deleteEvents(HttpServletResponse response) {
  checkPermission("events:delete");
  ...
}
```

## Dependencies

A model may have dependencies specified by an id. Eg. an `event` might have a `location` specified by `locationId`.
For some requests it might be convinient to include the data of a dependency. To add this functionallity the following
shall be implemented.

1. Add `locationData` to the `Event` model. `@Transient` tells the database to not store this attribute.
```java
@Transient
private Location locationData;
```

2. Add a static method `includeDependencies()` in `EventController`
```java
static public Event includeDependencies(final Event event, final MongoTemplate mongoTemplate) {
    if (event.getLocationId() != null {
      Location location = mongoTemplate.findById(event.getLocationId(), Location.class);
      if (location == null) {
        throw new NotFoundException();
      }
      event.setLocationData(location);
    }
    return event;
}
```

3. Use the above method in the `GET` requests
```java
public Booking getEvent(@PathVariable String id) {
    ...
    return includeDependencies(event, mongoTemplate);
}
```
