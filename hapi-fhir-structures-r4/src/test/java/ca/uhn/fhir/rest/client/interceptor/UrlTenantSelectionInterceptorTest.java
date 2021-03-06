package ca.uhn.fhir.rest.client.interceptor;

import ca.uhn.fhir.rest.client.BaseGenericClientR4Test;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;

public class UrlTenantSelectionInterceptorTest extends BaseGenericClientR4Test {

	@Test
	public void testAddTenantToGet() throws Exception {
		ArgumentCaptor<HttpUriRequest> capt = prepareClientForSearchResponse();

		IGenericClient client = ourCtx.newRestfulGenericClient("http://example.com/fhir");
		client.registerInterceptor(new UrlTenantSelectionInterceptor("TENANT-A"));

		client
			.history()
			.onType(Patient.class)
			.returnBundle(Bundle.class)
			.execute();

		assertEquals("http://example.com/fhir/TENANT-A/Patient/_history", capt.getAllValues().get(0).getURI().toString());
	}

	@Test
	public void testAddTenantToGetAtRoot() throws Exception {
		ArgumentCaptor<HttpUriRequest> capt = prepareClientForSearchResponse();

		IGenericClient client = ourCtx.newRestfulGenericClient("http://example.com:8000");
		client.registerInterceptor(new UrlTenantSelectionInterceptor("TENANT-A"));

		client
			.history()
			.onType(Patient.class)
			.returnBundle(Bundle.class)
			.execute();

		assertEquals("http://example.com:8000/TENANT-A/Patient/_history", capt.getAllValues().get(0).getURI().toString());
	}

	@Test
	public void testAddTenantToGetMetadataAtRoot() throws Exception {
		ArgumentCaptor<HttpUriRequest> capt = prepareClientForCapabilityStatement();

		IGenericClient client = ourCtx.newRestfulGenericClient("http://example.com:8000/");
		client.registerInterceptor(new UrlTenantSelectionInterceptor("TENANT-A"));

		client
			.capabilities()
			.ofType(CapabilityStatement.class)
			.execute();

		assertEquals("http://example.com:8000/TENANT-A/metadata", capt.getAllValues().get(0).getURI().toString());
	}

	@Test
	public void testAddTenantToPost() throws Exception {
		ArgumentCaptor<HttpUriRequest> capt = prepareClientForCreateResponse();

		IGenericClient client = ourCtx.newRestfulGenericClient("http://example.com/fhir");
		client.registerInterceptor(new UrlTenantSelectionInterceptor("TENANT-A"));

		client
			.create()
			.resource(new Patient().setActive(true))
			.execute();

		assertEquals("http://example.com/fhir/TENANT-A/Patient", capt.getAllValues().get(0).getURI().toString());
	}

}
