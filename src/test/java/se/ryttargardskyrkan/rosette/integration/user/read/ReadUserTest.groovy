package se.ryttargardskyrkan.rosette.integration.user.read

import static junit.framework.Assert.*

import javax.servlet.http.HttpServletResponse

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.shiro.authc.credential.DefaultPasswordService
import org.apache.shiro.authc.credential.PasswordService
import org.junit.Test

import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest
import se.ryttargardskyrkan.rosette.integration.util.TestUtil

import com.mongodb.util.JSON

public class ReadUserTest extends AbstractIntegrationTest {

	@Test
	public void test() throws ClientProtocolException, IOException {
		// Given
		String hashedPassword = new DefaultPasswordService().encryptPassword("password");
		mongoTemplate.getCollection("users").insert(JSON.parse("""
		[{
			"_id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"firstName" : "Lars",
			"lastName" : "Arvidsson",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		},{
			"_id" : "2",
			"username" : "nissehult",
			"hashedPassword" : "${hashedPassword}",
			"status" : "active"
		}]
		"""));

		// When
		HttpGet getRequest = new HttpGet(baseUrl + "/users/1")
		getRequest.setHeader("Accept", "application/json; charset=UTF-8")
		getRequest.setHeader("Content-Type", "application/json; charset=UTF-8")
		HttpResponse response = httpClient.execute(getRequest)

		// Then
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode())
		assertEquals("application/json;charset=UTF-8", response.getHeaders("Content-Type")[0].getValue())
		String expectedUser = """
		{
			"id" : "1",
			"username" : "lars.arvidsson@gmail.com",
			"firstName" : "Lars",
			"lastName" : "Arvidsson",
			"password" : null,
			"status" : "active"
		}
		"""
		TestUtil.assertJsonResponseEquals(expectedUser, response)
	}
}
