package se.leafcoders.rosette.integration.location

import com.mongodb.util.JSON
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.junit.Test
import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil

import javax.servlet.http.HttpServletResponse

import static junit.framework.Assert.assertEquals

public class ReadLocationsTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["locations:read"])
		givenUploadFolder(uploadFolderLocations)
		givenLocation(location1)
		givenLocation(location2)

		// When
		String getUrl = "/locations"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ location1.id }",
				"name" : "Away",
				"description" : "Description...",
				"directionImage" : null
			},
			{
				"id" : "${ location2.id }",
				"name" : "Home",
				"description" : "Description...",
				"directionImage" : null
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
