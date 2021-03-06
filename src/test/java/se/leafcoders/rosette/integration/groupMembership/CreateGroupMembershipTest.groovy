package se.leafcoders.rosette.integration.groupMembership

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.auth.BasicScheme
import org.junit.Test

import se.leafcoders.rosette.integration.AbstractIntegrationTest
import se.leafcoders.rosette.integration.util.TestUtil
import se.leafcoders.rosette.model.GroupMembership

import com.mongodb.util.JSON

public class CreateGroupMembershipTest extends AbstractIntegrationTest {

	@Test
	public void testSucces() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["groupMemberships:create", "*:read"])
		givenGroup(group1)
		
		// When
		String postUrl = "/groupMemberships"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"user" : ${ toJSON(userRef1) },
			"group" : ${ toJSON(group1) }
		}""")

		// Then
		String responseBody = thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"user" : ${ toJSON(userRef1) },
			"group" : ${ toJSON(group1) }
		}"""
		thenResponseDataIs(responseBody, expectedData)
		releasePostRequest()
		thenDataInDatabaseIs(GroupMembership.class, "[${expectedData}]")
		thenItemsInDatabaseIs(GroupMembership.class, 1)
	}

	@Test
	public void testFailBecauseAlreadyExists() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["groupMemberships:create", "*:read"])
		givenGroup(group1)
		givenGroupMembership(user1, group1)

		// When
		String postUrl = "/groupMemberships"
		HttpResponse postResponse = whenPost(postUrl, user1, """{
			"user" : ${ toJSON(userRef1) },
			"group" : ${ toJSON(group1) }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		releasePostRequest()
		thenItemsInDatabaseIs(GroupMembership.class, 1)
	}
}
