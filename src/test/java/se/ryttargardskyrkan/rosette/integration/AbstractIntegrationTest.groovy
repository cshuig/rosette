package se.ryttargardskyrkan.rosette.integration

import org.apache.http.client.methods.HttpDelete
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.jackson.map.ObjectMapper
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.Mongo
import com.mongodb.MongoException

abstract class AbstractIntegrationTest {
	protected static MongoTemplate mongoTemplate
	protected static GridFsTemplate gridFsTemplate
	protected static DefaultHttpClient httpClient
	protected static ObjectMapper mapper
	
	protected static String baseUrl = "http://localhost:9000/api/v1-snapshot"
	
	@BeforeClass
	static void beforeClass() throws UnknownHostException, MongoException {
		// Clearing auth cache
		httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(baseUrl + "/authCaches")
		httpClient.execute(httpDelete)
		httpClient.getConnectionManager().shutdown()
		
		mongoTemplate = new MongoTemplate(new Mongo(), "rosette-test")
		gridFsTemplate = new GridFsTemplate(mongoTemplate.mongoDbFactory, mongoTemplate.converter)
		httpClient = new DefaultHttpClient()
		mapper = new ObjectMapper()		
	}
	
	@Before
	public void before() {
		mongoTemplate.dropCollection("users")
		mongoTemplate.dropCollection("groups")
		mongoTemplate.dropCollection("groupMemberships")
		mongoTemplate.dropCollection("events")
        mongoTemplate.dropCollection("eventTypes")
		mongoTemplate.dropCollection("themes")
		mongoTemplate.dropCollection("permissions")
        mongoTemplate.dropCollection("posters")
        mongoTemplate.dropCollection("userResourceTypes")
        mongoTemplate.dropCollection("locations")
        mongoTemplate.dropCollection("bookings")
        gridFsTemplate.delete(null)
	}
	
	@AfterClass
	static void afterClass() {
		gridFsTemplate = null
		mongoTemplate = null
		
		httpClient.getConnectionManager().shutdown()
		httpClient = null
		
		mapper = null		
	}
}
