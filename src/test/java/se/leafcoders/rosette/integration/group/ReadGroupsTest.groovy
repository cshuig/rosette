package se.leafcoders.rosette.integration.group

import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.leafcoders.rosette.integration.AbstractIntegrationTest;
import se.leafcoders.rosette.integration.util.TestUtil;

public class ReadGroupsTest extends AbstractIntegrationTest {

	@Test
	public void readGroupsWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["groups:read"])
		givenGroup(group1)
		givenGroup(group2)
		
		// When
		String getUrl = "/groups"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		String responseBody = thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """[
			{
				"id" : "${ group1.id }",
				"name" : "Admins",
				"description" : null
			},
			{
				"id" : "${ group2.id }",
				"name" : "Users",
				"description" : null
			}
		]"""
		thenResponseDataIs(responseBody, expectedData)
	}
}
