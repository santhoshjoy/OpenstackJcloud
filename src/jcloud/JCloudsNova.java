package jcloud;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;

public class JCloudsNova implements Closeable {
	private final NovaApi novaApi;
	private final Set<String> regions;

	public static void main(String[] args) throws IOException {
		JCloudsNova jcloudsNova = new JCloudsNova();

		try {
			jcloudsNova.listServers();
			jcloudsNova.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jcloudsNova.close();
		}
	}

	public JCloudsNova() {
		Iterable<Module> modules = ImmutableSet
				.<Module> of(new SLF4JLoggingModule());

		String provider = "openstack-nova";
	    String identity = "tenantName:userName"; // tenantName:userName
	    String credential = "*****"; // demo account uses ADMIN_PASSWORD too

		novaApi = ContextBuilder.newBuilder(provider)
				.endpoint("http://xxx.xxx.xxx.xxx:5000/v2.0/")
				.credentials(identity, credential).modules(modules)
				.buildApi(NovaApi.class);
		regions = novaApi.getConfiguredZones();
	}

	private void listServers() {
		for (String region : regions) {
			ServerApi serverApi = novaApi.getServerApiForZone(region);

			System.out.println("Servers in " + region);

			for (Server server : serverApi.listInDetail().concat()) {
				System.out.println("  " + server);
			}
		}
	}

	public void close() throws IOException {
		Closeables.close((Closeable) novaApi, true);
	}
}