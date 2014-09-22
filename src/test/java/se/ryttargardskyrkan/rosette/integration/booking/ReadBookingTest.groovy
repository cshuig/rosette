package se.ryttargardskyrkan.rosette.integration.booking

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import se.ryttargardskyrkan.rosette.integration.AbstractIntegrationTest;
import se.ryttargardskyrkan.rosette.integration.util.TestUtil;

public class ReadBookingTest extends AbstractIntegrationTest {

	@Test
	public void readBookingWithSuccess() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:bookings:${ booking2.id }"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String getUrl = "/bookings/${ booking2.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_OK)
		thenResponseHeaderHas(getResponse, "Content-Type", "application/json;charset=UTF-8")

		String responseBody = TestUtil.jsonFromResponse(getResponse)
		String expectedData = """{
			"id" : "${ booking2.id }",
			"customerName" : "Arla",
			"startTime" : "2014-01-21 11:00 Europe/Stockholm",
			"endTime" : "2014-01-22 12:00 Europe/Stockholm",
			"location" : { "idRef" : null, "text" : "A location", "referredObject" : null }
		}"""
		thenResponseDataIs(responseBody, expectedData)
	}

	@Test
	public void failWhenReadBookingWithoutPermission() throws ClientProtocolException, IOException {
		// Given
		givenUser(user1)
		givenPermissionForUser(user1, ["read:bookings:${ booking1.id }"])
		givenLocation(location1)
		givenBooking(booking1)
		givenBooking(booking2)

		// When
		String getUrl = "/bookings/${ booking2.id }"
		HttpResponse getResponse = whenGet(getUrl, user1)

		// Then
		thenResponseCodeIs(getResponse, HttpServletResponse.SC_FORBIDDEN)
	}
}