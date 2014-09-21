package se.ryttargardskyrkan.rosette.integration.groupMembership

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.auth.BasicScheme
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil
import se.ryttargardskyrkan.rosette.model.GroupMembership

import com.mongodb.util.JSON

public class CreateGroupMembershipTest extends AbstractIntegrationTest {

	@Test
	public void testSucces() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:groupMemberships", "read:*"])
		givenGroup(group1)
		
		// When
		postRequest = new HttpPost(baseUrl + "/groupMemberships")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"user" : { "idRef": "${user1.id}" },
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_CREATED)
		thenResponseHeaderHas(postResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(postResponse)
		String expectedData = """{
			"id" : "${ JSON.parse(responseBody)['id'] }",
			"user" : { "idRef": "${user1.id}", "referredObject": null },
			"group" : { "idRef": "${group1.id}", "referredObject": null }
		}"""
		thenResponseDataIs(responseBody, expectedData)
		postRequest.releaseConnection()
		thenDataInDatabaseIs(GroupMembership.class, "[${expectedData}]")
		thenItemsInDatabaseIs(GroupMembership.class, 1)
	}

	@Test
	public void testFailBecauseAlreadyExists() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["create:groupMemberships", "read:*"])
		givenGroup(group1)
		givenGroupMembership(user1, group1)

		// When
		postRequest = new HttpPost(baseUrl + "/groupMemberships")
		HttpResponse postResponse = whenPost(postRequest, user1, """{
			"user" : { "idRef": "${user1.id}" },
			"group" : { "idRef": "${group1.id}" }
		}""")

		// Then
		thenResponseCodeIs(postResponse, HttpServletResponse.SC_BAD_REQUEST)
		postRequest.releaseConnection()
		thenItemsInDatabaseIs(GroupMembership.class, 1)
	}
}
